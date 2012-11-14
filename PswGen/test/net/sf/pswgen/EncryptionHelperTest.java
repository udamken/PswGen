package net.sf.pswgen;

import net.sf.pswgen.util.EncryptionHelper;
import junit.framework.Assert;
import junit.framework.TestCase;

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
		Assert.assertEquals(
				"familie-damken.de",
				EncryptionHelper.decrypt("passphrase",
						EncryptionHelper.encrypt("passphrase", "familie-damken.de")));
		Assert.assertEquals(
				"1234567890 / defghijkl",
				EncryptionHelper.decrypt("passphrase",
						EncryptionHelper.encrypt("passphrase", "1234567890 / defghijkl")));
	}

}
