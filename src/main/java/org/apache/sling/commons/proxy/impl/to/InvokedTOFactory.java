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
package org.apache.sling.commons.proxy.impl.to;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.commons.proxy.annotations.SlingChildren;
import org.apache.sling.commons.proxy.annotations.SlingProperty;
import org.apache.sling.commons.proxy.annotations.SlingReference;
import org.apache.sling.commons.proxy.impl.lang.MethodType;
import org.apache.sling.commons.proxy.impl.reflection.Annotations;

/**
 * Generates instances of InvokedTO's, based on the annotations available on the
 * method, this will return the correct transfer object type.
 */
public class InvokedTOFactory {

	/**
	 * Instantiates a new InvokedTO Object. This object will contain the
	 * relevant invocation properties for the method invocation
	 * 
	 * @param proxy
	 *            the proxy object
	 * @param method
	 *            the invoked method
	 * @param args
	 *            the method arguments
	 * @return the invocation TO
	 */
	public static InvokedTO newInstance(final Object proxy,
			final Method method, final Object[] args) {

		final MethodType mt = MethodType.getMethodType(method);
		if (mt.equals(MethodType.BackingResource)
				|| mt.equals(MethodType.Equals)
				|| mt.equals(MethodType.HashCode)
				|| mt.equals(MethodType.ToString)) {
			return new BaseInvokedTO(method, "", mt);
		} else if (Annotations
				.methodHasAnnotation(method, SlingReference.class)) {
			final SlingReference sr = method
					.getAnnotation(SlingReference.class);

			final String path = StringUtils.trim(sr.path());

			return new BaseInvokedTO(method, path, mt);
		} else if (Annotations.methodHasAnnotation(method, SlingChildren.class)) {
			final SlingChildren sc = method.getAnnotation(SlingChildren.class);
			final String path = StringUtils.trim(sc.path());
			final Class<?> returnType = sc.returnType();

			return new InvokedChildrenTO(method, args, path, returnType, mt);
		} else {
			final SlingProperty sp = method.getAnnotation(SlingProperty.class);
			if (sp == null) {
				throw new java.lang.IllegalStateException("Method "
						+ method.getName() + " on class "
						+ method.getClass().getCanonicalName()
						+ " does not have required annotations");
			}

			final String path = StringUtils.trim(sp.path());
			final String name = StringUtils.trim(sp.name());

			String property = (path.length() > 0 ? path + "/" : path) + name;
			if ((property == null) || (property.length() < 1)) {
				property = MethodType.getBeanName(mt, method);
				property = property.replace("_", ":");
			}
			if ((property == null) || (property.length() < 1)) {
				final String msg = "Could not determine Bean Property name either from @SlingProperty annotation or the JavaBean method name.";
				throw new IllegalStateException(msg);
			}

			return new InvokedPropertyTO(method, args, path, name, mt);
		}

	}

}
