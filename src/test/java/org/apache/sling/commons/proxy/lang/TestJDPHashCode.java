/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.commons.proxy.lang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.proxy.BaseSlingProxyTest;
import org.apache.sling.commons.proxy.SlingProxyService;
import org.apache.sling.commons.proxy.impl.DefaultSlingProxyServiceImpl;
import org.apache.sling.commons.proxy.samples.DuplicateSlingPropertyProxy;
import org.apache.sling.commons.proxy.samples.SlingPropertyProxy;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for the Sling Proxy hashCode implementation.
 */
public class TestJDPHashCode extends BaseSlingProxyTest {
	private static final Logger log = LoggerFactory
			.getLogger(TestJDPHashCode.class);

	/**
	 * All of the tests for the hashCode implementation.
	 */
	@Test
	public void runTests() {

		log.info("runTests");

		Resource pageResource = resolver.getResource("/content/test");
		SlingPropertyProxy pageProxy = pageResource
				.adaptTo(SlingPropertyProxy.class);

		log.info("Testing Equals functionality");
		int hashCode = pageProxy.hashCode();
		assertNotNull(hashCode);
		log.info("HashCode: " + hashCode);

		log.info("Ensuring the same proxy interface from the same resource get the same hash code");
		SlingPropertyProxy pageProxy2 = pageResource
				.adaptTo(SlingPropertyProxy.class);
		log.info("HashCode1 {}, HashCode 2 {}", hashCode, pageProxy2.hashCode());
		assertEquals(pageProxy2.hashCode(), hashCode);

		log.info("Ensuring different proxy interfaces from the same resource get different hash codes");
		DuplicateSlingPropertyProxy pageProxy3 = pageResource
				.adaptTo(DuplicateSlingPropertyProxy.class);
		log.info("HashCode1 {}, HashCode 2 {}", hashCode, pageProxy3.hashCode());
		assertFalse(pageProxy3.hashCode() == hashCode);

		log.info("Ensuring retrieving the same proxy from different proxy services results in the same hashCode");
		SlingProxyService slingProxyService2 = new DefaultSlingProxyServiceImpl();
		SlingPropertyProxy pageProxy4 = slingProxyService2.getProxy(
				pageResource, SlingPropertyProxy.class);
		log.info("HashCode1 {}, HashCode 2 {}", hashCode, pageProxy4.hashCode());
		assertEquals(hashCode, pageProxy4.hashCode());

		log.info("Tests Successful");
	}
}
