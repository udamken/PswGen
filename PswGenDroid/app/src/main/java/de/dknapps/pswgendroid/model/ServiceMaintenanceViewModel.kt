/************************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile
 *
 *     Copyright (C) 2005-2018 Uwe Damken
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
 ************************************************************************************/
package de.dknapps.pswgendroid.model

import androidx.lifecycle.ViewModel
import de.dknapps.pswgencore.model.ServiceInfo
import de.dknapps.pswgencore.model.ServiceInfoList
import java.io.File

class ServiceMaintenanceViewModel : ViewModel() {

    enum class InputMethodPickingState {
        NONE, INITIATING, ONGOING
    }

    /**
     * Validated passphrase (filled after encryption of the file) to be used to generate passwords.
     */
    var validatedPassphrase: String? = null

    /**
     * Entered passphrase to be used to generate passwords that are marked to use the old passphrase.
     */
    var oldPassphrase: String? = null

    /**
     * File to write the list of services to
     */
    var servicesFile: File? = null

    /**
     * All services loaded from file.
     */
    var services: ServiceInfoList? = null

    /**
     * Currently selected abbreviation of a service.
     */
    var currentServiceAbbreviation: String? = null
    /**
     * Currently selected service.
     */
    lateinit var currentServiceInfo: ServiceInfo

    /**
     * Entered or generated password from currently selected service.
     */
    var password: String? = null

    /**
     * Readable explanation to the password from currently selected service.
     */
    var passwordExplanation: String? = null

    /**
     * Currently edited service.
     */
    var editedServiceInfo: ServiceInfo? = null

    /**
     * Dirty tag - has the currently edited service been changed?
     */
    var isDirty = false

    init {
        resetModel()
    }

    override fun onCleared() {
        resetModel()
        super.onCleared()
    }

    fun retrieveService(): Boolean {
        val si = services?.getServiceInfo(currentServiceAbbreviation) ?: return false
        currentServiceInfo = si
        return true
    }

    fun resetModel() {
        validatedPassphrase = null
        oldPassphrase = null
        services = null
        currentServiceAbbreviation = null
        password = null
        passwordExplanation = null
    }

}
