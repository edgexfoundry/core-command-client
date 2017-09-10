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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresNone.class})
public class ResponseTest {

  static final String TEST_CODE = "200";
  static final String TEST_DESCRIPTION = "ok";
  static final String TEST_EXPECTED_VALUE1 = "temperature";
  static final String TEST_EXPECTED_VALUE2 = "humidity";

  private Response response;

  @Before
  public void setup() {
    response = new Response(TEST_CODE, TEST_DESCRIPTION);
    List<String> expected = new ArrayList<>();
    expected.add(TEST_EXPECTED_VALUE1);
    expected.add(TEST_EXPECTED_VALUE2);
    response.setExpectedValues(expected);
  }

  @Test
  public void testRemoveExpectedValue() {
    assertTrue("Removal of value did not succeed",
        response.removeExpectedValue(TEST_EXPECTED_VALUE1));
  }

  @Test
  public void testRemoveExpectedValueWithNoExpectedValuesToStart() {
    response.setExpectedValues(null);
    assertFalse("Removal of unknown value should not succeed",
        response.removeExpectedValue("unknown"));
  }

  @Test
  public void testRemoveExpectedValueWithUnknownValue() {
    assertFalse("Removal of unknown value should not succeed",
        response.removeExpectedValue("unknown"));
  }

  @Test
  public void testAddExpectedValue() {
    response.addExpectedValue("newvalue");
  }

  @Test
  public void testAddExpectedValueWithNoValuesToStart() {
    response.setExpectedValues(null);
    response.addExpectedValue("newvalue");
  }
  
  @Test
  public void testToString() {
    response.toString();
    response.setCode(null);
    response.setDescription(null);
    response.setExpectedValues(null);
    response.toString();
  }
}
