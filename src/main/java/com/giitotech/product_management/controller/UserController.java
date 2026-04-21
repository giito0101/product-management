package com.giitotech.product_management.controller;

import java.util.List;
import java.util.logging.Logger;

import com.giitotech.product_management.dto.UserSearchForm;
import com.giitotech.product_management.entity.User;
import com.giitotech.product_management.exception.*;
import com.giitotech.product_management.helper.UserSearchFormHelper;
import com.giitotech.product_management.validation.CreateGroup;
import com.giitotech.product_management.validation.EditGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.giitotech.product_management.dto.SearchForm;
import com.giitotech.product_management.dto.UserForm;
import com.giitotech.product_management.dto.UserRolesDto;
import com.giitotech.product_management.entity.Role;
import com.giitotech.product_management.service.KeywordService;
import com.giitotech.product_management.service.PagingService;
import com.giitotech.product_management.service.RoleService;
import com.giitotech.product_management.service.UserService;
import com.giitotech.product_management.service.UserSessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    private RoleService roleService;
    private KeywordService keywordService;
    private PagingService pagingService;
    private UserSessionService userSessionService;
    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    public UserController(
            UserService theUserService,
            RoleService theRoleService,
            KeywordService theKeywordService,
            PagingService thePagingService,
            UserSessionService theUserSessionService
    ) {
        this.roleService = theRoleService;
        this.userService = theUserService;
        this.keywordService = theKeywordService;
        this.pagingService = thePagingService;
        this.userSessionService = theUserSessionService;
    }

    @GetMapping("/showList")
    public String userList(
            @RequestParam(defaultValue = "userName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            Model theModel,
            @Valid @ModelAttribute("userSearchForm") UserSearchForm userSearchForm) {

        Page<UserRolesDto> theUserRoles = null;

        if (keyword != null) {
            userSearchForm.setSearch(keyword);
        }

        String[] keywords = {};

        keywords = keywordService.processKeywords(userSearchForm.getSearch());

        // ソートとページングの設定
        Pageable pageable = pagingService.createPageable(sortBy, direction, page, size);
        theUserRoles = userService.searchUsers(pageable, keywords, sortBy, direction);

        theModel.addAttribute("users", theUserRoles);
        theModel.addAttribute("currentPage", page);
        theModel.addAttribute("totalPages", theUserRoles.getTotalPages());
        theModel.addAttribute("pageSize", size);
        theModel.addAttribute("sortBy", sortBy);
        theModel.addAttribute("direction", direction);
        theModel.addAttribute("userSearchForm", userSearchForm);

        return "product-management/user_list";
    }

    @GetMapping("/search")
    public String search(
            Model theModel,
            @Valid @ModelAttribute("userSearchForm") UserSearchForm userSearchForm,
            BindingResult theBindingResult) {

        int page = 0;
        int size = 10;
        String sortBy = "userName";
        String direction = "asc";

        UserSearchFormHelper.fillDefaults(userSearchForm, sortBy, direction, page, size);

        if (theBindingResult.hasErrors()) {
            theModel.addAttribute("userSearchForm", userSearchForm);
            theModel.addAttribute("currentPage", userSearchForm.getPage());
            theModel.addAttribute("totalPages", 0);
            theModel.addAttribute("pageSize", userSearchForm.getSize());
            theModel.addAttribute("sortBy", userSearchForm.getSortBy());
            theModel.addAttribute("direction", userSearchForm.getDirection());

            return "product-management/user_list";
        }

        String[] keywords = null;
        String keyword = userSearchForm.getSearch();

        keywords = keywordService.processKeywords(keyword);

        Page<UserRolesDto> theUserRoles = null;
        Pageable pageable = pagingService.createPageable(userSearchForm);

        theUserRoles = userService.searchUsers(pageable, keywords, userSearchForm.getSortBy(), userSearchForm.getDirection());

        theModel.addAttribute("users", theUserRoles);
        theModel.addAttribute("currentPage", userSearchForm.getPage());
        theModel.addAttribute("totalPages", theUserRoles.getTotalPages());
        theModel.addAttribute("pageSize", userSearchForm.getSize());
        theModel.addAttribute("sortBy", userSearchForm.getSortBy());
        theModel.addAttribute("direction", userSearchForm.getDirection());
        theModel.addAttribute("userSearchForm", userSearchForm);

        // /商品一覧画面を表示
        return "product-management/user_list";
    }

    @GetMapping("/register")
    public String userRegister(Model theModel,
                               HttpServletRequest request,
                               @RequestParam(value = "userId", required = false) Integer userId) {
        HttpSession session = request.getSession(false); // セッションが存在する場合のみ取得

        UserForm userForm = null;
        if(userId == null) {
            userForm = userSessionService.getUserForm(session);
        } else {
            userForm = userSessionService.getUserForm(session, userId);
        }

        theModel.addAttribute("userForm", userForm);
        theModel.addAttribute("roles", roleService.findAll());

        return "product-management/user_register";
    }

    @PostMapping("/registerConfirmProcessing")
    public String registerConfirmProcessing(
            @Validated(CreateGroup.class) @ModelAttribute("userForm") UserForm userForm,
            BindingResult theBindingResult,
            Model theModel,
            HttpSession session) {
        // 全てのロールを取得
        List<Role> roles = roleService.findAll();
        if (roles.isEmpty()) {
            throw new RoleNotFoundException("ロール情報の取得に失敗しました");
        }

        // フォームを検証する
        if (theBindingResult.hasErrors()) {
            theModel.addAttribute("roles", roles);
            return "product-management/user_register";
        }

        // ユニーク制約フィールドの重複確認
        try {
            userService.validateForInsert(userForm);
        } catch(ValidationException ex) {
            for (FieldError error: ex.getFieldErrors()) {
                theBindingResult.rejectValue(error.getField(),"duplicate", error.getDefaultMessage());
            }
            theModel.addAttribute("roles", roles);
            return "product-management/user_register";
        }

        roleService.setRoleToUserForm(userForm, roles);

        // キャンセルした時の処理も兼ねて、セッションに格納
        session.setAttribute("registerUserForm", userForm);

        theModel.addAttribute("title", "ユーザー登録");

        return "product-management/user_confirm";
    }

    @GetMapping("/edit")
    public String edit(
            @RequestParam("userId") int userId,
            HttpServletRequest request,
            Model theModel) {
        HttpSession session = request.getSession(false); // セッションが存在する場合のみ取得

        // 全てのロールを取得
        List<Role> roles = roleService.findAll();
        if (roles.isEmpty()) {
            throw new RoleNotFoundException("ロール情報の取得に失敗しました");
        }
        // 全てのロールを取得
        theModel.addAttribute("roles", roles);

        // UserFormを初期化
        UserForm userForm = null;
        try {
            userForm = userSessionService.getUserForm(session, userId);
        } catch (UserNotFoundException e) {
            throw new UserFormNotFoundException("ユーザー情報の取得に失敗しました。");
        }

        theModel.addAttribute("userForm", userForm);

        return "product-management/user_edit";
    }

    @PostMapping("/editConfirmProcessing")
    public String editConfirmProcessing(
            @Validated(EditGroup.class)  @ModelAttribute("userForm") UserForm userForm,
            BindingResult theBindingResult,
            Model theModel,
            HttpSession session) {
        // 全てのロールを取得
        List<Role> roles = roleService.findAll();
        if (roles.isEmpty()) {
            throw new RoleNotFoundException("ロール情報の取得に失敗しました");
        }

        // フォームを検証する
        if (theBindingResult.hasErrors()) {
            theModel.addAttribute("roles", roles);
            return "product-management/user_edit";
        }

        // ユニーク制約フィールドの重複確認
        try {
            userService.validateForUpdate(userForm);
        } catch(ValidationException ex) {
            for (FieldError error: ex.getFieldErrors()) {
                theBindingResult.rejectValue(error.getField(),"duplicate", error.getDefaultMessage());
            }
            theModel.addAttribute("roles", roles);
            return "product-management/user_edit";
        }

        roleService.setRoleToUserForm(userForm, roles);

        // キャンセルした時の処理も兼ねて、セッションに格納
        session.setAttribute("editUserForm", userForm);
        theModel.addAttribute("title", "ユーザー編集");

        return "product-management/user_confirm";
    }

    @PostMapping("/save")
    public String save(
            HttpSession session,
            Model theModel,
            RedirectAttributes redirectAttributes) {
        // セッションから値を取得
        UserForm registerUserForm = (UserForm) session.getAttribute("registerUserForm");
        UserForm editUserForm = (UserForm) session.getAttribute("editUserForm");
        UserForm userForm = null;

        if (registerUserForm != null) {
            userForm = registerUserForm;
        } else if (editUserForm != null) {
            userForm = editUserForm;
        }

        if (userForm == null) {
            throw new SessionNotFoundException("セッションが無効です。もう一度やり直してください。");
        }

        // Userをデータベースに格納する
        User user = userService.save(userForm);

        // ２度押しを防ぐためにリダイレクトを使う
        String path = "";
        if (userForm.getId() != null) {
            // 編集前データをデータベースから取得
            path = "redirect:/user/edit";
            // リダイレクト先にパラメータを付与
            redirectAttributes.addAttribute("userId", user.getId());
        } else {
            path = "redirect:/user/register";
            redirectAttributes.addAttribute("userId", user.getId());
        }

        // フラッシュスコープにデータを設定
        redirectAttributes.addFlashAttribute("isNewUser", true);

        //Session削除
        session.removeAttribute("registerUserForm");
        session.removeAttribute("editUserForm");

        return path;
    }

    @GetMapping("/delete")
    public String delete(
            @RequestParam("userId") int userId,
            Model model,
            RedirectAttributes redirectAttributes) {
        // 商品を削除する
        userService.deleteById(userId);

        redirectAttributes.addFlashAttribute("isUserDelete", true);

        // /user/shoListへリダイレクトする
        return "redirect:/user/showList";
    }
}
