/*
 * Created on 04-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.delegate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

import com.thoughtworks.proxy.Invoker;

// TODO: We can delete this class/package and merge it with HotSwappingInvoker.
// We just need an extra flag indicating whether we're looking up a new method
// "that looks the same", or if we are calling the "same" method.
// AH
public class DelegatingInvoker implements Invoker {
    protected final Object delegate;
    
    public DelegatingInvoker(Object delegate) {
        this.delegate = delegate;
	}

	/**
     * @throws RemoteException if anything goes wrong that would
     *         cause deployment to fail (eg. missing/inaccessible method)
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invokeOnDelegate(method.getName(), method.getParameterTypes(), args);
    }
    
    protected Object invokeOnDelegate(String methodName, Class[] parameterTypes, Object[] args) throws Throwable {
        try {
            if (delegate == null) {
                return null;
            }
            return delegate.getClass().getMethod(methodName, parameterTypes).invoke(delegate, args);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        catch (Throwable e) {
            throw new DelegationException("Problem calling " + methodName, e);
        }
    }
    
    protected final Object invokeOnDelegate(Method method, Object[] args) throws Throwable {
        return invokeOnDelegate(method.getName(), method.getParameterTypes(), args);
    }
}