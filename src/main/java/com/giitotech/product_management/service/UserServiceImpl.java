package com.giitotech.product_management.service;


import com.giitotech.product_management.dao.RoleDao;
import com.giitotech.product_management.dao.UserDao;
import com.giitotech.product_management.dao.UserRepository;
import com.giitotech.product_management.dto.UserForm;
import com.giitotech.product_management.dto.UserRolesDto;
import com.giitotech.product_management.entity.Role;
import com.giitotech.product_management.entity.User;
import com.giitotech.product_management.exception.UserNotFoundException;
import com.giitotech.product_management.exception.ValidationException;
import com.giitotech.product_management.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

	private UserDao userDao;

	private RoleDao roleDao;

	private BCryptPasswordEncoder passwordEncoder;

	private UserRepository userRepository;

	private UserMapper userMapper;

	@Autowired
	public UserServiceImpl(UserDao userDao, RoleDao roleDao,
						   BCryptPasswordEncoder passwordEncoder,
						   UserRepository userRepository,
						   UserMapper userMapper) {
		this.userDao = userDao;
		this.roleDao = roleDao;
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if(username == null) {
			throw new UsernameNotFoundException("usernameを入力してください");
		}
		User user = userDao.findByUserName(username);

		if (user == null) {
			throw new UserNotFoundException("Invalid username or password.");
		}

		Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
				authorities);
	}

	private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

		for (Role tempRole : roles) {
			SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(tempRole.getName());
			authorities.add(tempAuthority);
		}

		return authorities;
	}

	@Override
	public User findByUserName(String userName) {
		// もしuserが存在していればデータベースをチェックする
		if(userName == null) {
            throw new UsernameNotFoundException("Please specify your username");
		}

		User user = userDao.findByUserName(userName);
		if(user == null) {
			throw new UserNotFoundException("An invalid username was entered");
		}

		return user;
	}

	@Override
	@Transactional
	public void deleteById(int id) {
		if (id == 0) {
			throw new UserNotFoundException("Please specify your id");
		}

		if(!userRepository.existsById(id)) {
			throw new UserNotFoundException("An invalid id was entered");
		}

		userRepository.deleteById(id);
	}

	public Page<UserRolesDto> searchUsers(Pageable pageable, String[] keywords, String sortBy, String direction) {
		List<String> keywordList = keywords == null ? List.of() :
				Arrays.stream(keywords)
					.filter(k -> k != null && !k.trim().isEmpty())
					.map(String::trim)
					.collect(Collectors.toList());

		List<UserRolesDto> users = userMapper.findUserByKeywords(
				keywordList, sortBy, direction,
				(int)pageable.getOffset(), pageable.getPageSize()
		);

		int total = userMapper.countUserByKeywords(keywordList);

		return new PageImpl<>(users, pageable, total);
	}

	public void validateForInsert(UserForm userForm) {
		// usernameとemailのどちらかがDBに存在した場合例外を出す
		List<FieldError> errors = new ArrayList<>();

		Optional<User> duplicatingUserName = userRepository.findByUserName(userForm.getUserName());
		Optional<User> duplicatingEmail = userRepository.findByEmail(userForm.getEmail());

		if (duplicatingUserName.isPresent()) {
			errors.add(new FieldError("userForm", "userName", "このユーザー名はすでに使われています"));
		}

		if (duplicatingEmail.isPresent()) {
			errors.add(new FieldError("userForm", "email", "このメールアドレスはすでに使われています"));
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors);
		}

	}

	@Override
	public void validateForUpdate(UserForm userForm) {
		List<FieldError> errors = new ArrayList<>();

		Optional<User> existing = userRepository.findById(userForm.getId().intValue());
		Optional<User> duplicatingUserName = userRepository.findByUserName(userForm.getUserName());
		Optional<User> duplicatingEmail = userRepository.findByEmail(userForm.getEmail());

		if (existing.isPresent() && !existing.get().getUserName().equals(userForm.getUserName()) && duplicatingUserName.isPresent()) {
			errors.add(new FieldError("userForm", "userName", "このユーザー名はすでに使われています"));
		}

		if (existing.isPresent() && !existing.get().getEmail().equals(userForm.getEmail()) && duplicatingEmail.isPresent()) {
			errors.add(new FieldError("userForm", "email", "このメールアドレスはすでに使われています"));
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors);
		}
	}

	@Override
	@Transactional
	public User save(UserForm userForm) {
		User user = convertUserFormToUser(userForm);

		// 保存するuserをデータベースで
		User saveUser = userDao.save(user);
		return saveUser;
	}

	private User convertUserFormToUser (UserForm userForm) {
		User user = new User();

		// userForm idがnullじゃなければuserにidをsetする
		if (userForm.getId() != null) {
			user.setId(userForm.getId().intValue());
		}

		// 編集画面で空文字を入力した場合以前のパスワードを使う
		if (userForm.getId() != null && userForm.getPassword().isEmpty()) {
			Optional<User> userOpt = userRepository.findById(userForm.getId().intValue());
			if(userOpt.isPresent()) {
				user.setPassword(userOpt.get().getPassword());
			} else {
				throw new UserNotFoundException("Userが見つかりません");
			}
		} else {
			user.setPassword(passwordEncoder.encode(userForm.getPassword()));
		}

		// userFormをuserオブジェクトへアサインする
		user.setUserName(userForm.getUserName());
		user.setEnabled(true);
		user.setFirstName(userForm.getFirstName());
		user.setLastName(userForm.getLastName());
		user.setEmail(userForm.getEmail());

		// 保存するロール一覧
		List<Role> roles = new ArrayList<>();

		roles.add(roleDao.findRoleByName(userForm.getRoleName()));
		user.setRoles(roles);

		// "管理者" の場合、マネージャーと社員も追加
		if ("ROLE_ADMIN".equals(userForm.getRoleName())) {
			user.addRole(roleDao.findRoleByName("ROLE_MANAGER"));
			user.addRole(roleDao.findRoleByName("ROLE_EMPLOYEE"));
		} else if ("ROLE_MANAGER".equals(userForm.getRoleName())) {
			user.addRole(roleDao.findRoleByName("ROLE_EMPLOYEE"));
		}

		return user;
	}
}
