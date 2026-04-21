package com.giitotech.product_management.dto;

public class HeaderUserInfo {

    private String userName;
    private String roleDisplayName;

    public HeaderUserInfo(String userName, String roleDisplayName) {
        this.userName = userName;
        this.roleDisplayName = roleDisplayName;
    }

    public HeaderUserInfo() {
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRoleDisplayName(String roleDisplayName) {
        this.roleDisplayName = roleDisplayName;
    }

    public String getUserName() {
        return userName;
    }

    public String getRoleDisplayName() {
        return roleDisplayName;
    }

    @Override
    public String toString() {
        return "HeaderUserInfo{" +
                "userName='" + userName + '\'' +
                ", roleDisplayName='" + roleDisplayName + '\'' +
                '}';
    }
}
