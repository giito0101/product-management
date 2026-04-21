package com.giitotech.product_management.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.giitotech.product_management.exception.RoleNotFoundException;
import com.giitotech.product_management.exception.UserFormNotFoundException;
import org.springframework.stereotype.Service;

import com.giitotech.product_management.dao.RoleRepository;
import com.giitotech.product_management.dto.UserForm;
import com.giitotech.product_management.entity.Role;

@Service
public class RoleServiceImpl implements RoleService {

	private RoleRepository roleRepository;

	private Logger logger = Logger.getLogger(getClass().getName());

	public RoleServiceImpl(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	@Override
	public void setRoleToUserForm(UserForm userForm, List<Role> roles) {
		if(userForm == null) {
			logger.warning("userFormが見つかりません");
			throw new UserFormNotFoundException("userFormが見つかりません");
		}

		if(roles == null) {
			logger.warning("rolesが見つかりません userName:" + (userForm.getUserName() != null ? userForm.getUserName() : "null"));
			throw new RoleNotFoundException("rolesが見つかりません");
		}

		Optional<Role> roleOpt = roles.stream()
				.filter(role -> role.getDisplayName() != null
						&& role.getDisplayName().equals(userForm.getRoleDisplayName()))
				.findFirst();

        if(roleOpt.isPresent()) {
			userForm.setRoleName(roleOpt.get().getName());
		} else {
			throw new RoleNotFoundException("roleが見つかりません");
		}

	}
}
