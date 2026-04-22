package com.giitotech.product_management.integration.controller;

import com.giitotech.product_management.dao.UserDao;
import com.giitotech.product_management.dao.UserRepository;
import com.giitotech.product_management.dto.UserForm;
import com.giitotech.product_management.dto.UserRolesDto;
import com.giitotech.product_management.entity.Role;
import com.giitotech.product_management.exception.RoleNotFoundException;
import com.giitotech.product_management.exception.SessionNotFoundException;
import com.giitotech.product_management.exception.UserFormNotFoundException;
import com.giitotech.product_management.service.*;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@Sql(
        scripts = "/testdata/integration/setup.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class UserControllerTest {

    private static MockHttpServletRequest request;

    @Mock
    KeywordService keywordService;

    @Mock
    PagingService pagingService;

    @Mock
    UserService userService;

    @MockitoSpyBean
    RoleService spyRoleService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RoleService roleService;

    @Autowired
    UserDao userDao;

    @Autowired
    UserRepository userRepository;

    @Autowired
    @Qualifier("userRolesTanaka")
    UserRolesDto userRolesTanaka;

    @Autowired
    @Qualifier("userRolesYamada")
    UserRolesDto userRolesYamada;

    @BeforeAll
    public static void setup() {
        request = new MockHttpServletRequest();
        request.setParameter("sortBy", "userName");
        request.setParameter("direction", "asc");
        request.setParameter("page", "0");
        request.setParameter("size", "10");
        request.setParameter("keyword", "マネージャー 社員");
    }

    @BeforeEach
    public void beforeEach() {
        doCallRealMethod().when(spyRoleService).findAll();
    }

    @WithMockUser(username = "tanaka", roles = "ADMIN")
    @Test
    void shouldReturnUserListViewWithModelAttributesWhenRequestingUserList() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/user/showList")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "userName")
                        .param("direction", "asc")
                        .param("search", "マネージャー 社員")
                )
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("pageSize", 10))
                .andExpect(model().attribute("sortBy", "userName"))
                .andExpect(model().attribute("direction", "asc"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("userSearchForm"))
                .andExpect(model().attributeExists("users"))
                .andReturn();


        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "product-management/user_list");

        // modelを取得
        Map<String, Object> model = mav.getModel();
        Page<UserRolesDto> userRolesPage = (Page<UserRolesDto>) model.get("users");
        assertEquals(29, userRolesPage.getTotalElements());
    }

    // 検索ワードが複数ある場合
    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnUserListViewWithModelAttributesWhenSearchingUserList() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/user/search")
                        .param("search", "管理者 マネージャー")
                        .param("sortBy", "displayName")
                        .param("direction", "desc")
                        .param("page", "0")
                        .param("size", "10")
                ).andExpect(status().isOk())
                .andExpect(view().name("product-management/user_list"))
                .andExpect(model().attributeExists("userSearchForm"))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("pageSize", 10))
                .andExpect(model().attribute("sortBy", "displayName"))
                .andExpect(model().attribute("direction", "desc"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("userSearchForm"))
                .andExpect(model().attributeExists("users")).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        Map<String, Object> model = mav.getModel();
        Page<UserRolesDto> userRolesPage = (Page<UserRolesDto>) model.get("users");
        assertEquals(4, userRolesPage.getTotalElements());
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnUserListViewWithModelAttributeWhenValidationErrorOccursInSearch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/search")
                        .param("search", "test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test ")
                        .param("sortBy", "displayName")
                        .param("direction", "desc")
                        .param("page", "1")
                        .param("size", "10")
                ).andExpect(status().isOk())
                .andExpect(view().name("product-management/user_list"))
                .andExpect(model().attributeExists("userSearchForm"))
                .andExpect(model().attributeHasFieldErrors("userSearchForm", "search"))
                .andExpect(content().string(containsString("検索文字数が適切ではありません")))
                .andExpect(content().string(containsString("検索キーワードは10以下にしてください。")))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("pageSize", 10))
                .andExpect(model().attribute("sortBy", "displayName"))
                .andExpect(model().attribute("direction", "desc"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("userSearchForm"))
                .andExpect(model().attributeDoesNotExist("users"))
        ;
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnUserRegisterViewWithModelAttributeWhenRegisteringUserForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/user_register"))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnUserRegisterViewWithModelAttributeWhenSessionIsExists() throws Exception {
        UserForm userForm = new UserForm(null, "test", "太郎", "test", null, "管理者", "test.tarou@example.com");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("registerUserForm", userForm);
        mockMvc.perform(MockMvcRequestBuilders.get("/user/register")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/user_register"))
                .andExpect(model().attributeExists("userForm"))
                .andExpect(model().attribute("userForm", hasProperty("userName", is("test"))))
                .andExpect(model().attribute("userForm", hasProperty("firstName", is("太郎"))))
                .andExpect(model().attribute("userForm", hasProperty("lastName", is("test"))))
                .andExpect(model().attribute("userForm", hasProperty("roleDisplayName", is("管理者"))))
                .andExpect(model().attribute("userForm", hasProperty("email", is("test.tarou@example.com"))))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnUserRegisterViewWithModelAttributeWhenUserIdIsExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/register")
                        .param("userId", "1")
                        .flashAttr("isNewUser", true))
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/user_register"))
                .andExpect(model().attributeExists("userForm"))
                .andExpect(model().attribute("userForm", hasProperty("userName", is("tanaka"))))
                .andExpect(model().attribute("userForm", hasProperty("firstName", is("太郎"))))
                .andExpect(model().attribute("userForm", hasProperty("lastName", is("田中"))))
                .andExpect(model().attribute("userForm", hasProperty("roleDisplayName", is("管理者"))))
                .andExpect(model().attribute("userForm", hasProperty("email", is("tanaka.taro@example.com"))))
                .andExpect(model().attributeExists("roles"))
                .andExpect(content().string(containsString("登録が完了しました")));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnConfirmViewWithRegisterUserFormOnPost() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/user/registerConfirmProcessing")
                        .param("userName", "itoga")
                        .param("firstName", "大輔")
                        .param("lastName", "糸賀")
                        .param("password", "password1234!")
                        .param("roleName", "")
                        .param("roleDisplayName", "管理者")
                        .param("email", "itoga.daisuke@example.com")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/user_confirm"))
                .andExpect(model().attribute("title", "ユーザー登録"))
                .andReturn();

        HttpSession session = mvcResult.getRequest().getSession(false);
        UserForm form = (UserForm) session.getAttribute("registerUserForm");
        assertEquals("itoga", form.getUserName());
        assertEquals("大輔", form.getFirstName());
        assertEquals("糸賀", form.getLastName());
        assertEquals("password1234!", form.getPassword());
        assertEquals("ROLE_ADMIN", form.getRoleName());
        assertEquals("管理者", form.getRoleDisplayName());
        assertEquals("itoga.daisuke@example.com", form.getEmail());

    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnUserRegisterIndexWithValidationErrorsWhenDtoIsInvalid() throws Exception {
        List<Role> roles = roleService.findAll();
        mockMvc.perform(post("/user/registerConfirmProcessing")
                        .param("userName", "")
                        .param("password", "")
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("roleDisplayName", "")
                        .param("email", "")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/user_register"))
                .andExpect(content().string(containsString("ユーザー名は2文字以上50文字以内で入力してください")))
                .andExpect(content().string(containsString("ユーザー名は必須です")))
                .andExpect(content().string(containsString("パスワードは8文字以上16文字以下で入力してください")))
                .andExpect(content().string(containsString("パスワードは必須です")))
                .andExpect(content().string(matchesPattern("(?s).*パスワードは.*含む必要があります.*")))
                .andExpect(content().string(containsString("名前は1文字以上30文字以下で入力してください")))
                .andExpect(content().string(containsString("名字は1文字以上30文字以下で入力してください")))
                .andExpect(content().string(containsString("役割は必須です")))
                .andExpect(content().string(containsString("メールは3文字以上256文字以下で入力してください")))
                .andExpect(content().string(containsString("メールアドレスは必須です")))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnRegisterViewWithValidationErrorsWhenUserNameAlreadyExists() throws Exception {
        mockMvc.perform(post("/user/registerConfirmProcessing")
                        .param("userName", "tanaka")  // 既存のユーザー名
                        .param("firstName", "太郎")
                        .param("lastName", "田中")
                        .param("password", "Password123!")
                        .param("roleDisplayName", "管理者")
                        .param("email", "tanaka.taro@example.com")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/user_register"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("userForm", "userName", "email"))
                .andExpect(content().string(containsString("このユーザー名はすでに使われています")))
                .andExpect(content().string(containsString("このメールアドレスはすでに使われています")));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldThrowRoleNotFoundExceptionWhenRolesAreNotFound() throws Exception {
        // RoleServiceをモック化して空のリストを返すように設定
        doReturn(new ArrayList<>(List.of())).when(spyRoleService).findAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/registerConfirmProcessing")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(view().name("product-management/error/general"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", containsString("ロール情報の取得に失敗しました")))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RoleNotFoundException));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldThrowRoleNotFoundExceptionWhenRolesIsEmpty() throws Exception {
        // 設定
        int userId = 1;
        doReturn(new ArrayList<>(List.of())).when(spyRoleService).findAll();

        UserForm userForm = new UserForm(1L, "田中", "太郎", "田中", null, "管理者", "tanaka.taro@example.com");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("editUserForm", userForm);

        // 実行・検証
        mockMvc.perform(MockMvcRequestBuilders.get("/user/edit")
                        .session(session)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(view().name("product-management/error/general"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RoleNotFoundException))
                .andExpect(result -> assertEquals("ロール情報の取得に失敗しました",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldThrowUserFormNotFoundExceptionWhenUserFormIsNull() throws Exception {
        // 設定
        int userId = 0;

        // 実行・検証
        mockMvc.perform(MockMvcRequestBuilders.get("/user/edit")
                        .param("userId", "0"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("product-management/error/general"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserFormNotFoundException))
                .andExpect(result -> assertEquals("ユーザー情報の取得に失敗しました。",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnEditViewWhenRequestIsValidWithoutSession() throws Exception {
        // 設定
        int userId = 1;
        UserForm userForm = new UserForm(1L, "tanaka", "太郎", "田中", "ROLE_ADMIN", "管理者", "tanaka.taro@example.com");

        // 実行・検証
        mockMvc.perform(MockMvcRequestBuilders.get("/user/edit")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/user_edit"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attribute("userForm", hasProperty("id", is(1L))))
                .andExpect(model().attribute("userForm", hasProperty("userName", is("tanaka"))))
                .andExpect(model().attribute("userForm", hasProperty("firstName", is("太郎"))))
                .andExpect(model().attribute("userForm", hasProperty("lastName", is("田中"))))
                .andExpect(model().attribute("userForm", hasProperty("roleName", is("ROLE_ADMIN"))))
                .andExpect(model().attribute("userForm", hasProperty("roleDisplayName", is("管理者"))))
                .andExpect(model().attribute("userForm", hasProperty("email", is("tanaka.taro@example.com"))));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnEditViewWhenRequestIsValidWithSession() throws Exception {
        // 設定
        int userId = 1;
        UserForm userForm = new UserForm(1L, "tanaka", "太郎", "田中", "", "管理者", "tanaka.taro@example.com");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("editUserForm", userForm);

        // 実行・検証
        mockMvc.perform(MockMvcRequestBuilders.get("/user/edit")
                        .session(session)
                        .param("userId", String.valueOf(userId))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/user_edit"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attribute("userForm", hasProperty("id", is(1L))))
                .andExpect(model().attribute("userForm", hasProperty("userName", is("tanaka"))))
                .andExpect(model().attribute("userForm", hasProperty("firstName", is("太郎"))))
                .andExpect(model().attribute("userForm", hasProperty("lastName", is("田中"))))
                .andExpect(model().attribute("userForm", hasProperty("roleName", is(""))))
                .andExpect(model().attribute("userForm", hasProperty("roleDisplayName", is("管理者"))))
                .andExpect(model().attribute("userForm", hasProperty("email", is("tanaka.taro@example.com"))));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnEditViewWithValidationErrorsWhenFormIsEmpty() throws Exception {
        mockMvc.perform(post("/user/editConfirmProcessing")
                        .param("id", "")
                        .param("userName", "")
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("password", "test")
                        .param("roleDisplayName", "")
                        .param("email", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/user_edit"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().hasErrors())
                .andExpect(content().string(containsString("ユーザー名は2文字以上50文字以内で入力してください")))
                .andExpect(content().string(containsString("ユーザー名は必須です")))
                .andExpect(content().string(matchesPattern("(?s).*パスワードは空欄または、8〜16文字・英数字＋記号.*で入力してください.*")))
                .andExpect(content().string(containsString("名前は1文字以上30文字以下で入力してください")))
                .andExpect(content().string(containsString("名前は必須です")))
                .andExpect(content().string(containsString("名字は1文字以上30文字以下で入力してください")))
                .andExpect(content().string(containsString("名字は必須です")))
                .andExpect(content().string(containsString("役割は必須です")))
                .andExpect(content().string(containsString("メールは3文字以上256文字以下で入力してください")))
                .andExpect(content().string(containsString("メールアドレスは必須です")));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnEditViewWithValidationErrorsWhenDuplicateUserExists() throws Exception {
        mockMvc.perform(post("/user/editConfirmProcessing")
                        .param("id", "2")
                        .param("userName", "tanaka")
                        .param("firstName", "太郎")
                        .param("lastName", "田中")
                        .param("password", "Password123!")
                        .param("roleDisplayName", "管理者")
                        .param("email", "tanaka.taro@example.com")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/user_edit"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("userForm", "userName", "email"))
                .andExpect(content().string(containsString("このユーザー名はすでに使われています")))
                .andExpect(content().string(containsString("このメールアドレスはすでに使われています")));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnConfirmViewWhenUserFormIsValidWithExistingId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/user/editConfirmProcessing")
                        .param("id", "1")
                        .param("userName", "tanaka")
                        .param("firstName", "一郎")
                        .param("lastName", "鈴木")
                        .param("password", "Password123!")
                        .param("roleName", "ROLE_ADMIN")
                        .param("roleDisplayName", "マネージャー")
                        .param("email", "suzuki.ichirou@example.com")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/user_confirm"))
                .andExpect(model().attribute("title", "ユーザー編集"))
                .andReturn();

        HttpSession session = mvcResult.getRequest().getSession();
        UserForm form = (UserForm) session.getAttribute("editUserForm");
        assertNotNull(form);
        assertEquals("ROLE_MANAGER", form.getRoleName());
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldProcessEditConfirmationWithoutPassword() throws Exception {
        // 実行
        mockMvc.perform(post("/user/editConfirmProcessing")
                        .param("id", "1")
                        .param("userName", "test")
                        .param("firstName", "太郎")
                        .param("lastName", "田中")
                        .param("password", "")  // パスワードは空で送信
                        .param("roleDisplayName", "社員")
                        .param("roleName", "ROLE_ADMIN")
                        .param("email", "test.taro@example.com")
                        .with(csrf()))
                // 検証
                .andExpect(status().isOk())
                .andExpect(view().name("product-management/user_confirm"))  // ViewName確認
                .andExpect(model().hasNoErrors())  // バリデーションエラーがないこと
                .andExpect(model().attribute("title", "ユーザー編集"))
                .andExpect(request().sessionAttribute("editUserForm", hasProperty("roleName", is("ROLE_EMPLOYEE"))))  // roleNameが更新されていること
                .andExpect(request().sessionAttribute("editUserForm", hasProperty("password", is(""))));  // パスワードが空であること
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldThrowRoleNotFoundExceptionWhenRolesIsEmptyInUserEdit() throws Exception {
        doReturn(new ArrayList<>(List.of())).when(spyRoleService).findAll();

        mockMvc.perform(post("/user/editConfirmProcessing")
                        .param("id", "1")
                        .param("userName", "test")
                        .param("firstName", "太郎")
                        .param("lastName", "田中")
                        .param("password", "Password123!")
                        .param("roleDisplayName", "社員")
                        .param("roleName", "ROLE_ADMIN")
                        .param("email", "test.taro@example.com")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(view().name("product-management/error/general"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RoleNotFoundException))
                .andExpect(result -> assertEquals("ロール情報の取得に失敗しました",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldThrowExceptionWhenSessionNotFound() throws Exception {
        MockHttpSession emptySession = new MockHttpSession();
        mockMvc.perform(post("/user/save")
                        .session(emptySession)
                        .with(csrf()))
                .andExpect(result -> {
                    assertNotNull(result.getResolvedException());
                    assertTrue(result.getResolvedException() instanceof SessionNotFoundException);
                    assertEquals("セッションが無効です。もう一度やり直してください。",
                            result.getResolvedException().getMessage());
                })
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error/general"));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldSaveNewUserWhenRegisterUserFormSessionExists() throws Exception {
        // セッションの設定
        UserForm userForm = new UserForm(null, "tuzimoto", "清見", "辻本", "ROLE_EMPLOYEE", "社員", "tuzimoto.kiyomi@example.com");
        userForm.setPassword("password1");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("registerUserForm", userForm);

        mockMvc.perform(post("/user/save")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/register"))
                .andExpect(flash().attribute("isNewUser", true))
                .andExpect(request().sessionAttributeDoesNotExist("registerUserForm"));

        assertNotNull(userDao.findByUserName("tuzimoto"));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldSaveExistingUserWhenEditUserFormSessionExists() throws Exception {
        // セッションの設定
        UserForm userForm = new UserForm(1L, "tanaka", "鈴木", "一郎", "ROLE_ADMIN", "管理者", "tanaka.taro@example.com");
        userForm.setPassword("password2");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("editUserForm", userForm);

        mockMvc.perform(post("/user/save")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/edit"))
                .andExpect(flash().attribute("isNewUser", true))
                .andExpect(request().sessionAttributeDoesNotExist("editUserForm"));

        assertNotNull(userDao.findByUserName("tanaka"));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldKeepExistingPasswordWhenEditUserFormSessionExistsWithEmptyPassword() throws Exception {
        // 更新前のユーザーのパスワードを保存
        String originalPassword = userDao.findByUserName("tanaka").getPassword();

        // セッションの設定
        UserForm userForm = new UserForm(1L, "tanaka", "太郎", "田中", "ROLE_MANAGER", "マネージャー", "tanaka.taro@example.com");
        userForm.setPassword("");  // パスワードを空に設定

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("editUserForm", userForm);

        // 実行と検証
        mockMvc.perform(post("/user/save")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/edit"))
                .andExpect(flash().attribute("isNewUser", true))
                .andExpect(request().sessionAttributeDoesNotExist("editUserForm"));

        // 更新後のパスワードを取得して比較
        String updatedPassword = userDao.findByUserName("tanaka").getPassword();
        assertThat(updatedPassword).isEqualTo(originalPassword);
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnErrorWhenUserIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/user/delete")
                        .param("userId", "30"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("product-management/error/general"))
                .andExpect(model().attribute("errorMessage", "An invalid id was entered"));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldReturnErrorWhenUserIdIsZero() throws Exception {
        mockMvc.perform(get("/user/delete")
                        .param("userId", "0"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("product-management/error/general"))
                .andExpect(model().attribute("errorMessage", "Please specify your id"));
    }

    @Test
    @WithMockUser(username = "tanaka", roles = "ADMIN")
    void shouldDeleteUserWhenUserIdExists() throws Exception {
        // 削除前にユーザーが存在することを確認
        assertThat(userRepository.existsById(29)).isTrue();

        mockMvc.perform(get("/user/delete")
                        .param("userId", "29"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/showList"))
                .andExpect(flash().attribute("isUserDelete", true));

        // 削除後にユーザーが存在しないことを確認
        assertThat(userRepository.existsById(29)).isFalse();
    }


}
