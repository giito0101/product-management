package com.giitotech.product_management.unit.dao;

import com.giitotech.product_management.dao.RoleRepository;
import com.giitotech.product_management.dao.UserDao;
import com.giitotech.product_management.dao.UserRepository;
import com.giitotech.product_management.entity.Role;
import com.giitotech.product_management.entity.User;
import com.giitotech.product_management.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/testdata/UserDaoUnitTestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserDaoTest {
    @Autowired
    UserDao userDao;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Qualifier("userTanaka")
    User userTanaka;

    @Autowired
    @Qualifier("userYamada")
    User userYamada;

    @BeforeEach
    void setUp() {
//        jdbc.execute("INSERT INTO \"user\" (username, password, enabled, first_name, last_name, email) VALUES\n" +
//                "('tanaka', '$2a$10$LfRa7F1ewbjv3SkAjzMja.1x/e43wqV685FiMkH72r2FYJROrnToW', 1, '太郎', '田中', 'tanaka.taro@example.com')");
    }

    //    Userが存在するときはUserNameでUserを見つけられるべき
    @Test
    void shouldFindUserWhenUserNameExists () {
        // 実行
        User actual = userDao.findByUserName("tanaka");

        // 検証
        assertEquals(1, actual.getId(), "UserIdが取得されるべきです");
        assertEquals("tanaka", actual.getUserName(), "UserNameが取得されるべきです");
        assertEquals("$2a$10$LfRa7F1ewbjv3SkAjzMja.1x/e43wqV685FiMkH72r2FYJROrnToW", actual.getPassword(), "Passwordが取得されるべきです");
        assertTrue(actual.isEnabled(), "Enableが取得されるべきです");
        assertEquals("太郎", actual.getFirstName(), "FirstNameが取得されるべきです");
        assertEquals("田中", actual.getLastName(), "LastNameが取得されるべきです");
        assertEquals("tanaka.taro@example.com", actual.getEmail(), "Emailが取得されるべきです");

    }

    // 新規と更新
    @Test
    void shouldInsertAndReturnUserIfUserDoesNotExist() {
        Optional<User> userOpt = userRepository.findById(2);
        assertFalse(userOpt.isPresent(), "Id2のuserは見つからないべき");

        // 設定
        Role roleManager = new Role(2L, "ROLE_MANAGER", "マネージャー");
        Role roleEmployee = new Role(3L, "ROLE_EMPLOYEE", "社員");

        Collection<Role> roles = new ArrayList<>(List.of(
                roleManager,
                roleEmployee
        ));

        User expextedUser = new User(0, "yamada", "password", true, "山田", "花子", "yamada.hanako@example.com", roles);
        User actual = userDao.save(expextedUser);
        expextedUser.setId(5);

        assertThat(actual).usingRecursiveComparison().ignoringFieldsMatchingRegexes(".*CGLIB.*").isEqualTo(expextedUser);
    }

    @Test
    void shouldUpdateAndReturnUserWhenUserExist () {
        Optional<User> userOpt = userRepository.findById(1);
        assertTrue(userOpt.isPresent(), "Id1のuserは見つかるべき");

        Role role = new Role(1L, "ROLE_ADMIN", "管理者");
        Role role2 = new Role(2L, "ROLE_MANAGER", "マネージャー");
        Role role3 = new Role(3L, "ROLE_EMPLOYEE", "社員");

        Collection<Role> roles = new ArrayList<>(List.of(
                role,
                role2,
                role3
        ));
        User expextedUser = new User(1, "suzuki", "test1234!", true, "一郎", "鈴木", "suzuki.itirou@example.com", roles);
        User actualUser = userDao.save(expextedUser);

        assertThat(actualUser).as("Userの中身が一致するべき").usingRecursiveComparison().ignoringFieldsMatchingRegexes(".*CGLIB.*").isEqualTo(expextedUser);
    }

}
