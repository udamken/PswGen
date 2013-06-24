package net.sf.pswgen.model;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them

 Copyright (C) 2005-2012  Uwe Damken

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *****************************************************************************/

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Hält die Informationen für ein Dienstekürzel, die zum Generieren eines Passworts notwendig sind.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 */
@XmlRootElement(name = "ServiceInfo")
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

	private int smallLettersCount;

	private int smallLettersStartIndex;

	private int smallLettersEndIndex;

	private int capitalLettersCount;

	private int capitalLettersStartIndex;

	private int capitalLettersEndIndex;

	private int digitsCount;

	private int specialCharactersCount;

	private int digitsStartIndex;

	private int digitsEndIndex;

	private int specialCharactersStartIndex;

	private int specialCharactersEndIndex;

	private int totalCharacterCount;

	private String password;

	private String passwordRepeated;

	/**
	 * Defaultkonstruktor für JAXB zum Laden der Daten aus XML.
	 */
	public ServiceInfo() {
		super();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
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

	/**
	 * Der Konstruktor für die "richtige" Arbeit.
	 */
	public ServiceInfo(String serviceAbbreviation) {
		this();
		this.serviceAbbreviation = serviceAbbreviation;
	}

}
