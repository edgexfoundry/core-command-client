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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.edgefoundry.command.client.ConsulDiscoveryClientTemplate;
import org.edgexfoundry.domain.CommandDevice;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.edgexfoundry.exception.controller.DataValidationException;

@Component
public class CmdClientImpl extends ConsulDiscoveryClientTemplate implements CmdClient {

	@Value("${core.db.command.url}")
	String url;

	@Override
	public List<CommandDevice> devices() {
		return getClient().devices();
	}

	@Override
	public CommandDevice device(String id) {
		return getClient().device(id);
	}

	@Override
	public CommandDevice deviceByName(String name) {
		return getClient().deviceByName(name);
	}

	@Override
	public String put(String id, String commandId, String body) {
		return getClient().put(id, commandId, body);
	}

	@Override
	public String get(String id, String commandId) {
		return getClient().get(id, commandId);
	}

	@Override
	protected String extractPath() {
		String result = "";
		try {
			URL urlObject = new URL(url);
			result = urlObject.getPath();
		} catch (MalformedURLException e) {
			throw new DataValidationException("the URL is malformed, core.db.command.url: " + url);
		}
		return result;
	}

	private CmdClient getClient() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target;

		String commandRootUrl = super.getCommandRootUrl();
		if (commandRootUrl == null || commandRootUrl.isEmpty()) {
			target = client.target(url);
		} else {
			target = client.target(commandRootUrl + super.getPath());
		}

		return target.proxy(CmdClient.class);
	}
}
