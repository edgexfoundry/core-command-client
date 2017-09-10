/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: core-command-client library
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.domain;

import java.util.List;

import org.edgexfoundry.domain.meta.AdminState;
import org.edgexfoundry.domain.meta.OperatingState;

public class CommandDevice {
  private String id;
  private String name;
  private String description;
  private AdminState adminState;
  private OperatingState operatingState;
  private long lastConnected;
  private long lastReported;
  private String[] labels;
  private Object location;
  private long responseDelay;
  private List<Command> commands;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public AdminState getAdminState() {
    return adminState;
  }

  public void setAdminState(AdminState adminState) {
    this.adminState = adminState;
  }

  public OperatingState getOperatingState() {
    return operatingState;
  }

  public void setOperatingState(OperatingState operatingState) {
    this.operatingState = operatingState;
  }

  public long getLastConnected() {
    return lastConnected;
  }

  public void setLastConnected(long lastConnected) {
    this.lastConnected = lastConnected;
  }

  public long getLastReported() {
    return lastReported;
  }

  public void setLastReported(long lastReported) {
    this.lastReported = lastReported;
  }

  public String[] getLabels() {
    return labels;
  }

  public void setLabels(String[] labels) {
    this.labels = labels;
  }

  public Object getLocation() {
    return location;
  }

  public void setLocation(Object location) {
    this.location = location;
  }

  public long getResponseDelay() {
    return responseDelay;
  }

  public void setResponseDelay(long responseDelay) {
    this.responseDelay = responseDelay;
  }

  public List<Command> getCommands() {
    return commands;
  }

  public void setCommands(List<Command> commands) {
    this.commands = commands;
  }

}
