package com.mergen.socialease.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	AdminAuthService adminAuthService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.httpBasic().authenticationEntryPoint(new AdminAuthEntryPoint());
		http.requestMatchers()
		.antMatchers("/mergen/admin/**")
		.and()
		.authorizeRequests()
			.antMatchers("/mergen/admin/login").authenticated()
			.antMatchers("/mergen/admin/getclubs").authenticated()
			.antMatchers("/mergen/admin/getspecclub").authenticated()
			.antMatchers("/mergen/admin/saveclub").authenticated()
			.antMatchers("/mergen/admin/deleteclub").authenticated()
			.antMatchers("/mergen/admin/updateclub").authenticated()
			.antMatchers("/mergen/admin/getclubrequests").authenticated()
			.antMatchers("/mergen/admin/deleteclubrequest").authenticated()
			.antMatchers("/mergen/admin/getadminrequests").authenticated()
			.antMatchers("/mergen/admin/getuserwithmode").authenticated();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(adminAuthService).passwordEncoder(new BCryptPasswordEncoder());
	}
}

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecondSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	UserAuthService userAuthService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.httpBasic().authenticationEntryPoint(new UserAuthEntryPoint());
		http
		.authorizeRequests()
			.antMatchers("/secured").authenticated()
			.antMatchers("/auth").authenticated()
			.antMatchers("/getuserdetails").authenticated()
			.antMatchers("/surveyanswers").authenticated()
			.antMatchers("/review").authenticated()
			.antMatchers("/report").authenticated()
			.antMatchers("/viewreport").authenticated()
			.antMatchers("/evaluatereport").authenticated()
			.antMatchers("/surveynewclubs").authenticated()
			.antMatchers("/saveclubrequest").authenticated()
			.antMatchers("/saveadminrequest").authenticated()
			.antMatchers("/getuserwithmode").authenticated()
			.antMatchers("/saveuserimage").authenticated()
			.antMatchers("/deleteuserimage").authenticated()
			.antMatchers("/getnewclubs").authenticated()
			.antMatchers("/createpost").authenticated()
			.antMatchers("/deletepost").authenticated()
			.antMatchers("/{subclubid}/getposts").authenticated()
			.antMatchers("/getpost/{postid}").authenticated()
			.antMatchers("/makecomment").authenticated()
			.antMatchers("/likepost").authenticated()
			.antMatchers("/{userid}/getuserposts").authenticated()
			.antMatchers("/getlikedposts").authenticated()
			.antMatchers("/getpostshomepage").authenticated()
			.antMatchers("/deletecomment").authenticated()
			.antMatchers("/getnameofthesubclub").authenticated()
			.antMatchers("/getnameoftheclub").authenticated()
			.antMatchers("/checkmyreview").authenticated()
			.antMatchers("/createevent").authenticated()
			.antMatchers("/deleteevent").authenticated()
			.antMatchers("/getsubclubevents").authenticated()
			.antMatchers("/getuserevents").authenticated()
			.antMatchers("/updateuser/{usernamee}").authenticated()
			.antMatchers("/deleteuser").authenticated()
		.and()
		.authorizeRequests()
			.antMatchers("/getclubs").permitAll()
			.antMatchers("/getspecclub").permitAll()
			.antMatchers("/register").permitAll()
			.antMatchers("/confirm").permitAll()
			.antMatchers("/forgot").permitAll()
			.antMatchers("/recover").permitAll();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userAuthService).passwordEncoder(new BCryptPasswordEncoder());
	}
}
