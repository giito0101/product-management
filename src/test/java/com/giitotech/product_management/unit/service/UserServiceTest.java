package com.giitotech.product_management.unit.service;

import com.giitotech.product_management.dao.RoleDao;
import com.giitotech.product_management.dao.UserDao;
import com.giitotech.product_management.dao.UserRepository;
import com.giitotech.product_management.dto.UserForm;
import com.giitotech.product_management.dto.UserRolesDto;
import com.giitotech.product_management.entity.Role;
import com.giitotech.product_management.entity.User;
import com.giitotech.product_management.exception.UserNotFoundException;
import com.giitotech.product_management.mapper.UserMapper;
import com.giitotech.product_management.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao mockUserDao;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private User mockUser;

    @Mock
    private BCryptPasswordEncoder mockPasswordEncoder;

    @Mock
    private RoleDao mockRoleDao;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    private User user2;

    private User user3;

    private Role role;

    private Role role2;

    private Role role3;

    private UserForm userForm;

    private UserForm userForm2;

    private UserForm userForm3;

    private UserForm userForm4;

    private UserRolesDto userRolesDto;

    private UserRolesDto userRolesDto2;

    @BeforeEach
    void setUp() {
        role = new Role(1L, "ROLE_ADMIN", "管理者");
        role2 = new Role(2L, "ROLE_MANAGER", "マネージャー");
        role3 = new Role(3L, "ROLE_EMPLOYEE", "社員");
        // 設定
        Collection<Role> roles = new ArrayList<>(List.of(
                role,
                role2,
                role3
        ));
        user = new User(1, "tanaka", "password", true, "太郎", "田中", "tanaka.tarou@example.com", roles);

        Collection<Role> roles2 = new ArrayList<>(List.of(
                role2,
                role3
        ));
        user2 = new User(2, "yamada", "password", true, "山田", "花子", "yamada.hanako@example.com", roles2);

        Collection<Role> roles3 = new ArrayList<>(List.of(
                role3
        ));

        user3 = new User(3, "suzuki", "password", true, "鈴木", "一郎", "suzuki.itirou@example.com", roles3);

        userForm = new UserForm(null, "tanaka", "太郎", "田中", "ROLE_ADMIN", "管理者", "tanaka.tarou@example.com");

        userForm2 = new UserForm(2L, "yamada", "花子", "山田", "ROLE_MANAGER", "マネージャー", "yamada.hanako@example.com");

        userForm3 = new UserForm(null, "suzuki", "一郎", "鈴木", "ROLE_EMPLOYEE", "社員", "suzuki.itirou@example.com");

        userForm4 = new UserForm(3L, "suzuki", "一郎", "鈴木", "ROLE_EMPLOYEE", "社員", "suzuki.itirou@example.com");

        userRolesDto = new UserRolesDto(1L, "tanaka", "管理者, マネージャー, 社員", "tanaka.tarou@example.com");

        userRolesDto2 = new UserRolesDto(3L, "suzuki", "社員", "suzuki.itirou@example.com");
    }

    @Test
    void shouldLoadUserReturnUserDetailsWhenUserExists() {

        // 設定
        when(mockUserDao.findByUserName("tanaka")).thenReturn(user);

        // テスト実行
        UserDetails userDetails = userService.loadUserByUsername("tanaka");

        // 検証
        assertNotNull(userDetails, "Userが存在する場合、UserDetailsが返されるべき");
    }

    @Test
    void shouldThrowsUsernameNotFoundExceptionWhenUsernameDoesNotExist() {

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                ()->userService.loadUserByUsername(null),
                "UsernameNotFoundExceptionが投げられるべき");

        assertEquals(ex.getMessage(),"usernameを入力してください", "メッセージが一致するべき");
    }

    @Test
    void shouldLoadUserThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        when(mockUserDao.findByUserName("sundy")).thenReturn(null);

        // 正しい例外が投げられることの確認
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.loadUserByUsername("sundy"),
                "UserNotFoundExceptionが投げられるべき");

        assertEquals("Invalid username or password.", exception.getMessage());
    }

    @Test
    void shouldFindByUserNameReturnUserWhenUserExists() {
        when(mockUserDao.findByUserName("tanaka")).thenReturn(user);

        User expexteUser = userService.findByUserName("tanaka");

        assertEquals(user, expexteUser, "存在するUserを入力するべき");
    }

    @Test
    void shouldFindByUserNameReturnUserNotFoundExceptionWhenUserDoNotExists() {

        UsernameNotFoundException nullException = assertThrows(UsernameNotFoundException.class, () -> userService.findByUserName(null), "存在しないUserを入力するべき");
        assertEquals("Please specify your username", nullException.getMessage(), "userNameを入力すべきでない");


        when(mockUserDao.findByUserName("sundy")).thenReturn(null);
        UserNotFoundException doNotUserException = assertThrows(UserNotFoundException.class, () -> userService.findByUserName("sundy"), "存在しないUserを入力するべき");
        assertEquals("An invalid username was entered", doNotUserException.getMessage(), "userNameを入力すべきでない");

    }

    @Test
    void shouldDeleteUserWhenIdExists() {

        // 設定
        when(mockUserRepository.existsById(1)).thenReturn(true);

        // 実行
        userService.deleteById(1);

        // 検証
        verify(mockUserRepository).deleteById(1);

    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenIdDoesNotExist() {
        // 実行
        UserNotFoundException nullException = assertThrows(UserNotFoundException.class, () -> userService.deleteById(0), "正しい例外を投げるべき");
        //検証
        assertEquals("Please specify your id", nullException.getMessage(), "正しいメッセージを指定するべき");

        // 設定
        when(mockUserRepository.existsById(99)).thenReturn(false);
        // 実行
        UserNotFoundException doesNotExistException = assertThrows(UserNotFoundException.class, () -> userService.deleteById(99), "正しい例外を投げるべき");
        // 検証
        assertEquals("An invalid id was entered", doesNotExistException.getMessage(), "正しいメッセージを指定するべき");
    }

    @Test
    void shouldReturnUserRolesDtoPageWhenKeywordsMaches() {
        // 準備
        // pageableの設定
        String sortBy = "userName";
        String direction = "asc";
        int page = 0;
        int size = 10;
        Sort sort;
        sort = Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // keywordsの設定
        String[] keywords = {"tanaka", "suzuki"};

        // Page<User> の設定 Userの中にRoleが入っていることも必要
        long totalElements = 2;

        List<UserRolesDto> expextedUserList = new ArrayList<>(Arrays.asList(userRolesDto, userRolesDto2));

        List<String> keywordList = keywords == null ? List.of() :
                Arrays.stream(keywords)
                        .filter(k -> k != null && !k.trim().isEmpty())
                        .map(String::trim)
                        .collect(Collectors.toList());

        // モック設定
        when(userMapper.findUserByKeywords(keywordList, sortBy, direction,
                (int)pageable.getOffset(), pageable.getPageSize()
        )).thenReturn(expextedUserList);

        when(userMapper.countUserByKeywords(keywordList)).thenReturn(2);

        // 検証
        // Page<UserRoleDto>の中身と件数の確認

        Page<UserRolesDto> actualUserPage = userService.searchUsers(pageable, keywords, sortBy, direction);
        assertEquals(totalElements, actualUserPage.getTotalElements(), "Page<UserRolesDto>のトータルが取得できるべき");
        assertEquals(page, actualUserPage.getNumber(), "pageを正しく設定すべき");

        // orderやdirectionの比較
        assertEquals(sort, actualUserPage.getSort(), "sortを正しく設定すべき");
        Sort.Order order = actualUserPage.getSort().getOrderFor("userName");
        assertNotNull(order, "ソート対象 'username' の順序が存在するべきこと");
        assertEquals(Sort.Direction.ASC, order.getDirection(), "'userName'のソートは昇順であるべき");

        List<UserRolesDto> actualUserList = actualUserPage.getContent();
        assertIterableEquals(expextedUserList, actualUserList, "全てのuserRolesDtoが一致するべき");
    }

    // update用とinsert用のデータを用意
    // それぞれの変換フィールドの動作チェック
    // RoleName別にuser.addが動いていること
    // ROLE_ADMIN, ROLE_MANAGER, ROLE_EMPLOYEEでそれぞれチェック
    // updateとinsertで2パターン✕Role3パターンの6パターンチェック

    // insert admin
    @Test
    void shouldReturnUserWithAllRolesWhenAdminInserted() {
        //設定
        List<Role> roles = new ArrayList<>(List.of(role, role2, role3));

        // モック
        when(mockPasswordEncoder.encode(userForm.getPassword())).thenReturn("password");
        when(mockRoleDao.findRoleByName(userForm.getRoleName())).thenReturn(role);
        when(mockRoleDao.findRoleByName("ROLE_MANAGER")).thenReturn(role2);
        when(mockRoleDao.findRoleByName("ROLE_EMPLOYEE")).thenReturn(role3);

        // 実行
        userService.save(userForm);

        // 検証
        // mockUserDao.save(user)のuser中身を確認
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserDao).save(userCaptor.capture());
        User saveUser = userCaptor.getValue();

        assertEquals(0, saveUser.getId());
        assertEquals("tanaka", saveUser.getUserName());
        assertEquals("password", saveUser.getPassword());
        assertTrue(saveUser.isEnabled());
        assertEquals("太郎", saveUser.getFirstName());
        assertEquals("田中", saveUser.getLastName());
        assertEquals("tanaka.tarou@example.com", saveUser.getEmail());
        assertEquals(roles, saveUser.getRoles());
    }

    // insert employee
    @Test
    void shouldReturnUserWithEmployeeWhenInserted() {
        //設定
        List<Role> roles = new ArrayList<>(List.of(role3));

        // モック
        when(mockPasswordEncoder.encode(userForm3.getPassword())).thenReturn("password");
        when(mockRoleDao.findRoleByName(userForm3.getRoleName())).thenReturn(role3);

        // 実行
        userService.save(userForm3);

        // 検証
        // mockUserDao.save(user)のuser中身を確認
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserDao).save(userCaptor.capture());
        User saveUser = userCaptor.getValue();

        assertEquals(0, saveUser.getId());
        assertEquals("suzuki", saveUser.getUserName());
        assertEquals("password", saveUser.getPassword());
        assertTrue(saveUser.isEnabled());
        assertEquals("一郎", saveUser.getFirstName());
        assertEquals("鈴木", saveUser.getLastName());
        assertEquals("suzuki.itirou@example.com", saveUser.getEmail());
        assertEquals(roles, saveUser.getRoles());
    }

    // update manager
    @Test
    void shouldReturnUserWithManagerAndEmployeeWhenManagerUpdated() {
        //設定
        List<Role> roles = new ArrayList<>(List.of(role2, role3));
        userForm2.setPassword("password");

        // モック
        when(mockPasswordEncoder.encode(userForm2.getPassword())).thenReturn("password");
        when(mockRoleDao.findRoleByName(userForm2.getRoleName())).thenReturn(role2);
        when(mockRoleDao.findRoleByName("ROLE_EMPLOYEE")).thenReturn(role3);

        // 実行
        userService.save(userForm2);

        // 検証
        // mockUserDao.save(user)のuser中身を確認
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserDao).save(userCaptor.capture());
        User saveUser = userCaptor.getValue();

        assertEquals(2, saveUser.getId());
        assertEquals("yamada", saveUser.getUserName());
        assertEquals("password", saveUser.getPassword());
        assertTrue(saveUser.isEnabled());
        assertEquals("花子", saveUser.getFirstName());
        assertEquals("山田", saveUser.getLastName());
        assertEquals("yamada.hanako@example.com", saveUser.getEmail());
        assertEquals(roles, saveUser.getRoles());
    }

    // update manager password=""
    @Test
    void shouldReturnUserWhenPasswordIsEmptyOnUpdated() {
        //設定
        List<Role> roles = new ArrayList<>(List.of(role2, role3));
        userForm2.setPassword("");
        Optional<User> userOpt = Optional.of(user2);

        // モック
        when(mockUserRepository.findById(userForm2.getId().intValue())).thenReturn(userOpt);
        when(mockRoleDao.findRoleByName(userForm2.getRoleName())).thenReturn(role2);
        when(mockRoleDao.findRoleByName("ROLE_EMPLOYEE")).thenReturn(role3);

        // 実行
        userService.save(userForm2);

        // 検証
        // mockUserDao.save(user)のuser中身を確認
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserDao).save(userCaptor.capture());
        User saveUser = userCaptor.getValue();

        assertEquals(2, saveUser.getId());
        assertEquals("yamada", saveUser.getUserName());
        assertEquals("password", saveUser.getPassword());
        assertTrue(saveUser.isEnabled());
        assertEquals("花子", saveUser.getFirstName());
        assertEquals("山田", saveUser.getLastName());
        assertEquals("yamada.hanako@example.com", saveUser.getEmail());
        assertEquals(roles, saveUser.getRoles());
    }

    // update employee
    @Test
    void shouldReturnUserWithEmployeeWhenUpdated() {
        //設定
        List<Role> roles = new ArrayList<>(List.of(role3));
        userForm4.setPassword("password");

        // モック
        when(mockPasswordEncoder.encode(userForm4.getPassword())).thenReturn("password");
        when(mockRoleDao.findRoleByName(userForm4.getRoleName())).thenReturn(role3);

        // 実行
        userService.save(userForm4);

        // 検証
        // mockUserDao.save(user)のuser中身を確認
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserDao).save(userCaptor.capture());
        User saveUser = userCaptor.getValue();

        assertEquals(3, saveUser.getId());
        assertEquals("suzuki", saveUser.getUserName());
        assertEquals("password", saveUser.getPassword());
        assertTrue(saveUser.isEnabled());
        assertEquals("一郎", saveUser.getFirstName());
        assertEquals("鈴木", saveUser.getLastName());
        assertEquals("suzuki.itirou@example.com", saveUser.getEmail());
        assertEquals(roles, saveUser.getRoles());
    }
}
