/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @microservice:  core-command-client library
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.controller;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServiceUnavailableException;

import org.edgexfoundry.controller.CmdClient;
import org.edgexfoundry.controller.CmdClientImpl;
import org.edgexfoundry.domain.CommandDevice;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.domain.meta.DeviceService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.edgexfoundry.test.category.RequiresCommandRunning;
import org.edgexfoundry.test.category.RequiresMetaDataRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.data.AddressableData;
import org.edgexfoundry.test.data.CommandData;
import org.edgexfoundry.test.data.DeviceData;
import org.edgexfoundry.test.data.ProfileData;
import org.edgexfoundry.test.data.ServiceData;

@Category({ RequiresMongoDB.class, RequiresMetaDataRunning.class, RequiresCommandRunning.class })
public class CmdClientTest {

	private static final String ENDPT = "http://localhost:48082/api/v1/device";
	private static final String DEVICE_ENDPT = "http://localhost:48081/api/v1/device";
	private static final String SRV_ENDPT = "http://localhost:48081/api/v1/deviceservice";
	private static final String PRO_ENDPT = "http://localhost:48081/api/v1/deviceprofile";
	private static final String ADDR_ENDPT = "http://localhost:48081/api/v1/addressable";
	private static final String CMD_ENDPT = "http://localhost:48081/api/v1/command";

	private CmdClient client;
	private DeviceClient deviceClient;
	private DeviceServiceClient srvClient;
	private DeviceProfileClient proClient;
	private AddressableClient addrClient;
	private CommandClient commandClient;
	private String id;
	private String commandId;

	@Before
	public void setup() throws Exception {
		deviceClient = new DeviceClientImpl();
		srvClient = new DeviceServiceClientImpl();
		proClient = new DeviceProfileClientImpl();
		addrClient = new AddressableClientImpl();
		commandClient = new CommandClientImpl();
		client = new CmdClientImpl();
		setURL();
		Addressable a = AddressableData.newTestInstance();
		addrClient.add(a);
		DeviceService s = ServiceData.newTestInstance();
		s.setAddressable(a);
		srvClient.add(s);
		Command command = CommandData.newTestInstance();
		commandId = commandClient.add(command);
		DeviceProfile p = ProfileData.newTestInstance();
		p.addCommand(command);
		proClient.add(p);
		Device device = DeviceData.newTestInstance();
		device.setAddressable(a);
		device.setProfile(p);
		device.setService(s);
		id = deviceClient.add(device);
		assertNotNull("CommandDevice did not get created correctly", id);

	}

	private void setURL() throws Exception {
		Class<?> clientClass = deviceClient.getClass();
		Field temp = clientClass.getDeclaredField("url");
		temp.setAccessible(true);
		temp.set(deviceClient, DEVICE_ENDPT);
		Class<?> clientClass2 = proClient.getClass();
		Field temp2 = clientClass2.getDeclaredField("url");
		temp2.setAccessible(true);
		temp2.set(proClient, PRO_ENDPT);
		Class<?> clientClass3 = srvClient.getClass();
		Field temp3 = clientClass3.getDeclaredField("url");
		temp3.setAccessible(true);
		temp3.set(srvClient, SRV_ENDPT);
		Class<?> clientClass4 = addrClient.getClass();
		Field temp4 = clientClass4.getDeclaredField("url");
		temp4.setAccessible(true);
		temp4.set(addrClient, ADDR_ENDPT);
		Class<?> clientClass5 = commandClient.getClass();
		Field temp5 = clientClass5.getDeclaredField("url");
		temp5.setAccessible(true);
		temp5.set(commandClient, CMD_ENDPT);

		Class<?> clientClass6 = client.getClass();
		Field temp6 = clientClass6.getDeclaredField("url");
		temp6.setAccessible(true);
		temp6.set(client, ENDPT);
	}

	@After
	public void cleanup() {
		List<Device> devices = deviceClient.devices();
		devices.forEach((d) -> deviceClient.delete(d.getId()));
		List<DeviceProfile> ps = proClient.deviceProfiles();
		ps.forEach((p) -> proClient.delete(p.getId()));
		List<DeviceService> ds = srvClient.deviceServices();
		ds.forEach((d) -> srvClient.delete(d.getId()));
		List<Addressable> as = addrClient.addressables();
		as.forEach((a) -> addrClient.delete(a.getId()));
		List<Command> commands = commandClient.commands();
		commands.forEach((c) -> commandClient.delete(c.getId()));
	}

	@Test
	public void testDevices() {
		List<CommandDevice> responses = client.devices();
		assertEquals("Command devices returned don't match expected count", 1, responses.size());
		CommandDevice d = responses.get(0);
		checkCommandDevice(d);
	}

	@Test
	public void testDevice() {
		CommandDevice d = client.device(id);
		checkCommandDevice(d);
	}

	@Test(expected = NotFoundException.class)
	public void testDeviceWithUnknownId() {
		client.device("nosuchid");
	}

	@Test
	public void testDeviceByName() {
		CommandDevice d = client.deviceByName(DeviceData.TEST_NAME);
		checkCommandDevice(d);
	}

	@Test(expected = NotFoundException.class)
	public void testDeviceByNameWithNoneMatching() {
		client.deviceByName("badname");
	}

	// since device service will not be up and running and able to respond, all
	// we can get back is 503 from command
	@Test(expected = ServiceUnavailableException.class)
	public void testGet() {
		client.get(id, commandId);
	}

	// since device service will not be up and running and able to respond, all
	// we can get back is 503 from command
	@Test(expected = ServiceUnavailableException.class)
	public void testPut() {
		client.put(id, commandId, "{\"temp\":72}");
	}

	private void checkCommandDevice(CommandDevice d) {
		assertEquals("Command device id does not match expected", id, d.getId());
		assertEquals("Command device name does not match expected", DeviceData.TEST_NAME, d.getName());
		assertEquals("Command device admin state does not match expected", DeviceData.TEST_ADMIN, d.getAdminState());
		assertEquals("Command device description does not match expected", DeviceData.TEST_DESCRIPTION,
				d.getDescription());
		assertArrayEquals("Command device labels does not match expected", DeviceData.TEST_LABELS, d.getLabels());
		assertEquals("Command device last connected does not match expected", DeviceData.TEST_LAST_CONNECTED,
				d.getLastConnected());
		assertEquals("Command device last reported does not match expected", DeviceData.TEST_LAST_REPORTED,
				d.getLastReported());
		assertEquals("Command device location does not match expected", DeviceData.TEST_LOCATION, d.getLocation());
		assertEquals("Command device operating state does not match expected", DeviceData.TEST_OP,
				d.getOperatingState());

	}

}
