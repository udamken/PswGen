package net.sf.pswgen;

import net.sf.pswgen.util.DomainException;
import net.sf.pswgen.util.PasswordFactory;
import junit.framework.Assert;
import junit.framework.TestCase;

public class PasswordFactoryTest extends TestCase {

	public PasswordFactoryTest() {
	}

	public void test01() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		Assert.assertEquals("KGMOCJXD", psw);
	}

	public void test02() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf.getPassword("abcdefghijklmnopqrstuvwxyz");
		Assert.assertEquals("kgmocjxd", psw);
	}

	public void test03() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
		Assert.assertEquals("kgmOCjXD", psw);
	}

	public void test04() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf.getPassword("0123456789");
		Assert.assertEquals("88408137", psw);
	}

	public void test05() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
		Assert.assertEquals("i6UMo7pv", psw);
	}

	public void test06() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf.getPassword(",.;:!$&()=?+-*/#");
		Assert.assertEquals("$=+*&-??", psw);
	}

	public void test07() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		String psw = pf
				.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,.;:!$&()=?+-*/#");
		Assert.assertEquals("kGm!CJXd", psw);
	}

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

	public void test09() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		pf.distributeCharacters(1, ":", 0, 1);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		Assert.assertEquals(":MOCJXDG", psw);
	}

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

	public void test11() {
		PasswordFactory pf = new PasswordFactory(4);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		pf.distributeCharacters(1, "abc", 0, 1);
		pf.distributeCharacters(1, "def", 1, 2);
		String psw = pf.getPassword(".");
		Assert.assertEquals("a.d.", psw);
	}

	public void test12() {
		PasswordFactory pf = new PasswordFactory(4);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630410L);
		pf.distributeCharacters(1, "abc", 0, 1);
		pf.distributeCharacters(1, "def", 1, 2);
		String psw = pf.getPassword(".");
		Assert.assertEquals("bd..", psw);
	}

	public void test13() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630409L);
		pf.distributeCharacters(1, ":", 7, 7);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		Assert.assertEquals("MOCJXDG:", psw);
	}

	public void test14() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630410L);
		pf.distributeCharacters(1, ":", 7, 7);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		Assert.assertEquals("ZRUHZFL:", psw);
	}

	public void test15() {
		PasswordFactory pf = new PasswordFactory(8);
		pf.setSeedForRandomToEnforceReproducableResults(0x19630410L);
		pf.distributeCharacters(1, ":", 6, 7);
		String psw = pf.getPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		Assert.assertEquals("ZRUHZF:L", psw);
	}

}
