package com.thoughtworks.proxy.toys.failover;

import com.thoughtworks.proxy.ProxyTestCase;
import static com.thoughtworks.proxy.toys.failover.Failover.failoverable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;


/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class FailoverTest extends ProxyTestCase {

    public static interface FailsOnNthCall {
        void doIt();

        int dunIt();
    }

    public static class FailsOnNthCallImpl implements FailsOnNthCall, Serializable {
        private static final long serialVersionUID = 1L;
        private int failsOn;
        private int dunItCount = 0;

        public FailsOnNthCallImpl(int failsOn) {
            this.failsOn = failsOn;
        }

        public void doIt() {
            if (dunItCount == failsOn) {
                throw new RuntimeException();
            }
            dunItCount++;
        }

        public int dunIt() {
            return dunItCount;
        }

    }

    @Test
    public void shouldFailoverToNextOnSpecialException() {
        FailsOnNthCall first = new FailsOnNthCallImpl(1);
        FailsOnNthCall second = new FailsOnNthCallImpl(1);
        FailsOnNthCall failover = failoverable(FailsOnNthCall.class)
                                     .with(first, second)
                                     .excepting(RuntimeException.class)
                                     .build(getFactory());
        assertEquals(0, first.dunIt());
        assertEquals(0, second.dunIt());
        failover.doIt();
        assertEquals(1, first.dunIt());
        assertEquals(0, second.dunIt());
        failover.doIt();
        assertEquals(1, first.dunIt());
        assertEquals(1, second.dunIt());
    }

    private void useSerializedProxy(FailsOnNthCall failoverr) {
        assertEquals(1, failoverr.dunIt());
        failoverr.doIt();
        assertEquals(1, failoverr.dunIt());
        try {
            failoverr.doIt();
            fail("Thrown " + RuntimeException.class.getName() + " expected");
        } catch (final RuntimeException e) {
        }
    }

    @Test
    public void serializeWithJDK() throws IOException, ClassNotFoundException {
        final FailsOnNthCall failover = failoverable(FailsOnNthCall.class)
                                          .with(new FailsOnNthCallImpl(1), new FailsOnNthCallImpl(1))
                                          .excepting(RuntimeException.class)
                                          .build(getFactory());
        failover.doIt();
        useSerializedProxy((FailsOnNthCall) serializeWithJDK(failover));
    }

    @Test
    public void serializeWithXStream() {
        final FailsOnNthCall failover = failoverable(FailsOnNthCall.class)
                                          .with(new FailsOnNthCallImpl(1), new FailsOnNthCallImpl(1))
                                          .excepting(RuntimeException.class)
                                          .build(getFactory());
        failover.doIt();
        useSerializedProxy((FailsOnNthCall) serializeWithXStream(failover));
    }

    @Test
    public void serializeWithXStreamInPureReflectionMode() {
        final FailsOnNthCall failover = failoverable(FailsOnNthCall.class)
                                          .with(new FailsOnNthCallImpl(1), new FailsOnNthCallImpl(1))
                                          .excepting(RuntimeException.class)
                                          .build(getFactory());
        failover.doIt();
        useSerializedProxy((FailsOnNthCall) serializeWithXStreamAndPureReflection(failover));
    }

}