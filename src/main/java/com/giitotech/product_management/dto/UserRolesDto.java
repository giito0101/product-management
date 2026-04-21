package com.giitotech.product_management.dto;

public class UserRolesDto {
	
	private long id;
	private String userName;
	private String roles;
	private String email;

	public UserRolesDto() {
	}

	public UserRolesDto(Long id, String userName, String roles, String email) {
		this.id = id;
		this.userName = userName;
		this.roles = roles;
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserRolesDto{" +
				"id=" + id +
				", userName='" + userName + '\'' +
				", roles='" + roles + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}
