/*
 * Created on 14-July-2005
 * 
 * (c) 2005 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.dispatch;

/**
 * An exception if a type cannot be dispatched.
 * 
 * @author J&ouml;rg Schaible
 * @since 0.2
 */
public class DispatchingException extends RuntimeException {

    private final Class type;

    /**
     * Construct a DispatchingException with the offending type.
     * 
     * @param message the meaningful message
     * @param type the type, that cannot be dispatched
     * @since 0.2
     */
    public DispatchingException(final String message, final Class type) {
        super(message);
        this.type = type;
    }

    /**
     * Returns the offending type.
     * 
     * @return the type
     * @since 0.2
     */
    public Object getType() {
        return type;
    }
}
