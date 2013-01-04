/*
 * Copyright 2012 Six Dimensions.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.commons.proxy.core.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import org.apache.sling.api.resource.Resource;
import com.apache.sling.commons.proxy.core.lang.DefaultHashCodeImpl;
import com.apache.sling.commons.proxy.core.lang.GetMethodToStringImpl;
import com.apache.sling.commons.proxy.core.lang.IEquals;
import com.apache.sling.commons.proxy.core.lang.IHashCode;
import com.apache.sling.commons.proxy.core.lang.PrimeNumber;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

/**
 * @author MJKelleher - Dec 23, 2012 11:27:36 PM
 *
 * proxy-poc
 *
 *
 * org.apache.sling.commons.proxy.poc.jdp.ResourceInvocationHandler
 */
final class ResourceInvocationHandler implements InvocationHandler {

    private final Resource r;
    private final Node node;
    @SuppressWarnings("rawtypes")
    private final Set<Class> denyInvocations;
    private final int denyInvocationSize;

    /**
     * Create a new ResourceInvocationHandler allowing invocation of all Methods
     * that this InvocationHandler represents
     *
     * @param r Resource - the
     */
    ResourceInvocationHandler(Resource r) {
        this(r, null);
    }

    @SuppressWarnings("rawtypes")
    ResourceInvocationHandler(Resource r, Set<Class> denyInvocations) {
        this.r = r;
        this.node = r.adaptTo(Node.class);
        this.denyInvocationSize = (denyInvocations != null ? denyInvocations.size() : -1);
        this.denyInvocations = (this.denyInvocationSize > 0 ? Collections.unmodifiableSet(denyInvocations) : null);
    }

    /**
     * ************************************************************************
     *
     *
     */
    /*
     * (non-Javadoc) @see
     * java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        if (denyInvocationSize > 0) {
            if (denyInvocations.contains(method.getDeclaringClass())) {
                String msg = "Invocation of method " + method.getName()
                        + " has been denied by this ResourceInvocationHandler.";
                throw new IllegalStateException(msg);
            }
        }
        InvokedTO to = InvokedTO.newInstance(proxy, method, args);
        if (to.isGetter()) {
            return (handleGet(to));
        } else if (to.isType(MethodType.JavaBeanSet)) {
            return null;
        } else if (to.isType(MethodType.ToString)) {
            return new GetMethodToStringImpl().toString(proxy);
        } else if (to.isType(MethodType.HashCode)) {
            IHashCode hc = new DefaultHashCodeImpl();
            return hc.hashCode(proxy);
        } else if (to.isType(MethodType.Equals)) {
            if (args == null || args.length != 1) {
                String msg = "Method 'equals' requires exactly 1 argument.";
                throw new IllegalArgumentException(msg);
            }
            IEquals ieq = new JDPEqualsImpl();
            return ieq.equals(proxy, args[0]);
        }
        throw new NoSuchMethodException("Method " + method.getName() + " DNE");
    }

    @Override
    public int hashCode() {
        int hashCode = ResourceInvocationHandler.class.hashCode() * PrimeNumber.getInstance().get(2) + r.getPath().hashCode();
        return hashCode;
    }

    /**
     * ************************************************************************
     *
     *
     */
    final String getResourcePath() {
        return r.getPath();
    }

    /**
     * ************************************************************************
     *
     *
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object handleGet(InvokedTO to) throws Throwable {
        Property p;
        try {
            if (to.beanName.startsWith("/")) {
                p = (Property) node.getSession().getItem(to.beanName);
            } else {
                p = node.getProperty(to.beanName);
            }
        } catch (PathNotFoundException ex) {
            return null;
        }

        Class type = to.method.getReturnType();
        if (isMultiple(p)) {
            return PropertyHandler.castPropertyArray(p, type);
        }
        return PropertyHandler.castProperty(p, type);
    }

    private boolean isMultiple(Property p) throws RepositoryException {
        try {
            Value v = p.getValue();
            return false;
        } catch (ValueFormatException vfex) {
        }
        return true;
    }
}
