/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.thoughtworks.proxy.Invoker;

/**
 * A {@link com.thoughtworks.proxy.ProxyFactory} based on a JDK greater or equal 1.3.
 * @author Aslak Helles&oslash;y
 * @since 0.1
 */
public class StandardProxyFactory extends AbstractProxyFactory {
    /** The native InvocationHandler implementation. */
    class StandardInvocationHandlerAdapter extends CoincidentalInvocationHandlerAdapter implements InvocationHandler {
        public StandardInvocationHandlerAdapter(Invoker invoker) {
            super(invoker);
        }
    }

    public Object createProxy(Class[] types, final Invoker invoker) {
        Class[] interfaces = new Class[types.length + 1];
        System.arraycopy(types, 0, interfaces, 0, types.length);
        interfaces[types.length] = InvokerReference.class;
        return Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new StandardInvocationHandlerAdapter(invoker));
    }

    public boolean canProxy(Class type) {
        return type.isInterface();
    }

    public boolean isProxyClass(Class type) {
        return Proxy.isProxyClass(type);
    }

}