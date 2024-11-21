package com.ems2p0.security;

import com.ems2p0.dao.service.EmsDaoService;
import com.ems2p0.dto.response.GenericResponseDto;
import com.ems2p0.model.UserDetails;
import com.ems2p0.utils.Ems2p0Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * EMS 2.0 - Global Authorization Filter which responsible to authorize all type
 * of employees or user's of this application and handling the authorities by
 * using spring security module.
 *
 * @author Mohan
 * @version v1.0.0
 * @category Security filter
 * @apiNote - Developer should be more responsible to optimise this logic for
 *          the authorization changes or alteration in the application level if
 *          any changes made in the layer will be impact all the entire APIS
 *          within the application
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AppAuthenticationFilter extends OncePerRequestFilter {

	/**
	 * Injected utility to invoke method for decoding the sensitive objects and
	 * parameters
	 */
	final Ems2p0Utility utility;

	/**
	 * Injected JWT utils to extract the user's userName from the token
	 */
	final JwtUtils jwtUtils;

	/**
	 * Injected EmsDaoService to invoke method for checking the valid employee or
	 * user
	 */
	final EmsDaoService daoService;

	@Value("${app.security.username}")
	String appUsername;

	@Value("${app.security.password}")
	String appPassword;

	private static final String[] whiteListedUrls = { "/authentication/"};

	/**
	 * Predefined filter gateway method to parse the request and do the
	 * authorization and things until the request redirecting to the controller
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String authorizationHeader = request.getHeader("Authorization");
		String requestUrl = request.getRequestURI();
		String contextPath = request.getContextPath();
		String endpoint = requestUrl.replaceFirst(contextPath, "");
		Boolean isWhitelisted = Stream.of(whiteListedUrls)
                .map(endpoint::startsWith)
                .findFirst()
                .orElse(false);
		if (isWhitelisted && (authorizationHeader == null)){
			filterChain.doFilter(request, response);
			return;
		}
		try {
			String jwt = authorizationHeader.substring(7);
			String userName = jwtUtils.extractUserName(jwt);
			if (StringUtils.isNotEmpty(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = daoService.loadUserByUsername(userName);
				if (jwtUtils.isTokenValid(jwt, userDetails)) {
					SecurityContext context = SecurityContextHolder.createEmptyContext();
					List<GrantedAuthority> authorities = new ArrayList<>();
					authorities.add(new SimpleGrantedAuthority(
							String.valueOf(userDetails.getEmployeeRoleManagement().getOfficialRole())));
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, authorities);
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					context.setAuthentication(authToken);
					SecurityContextHolder.setContext(context);
				}
			} else {
				log.error("Security context ot username is empty...!");
				this.writeErrorResponse(response, "Unauthorized entry");
			}
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			log.error("Exception occured while doing the internal authorization or parsing the token...!");
			this.writeErrorResponse(response, e.getMessage());
		}
	}

	/**
	 * Method to build the return response by using object mapper
	 *
	 * @param response
	 * @throws IOException
	 */
	private void writeErrorResponse(HttpServletResponse response, String errMsg) throws IOException {
		GenericResponseDto<String> errorResponse = new GenericResponseDto<>(false, errMsg, "UN_AUTHORISED ENTRY");
		ObjectMapper mapper = new ObjectMapper();
		String jsonResponse = mapper.writeValueAsString(errorResponse);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		response.getWriter().write(jsonResponse);
	}

}
