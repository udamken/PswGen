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
package de.dknapps.pswgenserver.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.dknapps.pswgenserver.service.Service;
import de.dknapps.pswgenserver.service.ServiceService;
import de.dknapps.pswgenserver.token.RequireToken;

/**
 * Handles all requests on <code>/api/service</code> and <code>/api/services</code>.
 * 
 */
@RestController
@RequestMapping({ "/api/service", "/api/services" })
public class ServiceController {
	/**
	 * The {@link ServiceService}.
	 * 
	 */
	@Autowired
	private ServiceService credentialsService;

	/**
	 * Retrieves all available services with the password for each.
	 * 
	 * @param request
	 *            The actual request.
	 * @return All available services.
	 */
	@RequireToken
	@RequestMapping
	public ResponseEntity<List<Service>> handleGetAllCredentials(final HttpServletRequest request) {
		return ResponseEntity.ok(this.credentialsService.getAllServices());
	}

	/**
	 * Retrieves the service with the password that has the given name.
	 * 
	 * @param request
	 *            The actual request.
	 * @param name
	 *            The name of the service to retrieve.
	 * @return The service.
	 */
	@RequireToken
	@RequestMapping("/{name}")
	public ResponseEntity<Service> handleGetCredentials(final HttpServletRequest request, @PathVariable
	final String name) {
		return ResponseEntity.ok(this.credentialsService.getService(name));
	}
}
