package com.giitotech.product_management.dto;

import com.giitotech.product_management.validation.CreateGroup;
import com.giitotech.product_management.validation.EditGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserForm {
    private Long id;

    @Size(min = 2, max = 50, message = "ユーザー名は2文字以上50文字以内で入力してください", groups = CreateGroup.class)
    @Size(min = 2, max = 50, message = "ユーザー名は2文字以上50文字以内で入力してください", groups = EditGroup.class)
    @NotBlank(message = "ユーザー名は必須です", groups = CreateGroup.class)
    @NotBlank(message = "ユーザー名は必須です", groups = EditGroup.class)
    private String userName;

    @Size(
            groups = CreateGroup.class,
            min = 8, max = 16, message = "パスワードは8文字以上16文字以下で入力してください")
    @NotBlank(
            groups = CreateGroup.class,
            message = "パスワードは必須です")
    @Pattern(
            groups = CreateGroup.class,
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-])[A-Za-z\\d!@#$%^&*()_+=-]{8,16}$",
            message = "パスワードは半角英数字と記号（!@#$%^&*()_+=-）を含む必要があります"
    )
    @Pattern(
            groups = EditGroup.class,
            regexp = "^$|^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-])[A-Za-z\\d!@#$%^&*()_+=-]{8,16}$",
            message = "パスワードは空欄または、8〜16文字・英数字＋記号（!@#$%^&*()_+=-）で入力してください"
    )
    private String password;

    @Size(min = 1, max = 30, message = "名前は1文字以上30文字以下で入力してください", groups = CreateGroup.class)
    @Size(min = 1, max = 30, message = "名前は1文字以上30文字以下で入力してください", groups = EditGroup.class)
    @NotBlank(message = "名前は必須です", groups = CreateGroup.class)
    @NotBlank(message = "名前は必須です", groups = EditGroup.class)
    private String firstName;

    @Size(min = 1, max = 30, message = "名字は1文字以上30文字以下で入力してください", groups = CreateGroup.class)
    @Size(min = 1, max = 30, message = "名字は1文字以上30文字以下で入力してください", groups = EditGroup.class)
    @NotBlank(message = "名字は必須です", groups = CreateGroup.class)
    @NotBlank(message = "名字は必須です", groups = EditGroup.class)
    private String lastName;

    private String roleName;

    @NotBlank(message = "役割は必須です", groups = CreateGroup.class)
    @NotBlank(message = "役割は必須です", groups = EditGroup.class)
    private String roleDisplayName;

    @Size(min = 3, max = 254, message = "メールは3文字以上256文字以下で入力してください", groups = CreateGroup.class)
    @Size(min = 3, max = 254, message = "メールは3文字以上256文字以下で入力してください", groups = EditGroup.class)
    @Email(message = "メールアドレスの形式が正しくありません", groups = CreateGroup.class)
    @Email(message = "メールアドレスの形式が正しくありません", groups = EditGroup.class)
    @NotBlank(message = "メールアドレスは必須です", groups = CreateGroup.class)
    @NotBlank(message = "メールアドレスは必須です", groups = EditGroup.class)
    private String email;

    public UserForm(Long id,
                    String userName,
                    String firstName,
                    String lastName,
                    String roleName,
                    String roleDisplayName,
                    String email) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roleName = roleName;
        this.roleDisplayName = roleDisplayName;
        this.email = email;
    }

    public UserForm() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDisplayName() {
        return roleDisplayName;
    }

    public void setRoleDisplayName(String roleDisplayName) {
        this.roleDisplayName = roleDisplayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserForm [userName=" + userName + ", password=" + password + ", firstName=" + firstName + ", lastName="
                + lastName + ", roleName=" + roleName + ", roleDisplayName=" + roleDisplayName + ", email=" + email + "]";
    }
}
