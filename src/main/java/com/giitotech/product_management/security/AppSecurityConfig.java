package com.giitotech.product_management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.giitotech.product_management.service.UserService;

@Configuration
public class AppSecurityConfig {

	// BCryptPasswordEncoderを定義
	@Bean
	public BCryptPasswordEncoder passwordEncode() {
		return new BCryptPasswordEncoder();
	}

	// Dao認証プロバイダーを定義
	@Bean
	public DaoAuthenticationProvider authenticationProvider(UserService userService) {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(userService); // custom user details serviceをセット
		auth.setPasswordEncoder(passwordEncode()); // password encoder - bcrypt をセット
		return auth;
	}

	// 認可処理を定義
	@Bean
	public SecurityFilterChain filterChain(
			HttpSecurity http,
			AuthenticationSuccessHandler customAuthenticationSuccessHandler,
			LogoutSuccessHandler customLogoutSuccessHandler) throws Exception {
		http.authorizeHttpRequests(configurer -> configurer
				.requestMatchers("/").hasRole("EMPLOYEE")
				.requestMatchers("/user/**").hasAnyRole("MANAGER", "ADMIN")
				.requestMatchers("/product/**").hasRole("EMPLOYEE")
				.requestMatchers("/loginPage").permitAll()
				.requestMatchers("/asset/**").permitAll()
				.requestMatchers("/css/**").permitAll()
				.requestMatchers("/js/**").permitAll()
				.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/loginPage")
						.loginProcessingUrl("/authenticateTheUser")
						.successHandler(customAuthenticationSuccessHandler)
						.permitAll())
				.rememberMe(rememberMe -> rememberMe
						.key("mySecretKey") // Remember-Meキーの設定
						.tokenValiditySeconds(1209600) // トークンの有効期限（14日間）
						.rememberMeCookieName("remember-me") // クッキー名を明示的に指定（デフォルトはremember-me）
				)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.invalidateHttpSession(false) // SecurityContextLogoutHandler による session.invalidate() を無効化
						.logoutSuccessHandler(customLogoutSuccessHandler)
						.permitAll())
				.exceptionHandling(configurer ->
						configurer.accessDeniedPage("/product-management/access-denied"));

		return http.build();
	}

	//	@Bean
	//	public InMemoryUserDetailsManager userDetailManager() {
	//
	//		UserDetails john = User.builder()
	//				.username("john")
	//				.password("{noop}test123")
	//				.roles("EMPLOYEE")
	//				.build();
	//
	//		UserDetails mary = User.builder()
	//				.username("mary")
	//				.password("{noop}test123")
	//				.roles("EMPLOYEE", "MANAGER")
	//				.build();
	//
	//		UserDetails susan = User.builder()
	//				.username("susan")
	//				.password("{noop}test123")
	//				.roles("EMPLOYEE", "MANAGER", "ADMIN")
	//				.build();
	//
	//		return new InMemoryUserDetailsManager(john, mary, susan);
	//	}
}
