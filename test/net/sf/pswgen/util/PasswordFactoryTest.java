package net.sf.pswgen.util;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them
 PswGenDroid - Generates your passwords managed by PswGen on your mobile  

 Copyright (C) 2005-2014 Uwe Damken

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/

import junit.framework.TestCase;
import net.sf.pswgen.model.ServiceInfo;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * Testklasse für PasswordFactory.
 * </p>
 * <p>
 * Copyright (C) 2005-2014 Uwe Damken
 * </p>
 */
public class PasswordFactoryTest extends TestCase {

	@Test
	public void test01() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		Assert.assertEquals("KGMOCJXD", psw);
	}

	@Test
	public void test02() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf.getPassword("abcdefghijklmnopqrstuvwxyz");
		Assert.assertEquals("kgmocjxd", psw);
	}

	@Test
	public void test03() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
		Assert.assertEquals("kgmOCjXD", psw);
	}

	@Test
	public void test04() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf.getPassword("0123456789");
		Assert.assertEquals("88408137", psw);
	}

	@Test
	public void test05() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
		Assert.assertEquals("i6UMo7pv", psw);
	}

	@Test
	public void test06() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf.getPassword(",.;:!$&()=?+-*/#");
		Assert.assertEquals("$=+*&-??", psw);
	}

	@Test
	public void test07() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf
				.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,.;:!$&()=?+-*/#");
		Assert.assertEquals("kGm!CJXd", psw);
	}

	@Test
	public void test08() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		pf.distributeCharacters(1, "a", 0, 0);
		try {
			pf.distributeCharacters(2, "d", 0, 0);
			Assert.fail("The previous statement should have thrown an IllegalArgumentException");
		} catch (DomainException illegalargumentexception) {
		}
	}

	@Test
	public void test09() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		pf.distributeCharacters(1, ":", 0, 1);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		Assert.assertEquals(":MOCJXDG", psw);
	}

	@Test
	public void test10() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		pf.distributeCharacters(2, "abc", 0, 1);
		try {
			pf.distributeCharacters(2, "def", 1, 2);
			Assert.fail("The previous statement should have thrown an IllegalArgumentException");
		} catch (DomainException illegalargumentexception) {
		}
	}

	@Test
	public void test11() {
		PasswordFactory pf = new PasswordFactory(4);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		pf.distributeCharacters(1, "abc", 0, 1);
		pf.distributeCharacters(1, "def", 1, 2);
		String psw = pf.getPassword(".");
		Assert.assertEquals("a.d.", psw);
	}

	@Test
	public void test12() {
		PasswordFactory pf = new PasswordFactory(4);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630410L);
		pf.distributeCharacters(1, "abc", 0, 1);
		pf.distributeCharacters(1, "def", 1, 2);
		String psw = pf.getPassword(".");
		Assert.assertEquals("bd..", psw);
	}

	@Test
	public void test13() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		pf.distributeCharacters(1, ":", 7, 7);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		Assert.assertEquals("MOCJXDG:", psw);
	}

	@Test
	public void test14() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630410L);
		pf.distributeCharacters(1, ":", 7, 7);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		Assert.assertEquals("ZRUHZFL:", psw);
	}

	@Test
	public void test15() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630410L);
		pf.distributeCharacters(1, ":", 6, 7);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		Assert.assertEquals("ZRUHZF:L", psw);
	}

	@Test
	public void test16() {
		ServiceInfo si = new ServiceInfo("test");
		si.setAdditionalInfo("10.08.2014");
		si.setUseSmallLetters(true);
		si.setUseCapitalLetters(true);
		si.setUseDigits(true);
		si.setUseSpecialCharacters(true);
		si.setSpecialCharacters(Constants.SPECIAL_CHARS);
		si.setTotalCharacterCount(20);
		String psw = PasswordFactory.getPassword(si, "test4711");
		Assert.assertEquals("y7zMGUS.Ixm&B#*)vhuB", psw);
	}

	@Test
	public void test16a() {
		ServiceInfo si = new ServiceInfo("test");
		si.setAdditionalInfo("10.08.2014");
		si.setUseSmallLetters(true);
		si.setUseCapitalLetters(true);
		si.setUseDigits(true);
		si.setUseSpecialCharacters(true);
		si.setSpecialCharacters(null);
		si.setTotalCharacterCount(20);
		String psw = PasswordFactory.getPassword(si, "test4711");
		Assert.assertEquals("y7zMGUS.Ixm&B#*)vhuB", psw);
	}

	@Test
	public void test16b() {
		ServiceInfo si = new ServiceInfo("test");
		si.setAdditionalInfo("10.08.2014");
		si.setUseSmallLetters(true);
		si.setUseCapitalLetters(true);
		si.setUseDigits(true);
		si.setUseSpecialCharacters(true);
		si.setSpecialCharacters("");
		si.setTotalCharacterCount(20);
		String psw = PasswordFactory.getPassword(si, "test4711");
		Assert.assertEquals("y7zMGUS.Ixm&B#*)vhuB", psw);
	}

	@Test
	public void test17() {
		ServiceInfo si = new ServiceInfo("test");
		si.setAdditionalInfo("10.08.2014");
		si.setUseSmallLetters(true);
		si.setUseCapitalLetters(true);
		si.setUseDigits(true);
		si.setUseSpecialCharacters(true);
		si.setSpecialCharacters("$%");
		si.setSpecialCharactersCount(1);
		si.setTotalCharacterCount(20);
		String psw = PasswordFactory.getPassword(si, "test4711");
		Assert.assertEquals("jo4AKvkD0E$VJfKZ5ehh", psw);
	}

	@Test
	public void test18() {
		ServiceInfo si = new ServiceInfo("test");
		si.setAdditionalInfo("10.08.2014");
		si.setUseSmallLetters(true);
		si.setUseCapitalLetters(true);
		si.setUseDigits(true);
		si.setUseSpecialCharacters(true);
		si.setSpecialCharacters(Constants.SPECIAL_CHARS);
		si.setSpecialCharactersCount(1);
		si.setSpecialCharactersStartIndex(19);
		si.setTotalCharacterCount(20);
		String psw = PasswordFactory.getPassword(si, "test4711");
		Assert.assertEquals("jo4AKvkD0EVJfKZ5ehh!", psw);
	}

}
