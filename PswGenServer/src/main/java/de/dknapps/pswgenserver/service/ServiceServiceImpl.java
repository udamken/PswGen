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
package de.dknapps.pswgenserver.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import de.dknapps.pswgencore.model.ServiceInfo;
import de.dknapps.pswgencore.model.ServiceInfoList;
import de.dknapps.pswgencore.util.EncryptionHelper;
import de.dknapps.pswgencore.util.FileHelper;
import de.dknapps.pswgencore.util.PasswordFactory;
import de.dknapps.pswgenserver.util.CommonJsonReaderWriterFactoryGsonImpl;

/**
 * Implementation of {@link ServiceService}.
 * 
 */
@org.springframework.stereotype.Service
@CacheConfig(cacheNames = "credentials")
public class ServiceServiceImpl implements ServiceService {
	/**
	 * The logger.
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceServiceImpl.class);

	/**
	 * The passphrase used to decrypt the services file.
	 * 
	 */
	@Value("${pswgenserver.passphrase}")
	private String passphrase;
	/**
	 * The services file.
	 * 
	 */
	@Value("${pswgenserver.services}")
	private File services;

	/**
	 * The current parsed and decrypted services list.
	 * 
	 */
	private ServiceInfoList serviceList;

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.dknapps.pswgenserver.service.ServicesFileWatchService.ServicesFileChangeListener#onServicesFileChange()
	 */
	@Override
	@CacheEvict(allEntries = true)
	public void onServicesFileChange() {
		ServiceServiceImpl.LOGGER
				.info("Loading service list from <" + this.services.getAbsolutePath() + ">.");

		final ServiceInfoList serviceList = FileHelper
				.getInstance(new CommonJsonReaderWriterFactoryGsonImpl()).loadServiceInfoList(this.services);
		final EncryptionHelper encryptionHelper = new EncryptionHelper(this.passphrase.toCharArray(),
				serviceList.getSaltAsHexString(), serviceList.getInitializerAsHexString());
		serviceList.decrypt(encryptionHelper);

		this.serviceList = serviceList;

		ServiceServiceImpl.LOGGER.info("Service list loaded!");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.dknapps.pswgenserver.service.ServiceService#getAllServices()
	 */
	@Override
	@Cacheable
	public List<Service> getAllServices() {
		ServiceServiceImpl.LOGGER.info("Retrieving all credentials.");

		final List<Service> result = new ArrayList<>();
		for (final ServiceInfo service : this.serviceList.getServices()) {
			result.add(new Service(service.getServiceAbbreviation(), service.getLoginUrl()));
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.dknapps.pswgenserver.service.ServiceService#getService(java.lang.String)
	 */
	@Override
	@Cacheable
	public Service getService(final String name) {
		ServiceServiceImpl.LOGGER.info("Retrieving credentials with name <" + name + ">.");

		final ServiceInfo service = this.serviceList.getServiceInfo(name);
		if (service == null) {
			return null;
		}
		return new Service(service.getServiceAbbreviation(), service.getLoginUrl(),
				PasswordFactory.getPassword(service, this.passphrase));
	}
}
