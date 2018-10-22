/*******************************************************************************
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
 *******************************************************************************/
package de.dknapps.pswgencore.model;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * <p>
 * Testklasse für ServiceInfoList.
 * </p>
 */
public class ServiceInfoListTest extends TestCase {

	@Test
	public void test_mergeEncrypted_justAddAll() {
		ServiceInfoList local = new ServiceInfoList();
		local.putServiceInfo(createServiceInfo("a", "A", false, "1000"));
		local.putServiceInfo(createServiceInfo("b", "B", false, "2000"));
		assertEquals(2, local.getServices(true).size());
		assertEquals(2, local.getServices(false).size());
		ServiceInfoList remote = new ServiceInfoList();
		remote.putServiceInfo(createServiceInfo("c", "C", false, "3000"));
		remote.putServiceInfo(createServiceInfo("d", "D", false, "4000"));
		assertEquals(2, remote.getServices(true).size());
		assertEquals(2, remote.getServices(false).size());
		local.merge(remote);
		assertEquals(4, local.getServices(true).size());
		assertEquals(4, local.getServices(false).size());
		assertServiceInfo(createServiceInfo("a", "A", false, "1000"), local, 0);
		assertServiceInfo(createServiceInfo("b", "B", false, "2000"), local, 1);
		assertServiceInfo(createServiceInfo("c", "C", false, "3000"), local, 2);
		assertServiceInfo(createServiceInfo("d", "D", false, "4000"), local, 3);
	}

	@Test
	public void test_mergeEncrypted_ignoreDeletionWithSameTimeMillis() {
		ServiceInfoList local = new ServiceInfoList();
		local.putServiceInfo(createServiceInfo("a", "A", false, "1000"));
		local.putServiceInfo(createServiceInfo("b", "B", false, "2000"));
		assertEquals(2, local.getServices(true).size());
		assertEquals(2, local.getServices(false).size());
		ServiceInfoList remote = new ServiceInfoList();
		remote.putServiceInfo(createServiceInfo("a", "A", true, "1000")); // not newer then local entry
		remote.putServiceInfo(createServiceInfo("c", "C", false, "3000"));
		remote.putServiceInfo(createServiceInfo("d", "D", false, "4000"));
		assertEquals(3, remote.getServices(true).size());
		assertEquals(2, remote.getServices(false).size());
		local.merge(remote);
		assertEquals(4, local.getServices(true).size());
		assertEquals(4, local.getServices(false).size());
		assertServiceInfo(createServiceInfo("a", "A", false, "1000"), local, 0); // not deleted
		assertServiceInfo(createServiceInfo("b", "B", false, "2000"), local, 1);
		assertServiceInfo(createServiceInfo("c", "C", false, "3000"), local, 2);
		assertServiceInfo(createServiceInfo("d", "D", false, "4000"), local, 3);
	}

	@Test
	public void test_mergeEncrypted_deletionByNewerEntry() {
		ServiceInfoList local = new ServiceInfoList();
		local.putServiceInfo(createServiceInfo("a", "A", false, "1000"));
		local.putServiceInfo(createServiceInfo("b", "B", false, "2000"));
		assertEquals(2, local.getServices(true).size());
		assertEquals(2, local.getServices(false).size());
		ServiceInfoList remote = new ServiceInfoList();
		remote.putServiceInfo(createServiceInfo("a", "A", true, "1001")); // delete this one
		remote.putServiceInfo(createServiceInfo("c", "C", false, "3000"));
		remote.putServiceInfo(createServiceInfo("d", "D", false, "4000"));
		assertEquals(3, remote.getServices(true).size());
		assertEquals(2, remote.getServices(false).size());
		local.merge(remote);
		assertEquals(4, local.getServices(true).size());
		assertEquals(3, local.getServices(false).size());
		assertServiceInfo(createServiceInfo("a", "A", true, "1001"), local, 0); // deleted
		assertServiceInfo(createServiceInfo("b", "B", false, "2000"), local, 1);
		assertServiceInfo(createServiceInfo("c", "C", false, "3000"), local, 2);
		assertServiceInfo(createServiceInfo("d", "D", false, "4000"), local, 3);
	}

	@Test
	public void test_mergeEncrypted_ignoreModificationWithSameTimeMillis() {
		ServiceInfoList local = new ServiceInfoList();
		local.putServiceInfo(createServiceInfo("a", "A", false, "1000"));
		local.putServiceInfo(createServiceInfo("b", "B", false, "2000"));
		assertEquals(2, local.getServices(true).size());
		assertEquals(2, local.getServices(false).size());
		ServiceInfoList remote = new ServiceInfoList();
		remote.putServiceInfo(createServiceInfo("a", "A'", false, "1000")); // not newer then local entry
		remote.putServiceInfo(createServiceInfo("c", "C", false, "3000"));
		remote.putServiceInfo(createServiceInfo("d", "D", false, "4000"));
		assertEquals(3, remote.getServices(true).size());
		assertEquals(3, remote.getServices(false).size());
		local.merge(remote);
		assertEquals(4, local.getServices(true).size());
		assertEquals(4, local.getServices(false).size());
		assertServiceInfo(createServiceInfo("a", "A", false, "1000"), local, 0); // not modifed
		assertServiceInfo(createServiceInfo("b", "B", false, "2000"), local, 1);
		assertServiceInfo(createServiceInfo("c", "C", false, "3000"), local, 2);
		assertServiceInfo(createServiceInfo("d", "D", false, "4000"), local, 3);
	}

	@Test
	public void test_mergeEncrypted_modificationByNewerEntry() {
		ServiceInfoList local = new ServiceInfoList();
		local.putServiceInfo(createServiceInfo("a", "A", false, "1000"));
		local.putServiceInfo(createServiceInfo("b", "B", false, "2000"));
		assertEquals(2, local.getServices(true).size());
		assertEquals(2, local.getServices(false).size());
		ServiceInfoList remote = new ServiceInfoList();
		remote.putServiceInfo(createServiceInfo("a", "A'", false, "1001")); // modify this one
		remote.putServiceInfo(createServiceInfo("c", "C", false, "3000"));
		remote.putServiceInfo(createServiceInfo("d", "D", false, "4000"));
		assertEquals(3, remote.getServices(true).size());
		assertEquals(3, remote.getServices(false).size());
		local.merge(remote);
		assertEquals(4, local.getServices(true).size());
		assertEquals(4, local.getServices(false).size());
		assertServiceInfo(createServiceInfo("a", "A'", false, "1001"), local, 0); // modified
		assertServiceInfo(createServiceInfo("b", "B", false, "2000"), local, 1);
		assertServiceInfo(createServiceInfo("c", "C", false, "3000"), local, 2);
		assertServiceInfo(createServiceInfo("d", "D", false, "4000"), local, 3);
	}

	@Test
	public void test_mergeEncrypted_ignoreModificationAndDeletionWithSameTimeMillis() {
		ServiceInfoList local = new ServiceInfoList();
		local.putServiceInfo(createServiceInfo("a", "A", false, "1000"));
		local.putServiceInfo(createServiceInfo("b", "B", false, "2000"));
		assertEquals(2, local.getServices(true).size());
		assertEquals(2, local.getServices(false).size());
		ServiceInfoList remote = new ServiceInfoList();
		remote.putServiceInfo(createServiceInfo("a", "A'", true, "1000")); // not newer then local entry
		remote.putServiceInfo(createServiceInfo("c", "C", false, "3000"));
		remote.putServiceInfo(createServiceInfo("d", "D", false, "4000"));
		assertEquals(3, remote.getServices(true).size());
		assertEquals(2, remote.getServices(false).size());
		local.merge(remote);
		assertEquals(4, local.getServices(true).size());
		assertEquals(4, local.getServices(false).size());
		assertServiceInfo(createServiceInfo("a", "A", false, "1000"), local, 0); // not modifed, not deleted
		assertServiceInfo(createServiceInfo("b", "B", false, "2000"), local, 1);
		assertServiceInfo(createServiceInfo("c", "C", false, "3000"), local, 2);
		assertServiceInfo(createServiceInfo("d", "D", false, "4000"), local, 3);
	}

	@Test
	public void test_mergeEncrypted_modificationAndDeletionByNewerEntry() {
		ServiceInfoList local = new ServiceInfoList();
		local.putServiceInfo(createServiceInfo("a", "A", false, "1000"));
		local.putServiceInfo(createServiceInfo("b", "B", false, "2000"));
		assertEquals(2, local.getServices(true).size());
		assertEquals(2, local.getServices(false).size());
		ServiceInfoList remote = new ServiceInfoList();
		remote.putServiceInfo(createServiceInfo("a", "A'", true, "1001")); // modify and delete this one
		remote.putServiceInfo(createServiceInfo("c", "C", false, "3000"));
		remote.putServiceInfo(createServiceInfo("d", "D", false, "4000"));
		assertEquals(3, remote.getServices(true).size());
		assertEquals(2, remote.getServices(false).size());
		local.merge(remote);
		assertEquals(4, local.getServices(true).size());
		assertEquals(3, local.getServices(false).size());
		assertServiceInfo(createServiceInfo("a", "A'", true, "1001"), local, 0); // modified and deleted
		assertServiceInfo(createServiceInfo("b", "B", false, "2000"), local, 1);
		assertServiceInfo(createServiceInfo("c", "C", false, "3000"), local, 2);
		assertServiceInfo(createServiceInfo("d", "D", false, "4000"), local, 3);
	}

	@Test
	public void test_mergeEncrypted_ignoreRecreattionWithSameTimeMillis() {
		ServiceInfoList local = new ServiceInfoList();
		local.putServiceInfo(createServiceInfo("a", "A", true, "1000"));
		local.putServiceInfo(createServiceInfo("b", "B", false, "2000"));
		assertEquals(2, local.getServices(true).size());
		assertEquals(1, local.getServices(false).size());
		ServiceInfoList remote = new ServiceInfoList();
		remote.putServiceInfo(createServiceInfo("a", "A'", false, "1000")); // not newer then local entry
		remote.putServiceInfo(createServiceInfo("c", "C", false, "3000"));
		remote.putServiceInfo(createServiceInfo("d", "D", false, "4000"));
		assertEquals(3, remote.getServices(true).size());
		assertEquals(3, remote.getServices(false).size());
		local.merge(remote);
		assertEquals(4, local.getServices(true).size());
		assertEquals(3, local.getServices(false).size());
		assertServiceInfo(createServiceInfo("a", "A", true, "1000"), local, 0); // not recreated
		assertServiceInfo(createServiceInfo("b", "B", false, "2000"), local, 1);
		assertServiceInfo(createServiceInfo("c", "C", false, "3000"), local, 2);
		assertServiceInfo(createServiceInfo("d", "D", false, "4000"), local, 3);
	}

	@Test
	public void test_mergeEncrypted_recreationByNewerEntry() {
		ServiceInfoList local = new ServiceInfoList();
		local.putServiceInfo(createServiceInfo("a", "A", true, "1000"));
		local.putServiceInfo(createServiceInfo("b", "B", false, "2000"));
		assertEquals(2, local.getServices(true).size());
		assertEquals(1, local.getServices(false).size());
		ServiceInfoList remote = new ServiceInfoList();
		remote.putServiceInfo(createServiceInfo("a", "A'", false, "1001")); // recreate this one
		remote.putServiceInfo(createServiceInfo("c", "C", false, "3000"));
		remote.putServiceInfo(createServiceInfo("d", "D", false, "4000"));
		assertEquals(3, remote.getServices(true).size());
		assertEquals(3, remote.getServices(false).size());
		local.merge(remote);
		assertEquals(4, local.getServices(true).size());
		assertEquals(4, local.getServices(false).size());
		assertServiceInfo(createServiceInfo("a", "A'", false, "1001"), local, 0); // recreated
		assertServiceInfo(createServiceInfo("b", "B", false, "2000"), local, 1);
		assertServiceInfo(createServiceInfo("c", "C", false, "3000"), local, 2);
		assertServiceInfo(createServiceInfo("d", "D", false, "4000"), local, 3);
	}

	private ServiceInfo createServiceInfo(String serviceAbbreviation, String additionalInfo, boolean deleted,
			String timeMillis) {
		ServiceInfo si = new ServiceInfo();
		si.setServiceAbbreviation(serviceAbbreviation);
		si.setAdditionalInfo(additionalInfo);
		si.setDeleted(deleted);
		si.setTimeMillis(timeMillis);
		return si;
	}

	private void assertServiceInfo(ServiceInfo expected, ServiceInfoList local, int i) {
		ServiceInfo actual = local.getServices(true).toArray(new ServiceInfo[0])[i];
		assertEquals(expected.getServiceAbbreviation(), actual.getServiceAbbreviation());
		assertEquals(expected.isDeleted(), actual.isDeleted());
		assertEquals(expected.getTimeMillis(), actual.getTimeMillis());
	}

}
