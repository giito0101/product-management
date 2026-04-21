package com.giitotech.product_management.unit.service;

import com.giitotech.product_management.dao.UserRepository;
import com.giitotech.product_management.dto.UserForm;
import com.giitotech.product_management.entity.User;
import com.giitotech.product_management.exception.RoleNotFoundException;
import com.giitotech.product_management.exception.UserNotFoundException;
import com.giitotech.product_management.service.UserSessionServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserSessionServiceTest {
    @Mock
    private HttpSession session;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSessionServiceImpl userSessionService;

    @Autowired
    @Qualifier("userFormTanaka")
    private UserForm userFormTanaka;

    @Autowired
    @Qualifier("userTanaka")
    User userTanaka;

    @Test
    void shouldReturnEmptyUserFormWhenSessionIsNull() {
        when((UserForm) session.getAttribute("registerUserForm")).thenReturn(null);

        UserForm userForm = userSessionService.getUserForm(session);

        UserForm expectedUserForm = new UserForm();

        assertThat(userForm)
                .as("空のUserインスタンスを取得するべき")
                .usingRecursiveComparison()
                .isEqualTo(expectedUserForm);
    }

    @Test
    void shouldReturnUserFormFromSessionWhenExists() {
        when((UserForm) session.getAttribute("registerUserForm")).thenReturn(userFormTanaka);

        UserForm userForm = userSessionService.getUserForm(session);

        assertSame(userFormTanaka, userForm);
    }

    @Test
    void shouldReturnRegisterUserFormFromSessionWhenEditUserFormIsNull() {
        when((UserForm) session.getAttribute("registerUserForm")).thenReturn(userFormTanaka);
        when((UserForm) session.getAttribute("editUserForm")).thenReturn(null);

        UserForm userForm = userSessionService.getUserForm(session, 0);

        assertSame(userFormTanaka, userForm, "同じUserFormが取得されるべき");
    }

    @Test
    void shouldReturnEditUserFormFromSessionWhenRegisterUserFormIsNull() {
        when((UserForm) session.getAttribute("editUserForm")).thenReturn(userFormTanaka);
        when((UserForm) session.getAttribute("registerUserForm")).thenReturn(null);

        UserForm userForm = userSessionService.getUserForm(session, 0);

        assertSame(userFormTanaka, userForm, "同じUserFormが取得されるべき");
    }

    @Test
    void throwsUserNotFoundExceptionIfBothRegisterAndEditUserFormsAreNull() {
        when((UserForm) session.getAttribute("editUserForm")).thenReturn(null);
        when((UserForm) session.getAttribute("registerUserForm")).thenReturn(null);
        Optional<User> userOpt = Optional.empty();
        when(userRepository.findById(0)).thenReturn(userOpt);


        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
        () -> userSessionService.getUserForm(session, 0),
                "UserNotFoundExceptionが投げられるべきです");

        assertEquals(ex.getMessage(), "User not found with userId: 0", "メッセージが一致するべき");
    }

    @Test
     void shouldReturnUserFormWhenUserOptExists () {
        Optional<User> userTanakaOpt = Optional.of(userTanaka);
        when(userRepository.findById(1)).thenReturn(userTanakaOpt);

        UserForm userForm = userSessionService.getUserForm(session, 1);

        UserForm expextedUserForm =  new UserForm(1L, "tanaka", "太郎", "田中", "ROLE_ADMIN", "管理者", "tanaka.tarou@example.com");

        assertThat(userForm)
                .as("userFormが取得されるべき")
                .usingRecursiveComparison()
                .isEqualTo(expextedUserForm);
     }

    @Test
     void throwUserNotFoundExceptionWhenUserOptIsEmpty () {
         Optional<User> userOpt = Optional.empty();
         when(userRepository.findById(99)).thenReturn(userOpt);

         UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                 () -> userSessionService.getUserForm(session, 99),
                 "UserNotFoundExceptionが投げられるべき");

         assertEquals(ex.getMessage(), "User not found with userId: 99");

     }

    @Test
    void shouldThrowRoleNotFoundExceptionIfUserHasNoMatchingRole () {
        userTanaka.setRoles(null);
        Optional<User> userTanakaOpt = Optional.of(userTanaka);

        when(userRepository.findById(1)).thenReturn(userTanakaOpt);

        RoleNotFoundException ex = assertThrows(RoleNotFoundException.class, () -> userSessionService.getUserForm(session, 1), "RoleNotFoundExceptionが投げられるべき");
        assertEquals(ex.getMessage(), "Role not found with userId: 1", "メッセージが表示されるべき");
    }

    @Test
    void shouldThrowRoleNotFoundExceptionIfUserHasEmptyRole () {
        userTanaka.setRoles(List.of());
        Optional<User> userTanakaOpt = Optional.of(userTanaka);

        when(userRepository.findById(1)).thenReturn(userTanakaOpt);

        RoleNotFoundException ex = assertThrows(RoleNotFoundException.class, () -> userSessionService.getUserForm(session, 1), "RoleNotFoundExceptionが投げられるべき");
        assertEquals(ex.getMessage(), "Role not found with userId: 1", "メッセージが表示されるべき");
    }
}
