package com.thoughtworks.proxytoys;

/**
 * Abstraction layer for proxy generation. Depending on this interface
 * (rather than {@link java.lang.reflect.Proxy} directly) will allow
 * you to use Java's standard proxy mechanism interchangeably with e.g. CGLIB.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public interface ProxyFactory {
    Object createProxy(Class type, Invoker invoker);
    boolean canProxy(Class type);
    boolean isProxyClass(Class type);
    Invoker getInvoker(Object proxy);
}