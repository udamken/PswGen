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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Watches the services file for changes and fires an event if it has changed.
 * 
 */
@Service
public class ServicesFileWatchService extends Thread {
	/**
	 * All listeners.
	 * 
	 */
	@Autowired
	private List<ServicesFileChangeListener> servicesFileChangeListeners;

	/**
	 * The services file.
	 * 
	 */
	@Value("${pswgenserver.services}")
	private File services;

	/**
	 * Invoked after this service has been created.
	 * 
	 * <p>
	 * Starts this service.
	 * </p>
	 * 
	 */
	@PostConstruct
	public void onPostConstruct() {
		this.fireServicesFileChange();

		this.start();
	}

	/**
	 * Invoked before this service gets destroyed.
	 * 
	 * <p>
	 * Stops this service.
	 * </p>
	 * 
	 */
	@PreDestroy
	public void onPreDestroy() {
		this.interrupt();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		FileTime lastModifiedTime;
		try {
			lastModifiedTime = Files.getLastModifiedTime(this.services.toPath());
		} catch (final IOException cause) {
			throw new RuntimeException(cause);
		}

		while (!this.isInterrupted()) {
			FileTime newModifiedTime;
			try {
				newModifiedTime = Files.getLastModifiedTime(this.services.toPath());
			} catch (final IOException cause) {
				throw new RuntimeException(cause);
			}
			if (lastModifiedTime.compareTo(newModifiedTime) < 0) {
				lastModifiedTime = newModifiedTime;

				this.fireServicesFileChange();
			}

			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (final InterruptedException dummy) {
				this.interrupt();
			}
		}
	}

	/**
	 * Fires an event on all {@link #servicesFileChangeListeners}.
	 * 
	 */
	private void fireServicesFileChange() {
		for (final ServicesFileChangeListener servicesFileChangeListener : this.servicesFileChangeListeners) {
			servicesFileChangeListener.onServicesFileChange();
		}
	}

	/**
	 * Classes implementing this interface are recognized by the {@link ServicesFileWatchService} and
	 * registered as file-change listeners.
	 * 
	 */
	public static interface ServicesFileChangeListener {
		/**
		 * Invoked when the service file changes.
		 * 
		 */
		void onServicesFileChange();
	}
}