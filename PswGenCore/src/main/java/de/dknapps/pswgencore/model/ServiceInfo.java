/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile  
 *
 *     Copyright (C) 2005-2017 Uwe Damken
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
package de.dknapps.pswgencore.model;

import de.dknapps.pswgencore.util.EmptyHelper;

/**
 * <p>
 * Hält die Informationen für ein Dienstekürzel, die zum Generieren eines Passworts notwendig sind.
 * </p>
 */
public class ServiceInfo {

	private String serviceAbbreviation;

	private String additionalInfo;

	private String loginUrl;

	private String loginInfo;

	private String additionalLoginInfo;

	private boolean useSmallLetters;

	private boolean useCapitalLetters;

	private boolean useDigits;

	private boolean useSpecialCharacters;

	private String specialCharacters;

	private int smallLettersCount = EmptyHelper.EMPTY_INT;

	private int smallLettersStartIndex = EmptyHelper.EMPTY_INT;

	private int smallLettersEndIndex = EmptyHelper.EMPTY_INT;

	private int capitalLettersCount = EmptyHelper.EMPTY_INT;

	private int capitalLettersStartIndex = EmptyHelper.EMPTY_INT;

	private int capitalLettersEndIndex = EmptyHelper.EMPTY_INT;

	private int digitsCount = EmptyHelper.EMPTY_INT;

	private int specialCharactersCount = EmptyHelper.EMPTY_INT;

	private int digitsStartIndex = EmptyHelper.EMPTY_INT;

	private int digitsEndIndex = EmptyHelper.EMPTY_INT;

	private int specialCharactersStartIndex = EmptyHelper.EMPTY_INT;

	private int specialCharactersEndIndex = EmptyHelper.EMPTY_INT;

	private int totalCharacterCount = EmptyHelper.EMPTY_INT;

	private String password;

	private String passwordRepeated;

	private boolean useOldPassphrase;

	private boolean deleted;

	private String timeMillis;

	public ServiceInfo() {
	}

	public ServiceInfo(String serviceAbbreviation) {
		this.serviceAbbreviation = serviceAbbreviation;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Wird in PswGenDroid benutzt, um einen Dienst in der Liste anzuzeigen.
	 */
	public String toString() {
		return serviceAbbreviation;
	}

	/**
	 * Im EncryptionHelper wird der HashCode der Map aus ServiceInfo-Objekten verwendet, um die Passphrase zu
	 * validieren, darum wird hier hashCode() benötigt.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((additionalInfo == null) ? 0 : additionalInfo.hashCode());
		result = prime * result + ((additionalLoginInfo == null) ? 0 : additionalLoginInfo.hashCode());
		result = prime * result + capitalLettersCount;
		result = prime * result + capitalLettersEndIndex;
		result = prime * result + capitalLettersStartIndex;
		result = prime * result + digitsCount;
		result = prime * result + digitsEndIndex;
		result = prime * result + digitsStartIndex;
		result = prime * result + ((loginInfo == null) ? 0 : loginInfo.hashCode());
		result = prime * result + ((loginUrl == null) ? 0 : loginUrl.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((passwordRepeated == null) ? 0 : passwordRepeated.hashCode());
		result = prime * result + ((serviceAbbreviation == null) ? 0 : serviceAbbreviation.hashCode());
		result = prime * result + smallLettersCount;
		result = prime * result + smallLettersEndIndex;
		result = prime * result + smallLettersStartIndex;
		result = prime * result + ((specialCharacters == null) ? 0 : specialCharacters.hashCode());
		result = prime * result + specialCharactersCount;
		result = prime * result + specialCharactersEndIndex;
		result = prime * result + specialCharactersStartIndex;
		result = prime * result + totalCharacterCount;
		result = prime * result + (useCapitalLetters ? 1231 : 1237);
		result = prime * result + (useDigits ? 1231 : 1237);
		result = prime * result + (useSmallLetters ? 1231 : 1237);
		result = prime * result + (useSpecialCharacters ? 1231 : 1237);
		// Ohne useOldPassphrase, deleted und timeMillis, um eine Migration des Hashcodes zu vermeiden
		return result;
	}

	/**
	 * Gibt true zurück, wenn der Timestamp für diese Diensteinformation älter ist als die der anderen.
	 */
	public boolean olderThan(ServiceInfo otherSi) {
		return Long.parseLong(timeMillis) < Long.parseLong(otherSi.timeMillis);
	}

	/**
	 * Setzt den Timestamp auf den aktuellen Systemwert.
	 */
	public void resetTimeMillis() {
		this.timeMillis = String.valueOf(System.currentTimeMillis());
	}

	/**
	 * Ab hier nur noch generierte Getter und Setter.
	 */

	public String getServiceAbbreviation() {
		return serviceAbbreviation;
	}

	public void setServiceAbbreviation(String serviceAbbreviation) {
		this.serviceAbbreviation = serviceAbbreviation;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(String loginInfo) {
		this.loginInfo = loginInfo;
	}

	public String getAdditionalLoginInfo() {
		return additionalLoginInfo;
	}

	public void setAdditionalLoginInfo(String additionalLoginInfo) {
		this.additionalLoginInfo = additionalLoginInfo;
	}

	public boolean isUseSmallLetters() {
		return useSmallLetters;
	}

	public void setUseSmallLetters(boolean useSmallLetters) {
		this.useSmallLetters = useSmallLetters;
	}

	public boolean isUseCapitalLetters() {
		return useCapitalLetters;
	}

	public void setUseCapitalLetters(boolean useCapitalLetters) {
		this.useCapitalLetters = useCapitalLetters;
	}

	public boolean isUseDigits() {
		return useDigits;
	}

	public void setUseDigits(boolean useDigits) {
		this.useDigits = useDigits;
	}

	public boolean isUseSpecialCharacters() {
		return useSpecialCharacters;
	}

	public void setUseSpecialCharacters(boolean useSpecialCharacters) {
		this.useSpecialCharacters = useSpecialCharacters;
	}

	public int getSmallLettersCount() {
		return smallLettersCount;
	}

	public void setSmallLettersCount(int smallLettersCount) {
		this.smallLettersCount = smallLettersCount;
	}

	public int getSmallLettersStartIndex() {
		return smallLettersStartIndex;
	}

	public void setSmallLettersStartIndex(int smallLettersStartIndex) {
		this.smallLettersStartIndex = smallLettersStartIndex;
	}

	public int getSmallLettersEndIndex() {
		return smallLettersEndIndex;
	}

	public void setSmallLettersEndIndex(int smallLettersEndIndex) {
		this.smallLettersEndIndex = smallLettersEndIndex;
	}

	public int getCapitalLettersCount() {
		return capitalLettersCount;
	}

	public void setCapitalLettersCount(int capitalLettersCount) {
		this.capitalLettersCount = capitalLettersCount;
	}

	public int getCapitalLettersStartIndex() {
		return capitalLettersStartIndex;
	}

	public void setCapitalLettersStartIndex(int capitalLettersStartIndex) {
		this.capitalLettersStartIndex = capitalLettersStartIndex;
	}

	public int getCapitalLettersEndIndex() {
		return capitalLettersEndIndex;
	}

	public void setCapitalLettersEndIndex(int capitalLettersEndIndex) {
		this.capitalLettersEndIndex = capitalLettersEndIndex;
	}

	public int getDigitsCount() {
		return digitsCount;
	}

	public void setDigitsCount(int digitsCount) {
		this.digitsCount = digitsCount;
	}

	public int getSpecialCharactersCount() {
		return specialCharactersCount;
	}

	public void setSpecialCharactersCount(int specialCharactersCount) {
		this.specialCharactersCount = specialCharactersCount;
	}

	public int getDigitsStartIndex() {
		return digitsStartIndex;
	}

	public void setDigitsStartIndex(int digitsStartIndex) {
		this.digitsStartIndex = digitsStartIndex;
	}

	public int getDigitsEndIndex() {
		return digitsEndIndex;
	}

	public void setDigitsEndIndex(int digitsEndIndex) {
		this.digitsEndIndex = digitsEndIndex;
	}

	public int getSpecialCharactersStartIndex() {
		return specialCharactersStartIndex;
	}

	public void setSpecialCharactersStartIndex(int specialCharactersStartIndex) {
		this.specialCharactersStartIndex = specialCharactersStartIndex;
	}

	public int getSpecialCharactersEndIndex() {
		return specialCharactersEndIndex;
	}

	public void setSpecialCharactersEndIndex(int specialCharactersEndIndex) {
		this.specialCharactersEndIndex = specialCharactersEndIndex;
	}

	public int getTotalCharacterCount() {
		return totalCharacterCount;
	}

	public void setTotalCharacterCount(int totalCharacterCount) {
		this.totalCharacterCount = totalCharacterCount;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordRepeated() {
		return passwordRepeated;
	}

	public void setPasswordRepeated(String passwordRepeated) {
		this.passwordRepeated = passwordRepeated;
	}

	public String getSpecialCharacters() {
		return specialCharacters;
	}

	public void setSpecialCharacters(String specialCharacters) {
		this.specialCharacters = specialCharacters;
	}

	public boolean isUseOldPassphrase() {
		return useOldPassphrase;
	}

	public void setUseOldPassphrase(boolean useOldPassphrase) {
		this.useOldPassphrase = useOldPassphrase;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getTimeMillis() {
		return timeMillis;
	}

	public void setTimeMillis(String timeMillis) {
		this.timeMillis = timeMillis;
	}

}
