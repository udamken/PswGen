package net.sf.pswgen.util;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them

 Copyright (C) 2005-2013 Uwe Damken

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

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.pswgen.util.EncryptionHelper;

/**
 * <p>
 * Testklasse für EncryptionHelper.
 * </p>
 * <p>
 * Copyright (C) 2005-2013 Uwe Damken
 * </p>
 */
public class EncryptionHelperTest extends TestCase {

	public EncryptionHelperTest() {
	}

	public void test01() {
		Assert.assertEquals("00", EncryptionHelper.toHexString((new Integer(0)).byteValue()));
		Assert.assertEquals("01", EncryptionHelper.toHexString((new Integer(1)).byteValue()));
		Assert.assertEquals("0F", EncryptionHelper.toHexString((new Integer(15)).byteValue()));
		Assert.assertEquals("10", EncryptionHelper.toHexString((new Integer(16)).byteValue()));
		Assert.assertEquals("7F", EncryptionHelper.toHexString((new Integer(127)).byteValue()));
		Assert.assertEquals("80", EncryptionHelper.toHexString((new Integer(128)).byteValue()));
		Assert.assertEquals("FF", EncryptionHelper.toHexString((new Integer(255)).byteValue()));
	}

	public void test02() {
		Assert.assertEquals((new Integer(0)).byteValue(), EncryptionHelper.toByte("0"));
		Assert.assertEquals((new Integer(0)).byteValue(), EncryptionHelper.toByte("00"));
		Assert.assertEquals((new Integer(1)).byteValue(), EncryptionHelper.toByte("1"));
		Assert.assertEquals((new Integer(1)).byteValue(), EncryptionHelper.toByte("01"));
		Assert.assertEquals((new Integer(15)).byteValue(), EncryptionHelper.toByte("F"));
		Assert.assertEquals((new Integer(15)).byteValue(), EncryptionHelper.toByte("0F"));
		Assert.assertEquals((new Integer(16)).byteValue(), EncryptionHelper.toByte("10"));
		Assert.assertEquals((new Integer(127)).byteValue(), EncryptionHelper.toByte("7F"));
		Assert.assertEquals((new Integer(128)).byteValue(), EncryptionHelper.toByte("80"));
		Assert.assertEquals((new Integer(255)).byteValue(), EncryptionHelper.toByte("FF"));
	}

	public void test03() {
		Assert.assertEquals("0000", EncryptionHelper.toHexString(EncryptionHelper.toByteArray("0000")));
		Assert.assertEquals("1234", EncryptionHelper.toHexString(EncryptionHelper.toByteArray("1234")));
		Assert.assertEquals("F00F", EncryptionHelper.toHexString(EncryptionHelper.toByteArray("F00F")));
		Assert.assertEquals("FFFF", EncryptionHelper.toHexString(EncryptionHelper.toByteArray("FFFF")));
	}

	public void test04() {
		String domainEncrypted = EncryptionHelper.encrypt("passphrase", "familie-damken.de");
		Assert.assertEquals("9351384B3EAE15B20CBA3CFD0D8D8E804ADFD449CC1E691C", domainEncrypted);
		Assert.assertEquals("familie-damken.de", EncryptionHelper.decrypt("passphrase", domainEncrypted));
		Assert.assertEquals(
				"1234567890 / defghijkl",
				EncryptionHelper.decrypt("passphrase",
						EncryptionHelper.encrypt("passphrase", "1234567890 / defghijkl")));
	}

}
