package com.giitotech.product_management.unit.service;

import com.giitotech.product_management.dto.UserForm;
import com.giitotech.product_management.entity.Role;
import com.giitotech.product_management.exception.RoleNotFoundException;
import com.giitotech.product_management.exception.UserFormNotFoundException;
import com.giitotech.product_management.service.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class RoleServiceTest {

    @Autowired
    @Qualifier("roles")
    List<Role> roles;

    @Autowired
    @Qualifier("userFormTanaka")
    UserForm userFormTanaka;

    @InjectMocks
    RoleServiceImpl roleService;

    @Mock
    private Logger logger = Logger.getLogger(getClass().getName());

    @Test
    void shouldSetRoleToUserFormWhenUserFormAndRolesExist() {
        UserForm expextedUserForm = new UserForm(null, "tanaka", "太郎", "田中", "ROLE_ADMIN", "管理者", "tanaka.tarou@example.com");

        userFormTanaka.setRoleName(null);

        roleService.setRoleToUserForm(userFormTanaka, roles);

        assertThat(userFormTanaka)
                .as("userFormTanakaが期待値と一致するべき")
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*CGLIB.*")
                .isEqualTo(expextedUserForm);

    }

    @Test
    void shouldThrowUserFormNotFoundExceptionWhenUserFormIsNull() {
        UserFormNotFoundException ex = assertThrows(UserFormNotFoundException.class,
                () ->roleService.setRoleToUserForm(null, roles),
                "UserFormNotExceptionが投げられるべき");

        assertEquals(ex.getMessage(), "userFormが見つかりません", "メッセージが一致するべき");
    }

    @Test
    void shouldThrowRoleNotFoundExceptionWhenRolesIsNull() {
        RoleNotFoundException ex = assertThrows(RoleNotFoundException.class,
                () ->roleService.setRoleToUserForm(userFormTanaka, null),
                "rolesが見つかりません");

        assertEquals(ex.getMessage(), "rolesが見つかりません", "メッセージが一致するべき");
    }

    @Test
    void shouldThrowRoleNotFoundExceptionWhenRolesIsEmpty() {

        RoleNotFoundException ex = assertThrows(RoleNotFoundException.class,
                () -> roleService.setRoleToUserForm(userFormTanaka, new ArrayList<>()),
                "RoleNotFOundExceptionが投げられるべき");

        assertEquals(ex.getMessage(), "roleが見つかりません", "roleが取得されるべき");

    }
}
