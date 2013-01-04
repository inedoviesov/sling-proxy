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
package org.apache.sling.commons.proxy.api.annotations;

import java.lang.annotation.*;

/**
 * Annotation for overriding the behavior of the DefaultProxyHandler in
 * retrieving properties from the Sling Resource.
 * 
 * @author dklco
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface SlingProperty {

	/**
	 * The name of the property to retrieve from Sling.
	 * 
	 * @return the name of the property to retrieve
	 */
	String name() default "";

	/**
	 * The path to the property to retrieve, if it begins with '/' it will 
	 * be treated as an absolute path, otherwise, it will be treated as a 
	 * relative path.
	 * 
	 * @return the path to the property to retrieve
	 */
	String path() default "";
}
