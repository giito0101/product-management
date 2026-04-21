package com.giitotech.product_management.integration.security;

import com.giitotech.product_management.dao.UserDao;
import com.giitotech.product_management.entity.Action;
import com.giitotech.product_management.entity.Log;
import com.giitotech.product_management.entity.Role;
import com.giitotech.product_management.entity.User;
import com.giitotech.product_management.testutil.repository.LogTestRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(
        scripts = "/testdata/integration/setup.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserDao userDao;

    @Autowired
    LogTestRepository logTestRepository;

    @Test
    @DisplayName("管理者ログイン認証成功テスト")
    public void adminLoginSuccessTest() throws Exception {
        // 設定
        String username = "tanaka";
        String password = "password1";

        // 実行
        MvcResult result = mockMvc.perform(post("/authenticateTheUser")
                        .param("username", username)
                        .param("password", password)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated())
                .andReturn();

        // 検証
        // セッション内のユーザー情報を確認
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        User sessionUser = (User) session.getAttribute("user");

        var roles = sessionUser.getRoles();
        assertEquals(3, roles.size());

        assertTrue(roles.stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN")));
        assertTrue(roles.stream().anyMatch(role -> role.getName().equals("ROLE_MANAGER")));
        assertTrue(roles.stream().anyMatch(role -> role.getName().equals("ROLE_EMPLOYEE")));
        assertNotNull(sessionUser);
        assertEquals(username, sessionUser.getUserName());

        // DBのユーザー情報と照合
        User dbUser = userDao.findByUserName(username);
        assertNotNull(dbUser);
        assertEquals(sessionUser.getUserName(), dbUser.getUserName());

        // ログ記録の確認
        Log latestLog = logTestRepository.findLatestLog();
        assertNotNull(latestLog);
        assertEquals(Action.LOGIN, latestLog.getAction());
        assertEquals(LocalDate.now(), latestLog.getTimestamp().toLocalDateTime().toLocalDate());
    }

    @Test
    @DisplayName("マネージャーログイン認証成功テスト")
    public void managerLoginSuccessTest() throws Exception {
        // 設定
        String username = "yamada";
        String password = "password2";

        // 実行・検証
        MvcResult result = mockMvc.perform(post("/authenticateTheUser")
                        .param("username", username)
                        .param("password", password)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated())
                .andReturn();

        // セッション内のユーザー情報とロールを確認
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        User sessionUser = (User) session.getAttribute("user");
        assertNotNull(sessionUser);
        assertEquals(username, sessionUser.getUserName());

        // ロールの検証
        var roles = sessionUser.getRoles();
        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(role -> role.getName().equals("ROLE_MANAGER")));
        assertTrue(roles.stream().anyMatch(role -> role.getName().equals("ROLE_EMPLOYEE")));

        // ログ記録の確認
        Log latestLog = logTestRepository.findLatestLog();
        assertNotNull(latestLog);
        assertEquals(Action.LOGIN, latestLog.getAction());
        assertEquals(LocalDate.now(), latestLog.getTimestamp().toLocalDateTime().toLocalDate());
    }

    @Test
    @DisplayName("一般社員ログイン認証成功テスト")
    public void employeeLoginSuccessTest() throws Exception {
        // 設定
        String username = "suzuki";
        String password = "password3";

        // 実行・検証
        MvcResult result = mockMvc.perform(post("/authenticateTheUser")
                        .param("username", username)
                        .param("password", password)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated())
                .andReturn();

        // セッション内のユーザー情報とロールを確認
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        User sessionUser = (User) session.getAttribute("user");
        assertNotNull(sessionUser);
        assertEquals(username, sessionUser.getUserName());

        // ロールの検証
        var roles = sessionUser.getRoles();
        assertEquals(1, roles.size());
        assertTrue(roles.stream().anyMatch(role -> role.getName().equals("ROLE_EMPLOYEE")));
        assertEquals("社員", new ArrayList<>(roles).get(0).getDisplayName());

        // ログ記録の確認
        Log latestLog = logTestRepository.findLatestLog();
        assertNotNull(latestLog);
        assertEquals(Action.LOGIN, latestLog.getAction());
        assertEquals(LocalDate.now(), latestLog.getTimestamp().toLocalDateTime().toLocalDate());
    }

    @Test
    @DisplayName("ログイン認証失敗テスト")
    public void loginFailureTest() throws Exception {
        // 設定
        String username = "test";
        String password = "test";

        // 実行・認証失敗の検証
        MvcResult result = mockMvc.perform(post("/authenticateTheUser")
                        .param("username", username)
                        .param("password", password)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/loginPage?error"))
                .andExpect(unauthenticated())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        assertNull(session.getAttribute("user"));

        // 認証されていない場合、ログインページへリダイレクトされることの確認
        mockMvc.perform(get("/")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/loginPage"));
    }

    @Test
    @DisplayName("認証済みユーザーのホーム画面表示テスト")
    @WithMockUser(username = "tanaka", roles = "EMPLOYEE")
    public void authenticatedHomePageTest() throws Exception {
        // 実行・検証
        MockHttpSession resultSession = (MockHttpSession) mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/home"))
                .andReturn()
                .getRequest()
                .getSession();

        // セッションユーザー情報の検証
        User sessionUser = (User) resultSession.getAttribute("user");
        assertEquals("tanaka", sessionUser.getUserName());
        String primaryRole = sessionUser.getRoles().stream()
                .min(Comparator.comparing(Role::getId))
                .map(Role::getDisplayName)
                .orElse("");
        assertEquals("管理者", primaryRole);
    }

    @Test
    @DisplayName("認証済みユーザー（マネージャー）のホーム画面表示テスト")
    @WithMockUser(username = "yamada", roles = "EMPLOYEE")
    public void authenticatedManagerHomePageTest() throws Exception {
        // 実行・検証
        MockHttpSession resultSession = (MockHttpSession) mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/home"))
                .andReturn()
                .getRequest()
                .getSession();

        // セッションユーザー情報の検証
        User sessionUser = (User) resultSession.getAttribute("user");
        assertEquals("yamada", sessionUser.getUserName());

        String roleDisplayName = sessionUser.getRoles().stream()
                .findFirst()
                .map(Role::getDisplayName)
                .orElse("");
        assertEquals("マネージャー", roleDisplayName);
    }

    @Test
    @DisplayName("認証済みユーザー（社員）のホーム画面表示テスト")
    @WithMockUser(username = "suzuki", roles = "EMPLOYEE")
    public void authenticatedEmployeeHomePageTest() throws Exception {
        // 実行・検証
        MockHttpSession resultSession = (MockHttpSession) mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/home"))
                .andReturn()
                .getRequest()
                .getSession();

        // セッションユーザー情報の検証
        User sessionUser = (User) resultSession.getAttribute("user");
        assertEquals("suzuki", sessionUser.getUserName());

        String roleDisplayName = sessionUser.getRoles().stream()
                .findFirst()
                .map(Role::getDisplayName)
                .orElse("");
        assertEquals("社員", roleDisplayName);
    }

    @Test
    @DisplayName("ログイン時のRemember-me機能のテスト")
    public void loginWithRememberMeTest() throws Exception {
        // 最初はログインリクエスト
        MvcResult loginResult = mockMvc.perform(post("/authenticateTheUser")
                        .param("username", "tanaka")
                        .param("password", "password1") // 本物のパスワード
                        .param("remember-me", "true") // ← remember-meつける
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection()) // ログイン成功後リダイレクト
                .andReturn();

        // ログイン成功後のRemember-Meクッキーを取得する
        Cookie rememberMeCookie = loginResult.getResponse().getCookie("remember-me");
        assertNotNull(rememberMeCookie, "Remember-meクッキーが発行されること");
    }

    @Test
    @DisplayName("Remember-me有効時にセッションタイムアウトしてもログインが維持される")
    public void rememberMeKeepsUserLoggedInAfterSessionTimeout() throws Exception {
        // 1. 最初ログインリクエスト
        MvcResult loginResult = mockMvc.perform(post("/authenticateTheUser")
                        .param("username", "tanaka")
                        .param("password", "password1")
                        .param("remember-me", "true")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie rememberMeCookie = loginResult.getResponse().getCookie("remember-me");
        assertNotNull(rememberMeCookie, "Remember-meクッキーが発行されること");

        // 2. セッション取得
        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(session, "最初のセッションがあること");
        session.setMaxInactiveInterval(1); // 1秒で切れる

        // 3. 最初に通常リクエストしておく
        mockMvc.perform(get("/")
                        .session(session)
                        .cookie(rememberMeCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/home"));

        // 4. セッションタイムアウトを待機
        Thread.sleep(1500); // 1秒ちょい待つ

        // 5. もう一回リクエスト（セッション切れ後）
        MvcResult afterTimeoutResult = mockMvc.perform(get("/")
                        .cookie(rememberMeCookie)) // セッションなしだけどremember-meクッキーあり
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/home"))
                .andReturn();

        // 6. 新しいセッションができていること
        MockHttpSession newSession = (MockHttpSession) afterTimeoutResult.getRequest().getSession(false);
        assertNotNull(newSession, "新しいセッションが作成されていること");
        assertNotEquals(session.getId(), newSession.getId(), "セッションIDが変わっていること");

        // 7. ユーザー情報が再設定されていること
        Object sessionUser = newSession.getAttribute("user"); // セッションに"ユーザー"が再格納される仕組みがある場合
        assertNotNull(sessionUser, "ユーザー情報がセッションに再設定されていること");
    }

    @Test
    @DisplayName("Remember-meクッキーの有効期限が設定されているかテスト")
    public void rememberMeCookieHasCorrectMaxAge() throws Exception {
        // ログインリクエスト（remember-me有効）
        MvcResult loginResult = mockMvc.perform(post("/authenticateTheUser")
                        .param("username", "tanaka")
                        .param("password", "password1")
                        .param("remember-me", "true")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andReturn();

        // Remember-meクッキーを取得
        Cookie rememberMeCookie = loginResult.getResponse().getCookie("remember-me");
        assertNotNull(rememberMeCookie, "Remember-meクッキーが存在すること");

        // MaxAgeが設定されていることを確認
        assertTrue(rememberMeCookie.getMaxAge() > 0, "Remember-meクッキーのMaxAgeが設定されていること");

        // tokenValiditySeconds=1209600秒に一致しているか
        assertEquals(1209600, rememberMeCookie.getMaxAge(), "Remember-meクッキーのMaxAgeが1209600秒になっていること");
    }

    @Test
    @DisplayName("Remember-me無効＆セッションタイムアウト後にログイン画面へリダイレクトされるテスト")
    public void sessionTimeoutWithoutRememberMeTest() throws Exception {
        // セッションタイムアウトを短く設定
        int sessionTimeout = 1; // 秒
        MockHttpSession session = new MockHttpSession();
        session.setMaxInactiveInterval(sessionTimeout);

        Role roleAdmin = new Role(1L, "ROLE_ADMIN", "管理者");
        Role roleManager = new Role(2L, "ROLE_MANAGER", "マネージャー");
        Role roleEmployee = new Role(3L, "ROLE_EMPLOYEE", "社員");

        // 設定
        Collection<Role> roles = new ArrayList<>(List.of(
                roleAdmin,
                roleManager,
                roleEmployee
        ));
        User user =  new User(1, "tanaka", "password", true, "太郎", "田中", "tanaka.tarou@example.com", roles);

        // セッションにユーザー情報を設定
        session.setAttribute("user", user);

        // 最初のリクエスト（remember-meは設定しない）
        MvcResult initialResult = mockMvc.perform(get("/")
                        .session(session)
                        .with(user("tanaka").roles("ADMIN", "MANAGER", "EMPLOYEE"))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/home")) // 最初はホームが表示される想定
                .andReturn();

        // Remember-meクッキーが発行されていないことを確認
        Cookie[] cookies = initialResult.getResponse().getCookies();
        Optional<Cookie> rememberMeCookie = Arrays.stream(cookies)
                .filter(c -> "remember-me".equals(c.getName()))
                .findFirst();
        assertFalse(rememberMeCookie.isPresent(), "Remember-meクッキーは発行されないこと");

        // セッションタイムアウトを待つ
        Thread.sleep(sessionTimeout * 1000 + 100);

        // セッション切れた後に再リクエスト
        mockMvc.perform(get("/")
                )
                .andExpect(status().is3xxRedirection()) // リダイレクトが起きる
                .andExpect(redirectedUrlPattern("**/loginPage")); // ログインページへリダイレクト
    }

    @Test
    @DisplayName("ログアウト処理とログ記録のテスト")
    public void logoutTest() throws Exception {
        // --- セットアップ ---
        int sessionTimeout = 1;
        MockHttpSession session = new MockHttpSession();
        session.setMaxInactiveInterval(sessionTimeout);

        Role roleAdmin = new Role(1L, "ROLE_ADMIN", "管理者");
        Role roleManager = new Role(2L, "ROLE_MANAGER", "マネージャー");
        Role roleEmployee = new Role(3L, "ROLE_EMPLOYEE", "社員");

        // 設定
        Collection<Role> roles = new ArrayList<>(List.of(
                roleAdmin,
                roleManager,
                roleEmployee
        ));
        User user =  new User(1, "tanaka", "password", true, "太郎", "田中", "tanaka.tarou@example.com", roles);

        // セッションに認証済みユーザーをセット
        session.setAttribute("user", user);

        // --- 実行 ---
        MvcResult result = mockMvc.perform(post("/logout")
                        .session(session)
                        .with(user("tanaka").roles("ADMIN", "MANAGER", "EMPLOYEE"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/loginPage?logout"))
                .andReturn();

        // --- 検証 ---

        // 1. セッションが無効になっている
        HttpSession invalidatedSession = result.getRequest().getSession(false);
        assertNull(invalidatedSession, "ログアウト後はセッションが無効になっていること");

        // 2. View名（リダイレクトURL）が正しい
        assertEquals("/loginPage?logout", result.getResponse().getRedirectedUrl(), "ログアウト後にログインページにリダイレクトされること");

        // 3. ログ記録がされている
        Log latestLog = logTestRepository.findLatestLog();
        assertNotNull(latestLog, "ログが記録されていること");
        assertEquals(Action.LOGOUT, latestLog.getAction(), "最新のログがLOGOUTであること");
        assertEquals(LocalDate.now(), latestLog.getTimestamp().toLocalDateTime().toLocalDate(), "ログの日付が今日であること");
    }

}