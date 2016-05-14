/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile
 *
 *     Copyright (C) 2005-2016 Uwe Damken
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.dknapps.pswgenserver.token;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * The aspect for handling {@link RequireToken}.
 * 
 */
@Aspect
@Component
public class TokenAspect {
	/**
	 * The name of the header that must contain the {@link #securityToken}.
	 * 
	 */
	public static final String HEADER_SECURITY_TOKEN = "X-SECURITY-TOKEN";

	private static final Map<String, String> ERROR_MESSAGE = new HashMap<>();

	/**
	 * The security token the value of the header {@value #HEADER_SECURITY_TOKEN} must have.
	 * 
	 */
	@Value("${pswgenserver.security-token}")
	private String securityToken;

	static {
		TokenAspect.ERROR_MESSAGE.put("msg", "Missing or got wrong security token!");
	}

	/**
	 * The actual pointcut.
	 * 
	 * @param joinPoint
	 *            The join point. It is not invoked if the header-check fails.
	 * @param request
	 *            The actual {@link HttpServletRequest request}.
	 * @return The result entity.
	 * @throws Throwable
	 *             If the join point execution throws it.
	 */
	@Around("execution(@de.dknapps.pswgenserver.token.RequireToken org.springframework.http.ResponseEntity "
			+ "de.dknapps.pswgenserver.controller..*.*(.., javax.servlet.http.HttpServletRequest, ..)) "
			+ "&& args(request, ..)")
	public ResponseEntity<?> beforeTokenMethod(final ProceedingJoinPoint joinPoint,
			final HttpServletRequest request) throws Throwable {
		if (request.getHeader(TokenAspect.HEADER_SECURITY_TOKEN) == null
				|| !request.getHeader(TokenAspect.HEADER_SECURITY_TOKEN).equals(this.securityToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TokenAspect.ERROR_MESSAGE);
		}

		return (ResponseEntity<?>) joinPoint.proceed();
	}
}
