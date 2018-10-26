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
package de.dknapps.pswgendroid.adapter

import android.content.Context
import android.util.Log
import android.widget.Toast
import de.dknapps.pswgencore.model.ServiceInfoList
import de.dknapps.pswgencore.util.DomainException
import de.dknapps.pswgencore.util.FileHelper
import de.dknapps.pswgendroid.DroidConstants.Companion.TAG
import de.dknapps.pswgendroid.R
import java.io.File

/**
 * Class to adapt PswGenDroid to PswGenCore.
 */
object PswGenAdapter {

    /**
     * Returns list of services read from files and merged into one or throws a DomainException on failure.
     */
    fun loadServiceInfoList(servicesFile: File, otherServicesFile: File, passphrase: String): ServiceInfoList {
        val fileHelper = FileHelper.getInstance(CommonJsonReaderWriterFactoryAndroidImpl())
        return fileHelper.loadServiceInfoList(servicesFile, otherServicesFile, passphrase)!!
    }

    /**
     * Bring out a toast after translating a domain exception to messages text.
     */
    fun handleThrowable(context: Context, t: Throwable) {
        if (t is DomainException) {
            Log.e(TAG, "DomainException caught: ", t)
            Toast.makeText(context, getDomainExceptionText(context, t), Toast.LENGTH_LONG)
                .show()
        } else {
            Log.e(TAG, "Throwable caught: ", t)
            Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Returns the resid of the text that corresponds with the text of the DomainException.
     */
    private fun getDomainExceptionText(context: Context, domainException: DomainException): String {
        when (domainException.message) {
            "PassphraseInvalidMsg" -> return context.getString(R.string.PassphraseInvalidMsg)
            "FileCouldNotBeOpenedMsg" -> return context.getString(R.string.FileCouldNotBeOpenedMsg)
            "UnsupportedFileFormatMsg" -> return context.getString(R.string.UnsupportedFileFormatMsg)
            "TotalCharacterCountExceededMsg" -> return context.getString(R.string.TotalCharacterCountExceededMsg)
            "InvalidCharacterCountMsg" -> return context.getString(R.string.InvalidCharacterCountMsg)
            "CharacterOrTotalCharacterCountMissingMsg" -> return context.getString(R.string.CharacterOrTotalCharacterCountMissingMsg)
            "PassphraseMismatchMsg" -> return context.getString(R.string.PassphraseMismatchMsg)
            "ServiceAbbreviationEmptyMsg" -> return context.getString(R.string.ServiceAbbreviationEmptyMsg)
            "ServiceAbbreviationMissingMsg" -> return context.getString(R.string.ServiceAbbreviationMissingMsg)
            "PassphraseEmptyMsg" -> return context.getString(R.string.PassphraseEmptyMsg)
            "NewPassphraseMismatchMsg" -> return context.getString(R.string.NewPassphraseMismatchMsg)
            "OldPassphraseEmptyMsg" -> return context.getString(R.string.OldPassphraseEmptyMsg)
            "NewPassphraseEmptyMsg" -> return context.getString(R.string.NewPassphraseEmptyMsg)
            "PasswordMismatchMsg" -> return context.getString(R.string.PasswordMismatchMsg)
            else -> return "Unknown error message: ${domainException.message}"
        }
    }

}
