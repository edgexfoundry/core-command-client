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
package org.edgefoundry.command.client;

import java.net.URI;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

public abstract class ConsulDiscoveryClientTemplate {

	public static final String COMMAND_APP_ID = "edgex-core-command";
	private static boolean IS_CACHE_DISCOVERY_RESULT = false;


	@Autowired
	private DiscoveryClient discoveryClient;

	private String commandRootUrl = "";
	private String path = "";

	@Value("${client.is-cache-discovery-result:false}")
	public void setIsCacheDiscoveryResult(boolean flag) {
		IS_CACHE_DISCOVERY_RESULT = flag;
	}

	@PostConstruct
	private void initClient() {
		commandRootUrl = retrieveUriFromDiscoveryClient();
		path = extractPath();
	}

	private String retrieveUriFromDiscoveryClient() {
		String result = "";
		if (discoveryClient == null) {
			return result;
		}
		
		List<ServiceInstance> list = discoveryClient.getInstances(COMMAND_APP_ID);
		if (list != null && list.size() > 0) {
			URI uri = list.get(0).getUri();
			if (uri != null) {
				result = uri.toString();
			}
		}
		return result;
	}

	protected abstract String extractPath();

	public String getCommandRootUrl() {
		if (commandRootUrl == null || commandRootUrl.isEmpty() || !IS_CACHE_DISCOVERY_RESULT) {
			commandRootUrl = retrieveUriFromDiscoveryClient();
		}
		return commandRootUrl;
	}

	public String getPath() {
		if (path == null || path.isEmpty()) {
			path = extractPath();
		}
		return path;
	}

}
