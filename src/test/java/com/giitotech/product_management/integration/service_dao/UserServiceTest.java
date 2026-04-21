package com.giitotech.product_management.integration.service_dao;

import com.giitotech.product_management.dto.UserRolesDto;
import com.giitotech.product_management.mapper.UserMapper;
import com.giitotech.product_management.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("/application-test.properties")
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Test
    void contextLoads() {
        assertThat(userMapper).isNotNull();
    }

    @Test
    @Sql("/testdata/UserServiceIntegrationTestData.sql")
    void shouldReturnUserRolesDtoPageWhenSearchesUserAndRole() {
        String sortBy = "userName";
        int page = 0;
        int size = 10;
        Sort sort;
        sort = Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        String[] keywords = {"社員"};
        String direction = "asc";

        Page<UserRolesDto> actualUserPage = userService.searchUsers(pageable, keywords, sortBy, direction);

        assertNotNull(actualUserPage, "actualUserPageをnullにするべきではない");

        // トータル件数
        assertEquals(13, actualUserPage.getTotalElements(), "トータル件数が一致するべき");
        // ページ内件数
        assertEquals(10, actualUserPage.getNumberOfElements(), "ページ内件数が一致するべき");
    }
}
