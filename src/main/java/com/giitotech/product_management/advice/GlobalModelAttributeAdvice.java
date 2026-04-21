package com.giitotech.product_management.advice;

import com.giitotech.product_management.dto.HeaderUserInfo;
import com.giitotech.product_management.entity.Role;
import com.giitotech.product_management.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Comparator;

@ControllerAdvice
public class GlobalModelAttributeAdvice {

    @ModelAttribute("headerUserInfo")
    public HeaderUserInfo setUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return null;

        // ID昇順で一番若いRoleを取得
        Role role = user.getRoles().stream()
                .sorted(Comparator.comparing(Role::getId))
                .findFirst()
                .orElse(null);

        return new HeaderUserInfo(user.getUserName(), role != null ? role.getDisplayName() : "");
    }
}