/*
 * Created on 24-Mar-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxytoys;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


class NullInvocationHandler implements InvocationHandler {
    private static final Method equals;
    private static final Method hashCode;
    private static final Method toString;
    
    static {
        try {
            equals = Object.class.getMethod("equals", new Class[]{Object.class});
            hashCode = Object.class.getMethod("hashCode", new Class[0]);
            toString = Object.class.getMethod("toString", new Class[0]);
        } catch (Exception e) {
            throw new InternalError("hashCode(), equals(Object) or toString() missing!");
        }
    }

    private final Class type;
    
    public NullInvocationHandler(Class type) {
        this.type = type;
    }
    
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        
		// Object methods
		if (toString.equals(method)) {
            result = "Null Object for " + type.getName();
        }
        else if (equals.equals(method)) {
            Object other = args[0];
            result = (Null.isNullObject(other)
                    && type.equals(getType(other)))
                ? Boolean.TRUE : Boolean.FALSE;
        }
        else if (hashCode.equals(method)) {
			result = new Integer(type.hashCode());
        }
        else {
            result = Null.object(method.getReturnType());
        }
        return result;
	}

	private Class getType(Object object) {
        final Class result;
        if (Proxy.isProxyClass(object.getClass())) {
            NullInvocationHandler handler = (NullInvocationHandler) Proxy.getInvocationHandler(object);
            result = handler.type;
        }
        else {
            result = object.getClass();
        }
        return result;
	}
}