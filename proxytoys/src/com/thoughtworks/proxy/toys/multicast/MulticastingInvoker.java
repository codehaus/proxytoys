package com.thoughtworks.proxy.toys.multicast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;

/**
 * Generic object multicaster. Proxies generated by this class will forward all method
 * invocation to an array of underlying objects. The behaviour is recursive, so return
 * values will also be multicasting objects.
 *
 * @author Aslak Helles&oslash;y
 * @author Chris Stevenson
 * @version $Revision: 1.3 $
 */
public class MulticastingInvoker implements Invoker {
    private final Class[] types;
    private final ProxyFactory proxyFactory;
    private final Object[] targets;
    private final List invocationResults = new ArrayList();

    public MulticastingInvoker(Class[] type, ProxyFactory proxyFactory, Object[] targets) {
        this.types = type;
        this.proxyFactory = proxyFactory;
        this.targets = targets;
    }

    Object proxy() {
        return proxyFactory.createProxy(types, this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        invocationResults.clear();
        for (int i = 0; i < targets.length; i++) {
            if(method.getDeclaringClass().isInstance(targets[i])) {
                Object result = method.invoke(targets[i], args);
                if(result != null) {
                    invocationResults.add(result);
                }
            }
        }
        if(invocationResults.size() == 0) {
            return null;
        }
        if(invocationResults.size() == 1) {
            return invocationResults.get(0);
        }
        if (method.getReturnType().equals(int.class)) {
            return addIntegers(invocationResults.toArray());
        }
        if (method.getReturnType().equals(boolean.class)) {
            return addBooleans(invocationResults.toArray());
        }

        return Multicasting.object(proxyFactory, invocationResults.toArray());
    }

    private static Integer addIntegers(Object[] args) {
        int result = 0;
        for (int i = 0; i < args.length; i++) {
            result += ((Integer)args[i]).intValue();
        }
        return new Integer(result);
    }

    private static Boolean addBooleans(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            if(!((Boolean)args[i]).booleanValue()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

}