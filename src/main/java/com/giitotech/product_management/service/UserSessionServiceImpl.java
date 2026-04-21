package com.giitotech.product_management.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import com.giitotech.product_management.dao.UserRepository;
import com.giitotech.product_management.exception.RoleNotFoundException;
import com.giitotech.product_management.exception.UserFormNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.giitotech.product_management.dto.UserForm;
import com.giitotech.product_management.entity.Role;
import com.giitotech.product_management.entity.User;
import com.giitotech.product_management.exception.UserNotFoundException;

import jakarta.servlet.http.HttpSession;

@Service
public class UserSessionServiceImpl implements UserSessionService {
    private UserService userService;
    private UserRepository userRepository;

    @Autowired
    public UserSessionServiceImpl(
            UserService theUserService,
            UserRepository theUserRepository) {
        this.userService = theUserService;
        this.userRepository = theUserRepository;

    }

    public UserForm getUserForm(HttpSession session) {
        // セッションから productForm を取得
        UserForm userForm = (UserForm) session.getAttribute("registerUserForm");
        if (userForm == null) {
            userForm = new UserForm();
        }
        return userForm;
    }

    public UserForm getUserForm(HttpSession session, int userId) {
        UserForm userForm = null;
        UserForm editUserForm = null;
        UserForm registerUserForm = null;
        Optional<User> userOpt = Optional.empty();
        User user = null;

        // ブラウザの戻るボタンやキャンセルボタンを押した時のページ遷移
        registerUserForm = (UserForm) session.getAttribute("registerUserForm");
        editUserForm = (UserForm) session.getAttribute("editUserForm");

        if (registerUserForm != null) {
            return registerUserForm;


        } else if (editUserForm != null) {
            return editUserForm;
        }

        // 一覧ページから編集画面を表示した後のページ遷移
        userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            throw new UserNotFoundException("User not found with userId: " + String.valueOf(userId));
        }

        // UserForm用のRoleを取得
        Collection<Role> roles = user.getRoles();

        if (roles == null) {
            throw new RoleNotFoundException("Role not found with userId: " + String.valueOf(userId));
        }

        // idでソート
        Role minRole = roles.stream()
                .min(Comparator.comparing(role -> role.getId()))
                .orElse(null); // Roleが空の場合nullを返す

        if (minRole == null) {
            throw new RoleNotFoundException("Role not found with userId: " + String.valueOf(userId));
        }

        String roleName = minRole.getName();
        String roleDisplayName = minRole.getDisplayName();

        userForm = new UserForm(
                (long) user.getId(),
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                roleName,
                roleDisplayName,
                user.getEmail());

        return userForm;
    }
}
