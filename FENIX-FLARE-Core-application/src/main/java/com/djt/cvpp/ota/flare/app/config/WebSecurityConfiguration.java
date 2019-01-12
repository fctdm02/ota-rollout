package com.djt.cvpp.ota.flare.app.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {


	@Configuration
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	public static class HttpSecurityConfiguration extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
			.csrf()
				.disable()
			.authorizeRequests()
				.antMatchers("/api/**").permitAll()
				.antMatchers("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/v2/api-docs").permitAll()
				.anyRequest().authenticated()
				.and()
			.httpBasic();
		}
	}

}
