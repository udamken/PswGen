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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Controller methods that are annotated with this annotation, return a {@link ResponseEntity} and have one
 * argument with the type {@link HttpServletRequest} are recognized by the {@link TokenAspect} and require the
 * header {@link TokenAspect#HEADER_SECURITY_TOKEN} to be set to the configured security token in order to be
 * executed correctly. If the header was not set, an {@link HttpStatus#BAD_REQUEST} is sent and a short
 * description about what to do to make the request work.
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequireToken {
	// Nothing to do.
}
