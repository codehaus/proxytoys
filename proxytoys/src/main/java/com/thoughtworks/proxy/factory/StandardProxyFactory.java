/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.factory;

import com.thoughtworks.proxy.Invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;


/**
 * A {@link com.thoughtworks.proxy.ProxyFactory} based on a JDK greater or equal 1.3.
 *
 * @author Aslak Helles&oslash;y
 * @see com.thoughtworks.proxy.factory
 * @since 0.1
 */
public class StandardProxyFactory extends AbstractProxyFactory {
    private static final long serialVersionUID = 4430360631813383235L;

    /**
     * The native InvocationHandler implementation.
     *
     * @since 0.1
     */
    static class StandardInvocationHandlerAdapter extends CoincidentalInvocationHandlerAdapter implements
            InvocationHandler {
        private static final long serialVersionUID = 141954540221604284L;

        /**
         * Construct a StandardInvocationHandlerAdapter.
         *
         * @param invoker the wrapping invoker instance
         * @since 0.1
         */
        public StandardInvocationHandlerAdapter(Invoker invoker) {
            super(invoker);
        }
    }

    public Object createProxy(final Class[] types, final Invoker invoker) {
        final Class[] interfaces = new Class[types.length + 1];
        System.arraycopy(types, 0, interfaces, 0, types.length);
        interfaces[types.length] = InvokerReference.class;
        return Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new StandardInvocationHandlerAdapter(
                invoker));
    }

    public boolean canProxy(final Class type) {
        return type.isInterface();
    }

    public boolean isProxyClass(final Class type) {
        return Proxy.isProxyClass(type);
    }

}