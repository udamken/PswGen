/*
 * #%L
 * PswGen
 * %%
 * Copyright (C) 2005 - 2016 Uwe Damken
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.dknapps.pswgen.model;

import de.dknapps.pswgen.util.EmptyHelper;

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

	/**
	 * Defaultkonstruktor für JAXB zum Laden der Daten aus XML.
	 */
	public ServiceInfo() {
		super();
	}

	/**
	 * Der Konstruktor für die "richtige" Arbeit.
	 */
	public ServiceInfo(String serviceAbbreviation) {
		this();
		this.serviceAbbreviation = serviceAbbreviation;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return serviceAbbreviation;
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

}
