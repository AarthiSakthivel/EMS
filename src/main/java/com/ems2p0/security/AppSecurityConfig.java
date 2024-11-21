package com.ems2p0.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * EMS 2.0 - Security layer to maintain all the security logics to prevent the
 * external attacks or threats by preventing the application functionalities
 * anonymous access or request
 *
 * @author Mohan
 * @category Security layer
 * @apiNote Developer should be more responsible to maintain and writing the
 *          logic in this layer and the bean methods will be more concise and
 *          simple.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(jsr250Enabled=true)
public class AppSecurityConfig {

	/**
	 * Injected application authentication filter to adding authorization whenever a
	 * request receives from the employee or user
	 */ 
	final AppAuthenticationFilter authenticationFilter;

	/**
	 * Method configure all of the API level authorization and mentioned the session
	 * as state less as of now. All of the authorization are handled in controller
	 * level with roles based on the jsr250Enabled by global method security.
	 *
	 * @param http
	 * @return
	 * @throws Exception
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(request -> request.requestMatchers("/authentication/**",
																 "/attendance/employee/work-type/**")
						.permitAll().anyRequest().authenticated())
				.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	/**
	 * Initiating the authentication manager bean.
	 *
	 * @param config
	 * @return
	 * @throws Exception
	 */
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}
