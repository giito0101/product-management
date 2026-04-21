package com.giitotech.product_management.testutil;

import com.giitotech.product_management.dto.UserForm;
import com.giitotech.product_management.dto.UserRolesDto;
import com.giitotech.product_management.entity.Role;
import com.giitotech.product_management.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class TestUserFactory {

    @Bean
    @Scope(value = "prototype")
    @Qualifier("userTanaka")
    public User createUserWithTanaka() {
        Role roleAdmin = new Role(1L, "ROLE_ADMIN", "管理者");
        Role roleManager = new Role(2L, "ROLE_MANAGER", "マネージャー");
        Role roleEmployee = new Role(3L, "ROLE_EMPLOYEE", "社員");

        // 設定
        Collection<Role> roles = new ArrayList<>(List.of(
                roleAdmin,
                roleManager,
                roleEmployee
        ));
        return new User(1, "tanaka", "password", true, "太郎", "田中", "tanaka.tarou@example.com", roles);
    }

    @Bean
    @Scope
    @Qualifier("userYamada")
    public User createUserWithYamada() {
        Role roleManager = new Role(2L, "ROLE_MANAGER", "マネージャー");
        Role roleEmployee = new Role(3L, "ROLE_EMPLOYEE", "社員");

        // 設定
        Collection<Role> roles = new ArrayList<>(List.of(
                roleManager,
                roleEmployee
        ));
        return new User(2, "yamada", "password", true, "山田", "花子", "yamada.hanako@example.com", roles);
    }

    @Bean
    @Scope(value = "prototype")
    @Qualifier("userFormTanaka")
    public UserForm createUserFormWithTanaka() {
        return new UserForm(null, "tanaka", "太郎", "田中", "ROLE_ADMIN", "管理者", "tanaka.tarou@example.com");
    }

    @Bean
    @Scope(value = "prototype")
    @Qualifier("userFormYamada")
    public UserForm createUserFormWithYamada() {
        return new UserForm(2L, "yamada", "花子", "山田", "ROLE_MANAGER", "マネージャー", "yamada.hanako@example.com");
    }

    @Bean
    @Scope(value = "prototype")
    @Qualifier("roles")
    public List<Role> createRoles() {
        return List.of(new Role(1L, "ROLE_ADMIN", "管理者"),
                new Role(2L, "ROLE_MANAGER", "マネージャー"),
                new Role(3L, "ROLE_EMPLOYEE", "社員"));
    }

    @Bean
    @Scope(value = "prototype")
    @Qualifier("userRolesTanaka")
    public UserRolesDto createUserRolesWithTanaka() {
        return new UserRolesDto(1L, "tanaka", "管理者, マネージャー, 社員", "tanaka.tarou@example.com");
    }

    @Bean
    @Scope(value = "prototype")
    @Qualifier("userRolesYamada")
    public UserRolesDto createUserRolesWithYamada() {
        return new UserRolesDto(2L, "yamada", "マネージャー, 社員", "yamada.hanako@example.com");
    }
}
