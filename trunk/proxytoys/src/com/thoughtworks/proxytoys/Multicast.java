package com.thoughtworks.proxytoys;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Generic object multicaster. Proxies generated by this class will forward all method
 * invocation to an array of underlying objects. The behaviour is recursive, so return
 * values will also be multicasting objects.
 *
 * @author Aslak Helles&oslash;y
 * @author Chris Stevenson
 * @version $Revision: 1.3 $
 */
public class Multicast implements Invoker {
    private final Class[] types;
    private final ProxyFactory proxyFactory;
    private final Object[] targets;

    public Multicast(Class[] type, ProxyFactory proxyFactory, Object[] targets) {
        this.types = type;
        this.proxyFactory = proxyFactory;
        this.targets = targets;
        for (int i = 0; i < type.length; i++) {
            for (int j = 0; j < targets.length; j++) {
                if (!type[i].isInstance(targets[j])) {
                    String name = targets[j] == null ? "null" : targets[j].getClass().getName();
                    throw new IllegalArgumentException(name + " is not an instance of " + type[i].getName());
                }
            }
        }
    }

    public static Object object(Class[] types, ProxyFactory proxyFactory, Object[] targets) {
        return new Multicast(types, proxyFactory, targets).proxy();
    }

    public static Object object(Class type, ProxyFactory proxyFactory, Object[] targets) {
        return object(new Class[]{type}, proxyFactory, targets);
    }

    public static Object object(ProxyFactory proxyFactory, Object[] targets) {
        Class superclass = ClassHierarchyIntrospector.getMostCommonSuperclass(targets);
        Class[] interfaces = ClassHierarchyIntrospector.getAllInterfaces(targets);
        Class[] proxyTypes = addIfClassProxyingSupportedAndNotObject(superclass, interfaces, proxyFactory);
        return object(proxyTypes, proxyFactory, targets);
    }

    public static Object object(Object[] targets) {
        return object(new StandardProxyFactory(), targets);
    }

    private Object proxy() {
        return proxyFactory.createProxy(types, this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object[] results = new Object[targets.length];

        for (int i = 0; i < targets.length; i++) {
            results[i] = method.invoke(targets[i], args);
        }

        if (method.getReturnType().equals(void.class)) {
            return null;
        }
        if (method.getReturnType().equals(int.class)) {
            return addIntegers(results);
        }
        if (method.getReturnType().equals(boolean.class)) {
            return addBooleans(results);
        }

        return object(proxyFactory, results);
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

    private static Class[] addIfClassProxyingSupportedAndNotObject(Class clazz, Class[] interfaces, ProxyFactory proxyFactory) {
        Class[] result;
        if (proxyFactory.canProxy(ArrayList.class) && !clazz.equals(Object.class)) {
            result = new Class[interfaces.length + 1];
            result[0] = clazz;
            System.arraycopy(interfaces, 0, result, 1, interfaces.length);
        } else {
            result = interfaces;
        }
        return result;
    }
}