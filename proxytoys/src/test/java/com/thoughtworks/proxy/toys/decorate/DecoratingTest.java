/*
 * Created on 03-May-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.decorate;

import com.thoughtworks.proxy.NewProxyTestCase;
import com.thoughtworks.proxy.SameArrayMatcher;
import com.thoughtworks.proxy.kit.NoOperationResetter;
import com.thoughtworks.proxy.kit.Resetter;
import static com.thoughtworks.proxy.toys.decorate.Decorating.decoratable;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class DecoratingTest extends NewProxyTestCase {
    private static final String getSomething = "getSomething";

    private static final Method getSomethingMethod;

    static {
        try {
            getSomethingMethod = Foo.class.getMethod(getSomething, String.class);
        } catch (Exception e) {
            throw new NoSuchMethodError("Foo.getSomething(String)");
        }
    }

    private Foo foo;
    private InvocationDecorator decoratorMock;
    private Foo fooMock;

    public interface Foo {
        String getSomething(String arg);
    }


    @Before
    public void setUp() throws Exception {
        fooMock = mock(Foo.class);
        decoratorMock = mock(InvocationDecorator.class);
        assertNotNull(fooMock);
        foo = decoratable(Foo.class).with(fooMock, decoratorMock).build();
        assertNotNull(fooMock);
    }

    private Object[] toArray(Object value) {
        return new Object[]{value};
    }


    @Test
    public void shouldInterceptMethodInvocation() throws Exception {

        when(decoratorMock.beforeMethodStarts(same(foo), eq(getSomethingMethod), argThat(new SameArrayMatcher(toArray("fooMock"))))).thenReturn(toArray("decorated"));

        when(fooMock.getSomething(eq("decorated"))).thenReturn("hello");

        // execute
        foo.getSomething("fooMock");


        verify(decoratorMock).beforeMethodStarts(same(foo), eq(getSomethingMethod), argThat(new SameArrayMatcher(toArray("fooMock"))));
        verify(fooMock).getSomething(eq("decorated"));
    }

    @Test
    public void shouldInterceptMethodSuccess() throws Exception {
        // expect
        when(decoratorMock.beforeMethodStarts(any(Object.class), any(Method.class), any(Object[].class))).thenReturn(toArray("ignored"));
        when(fooMock.getSomething(any(String.class))).thenReturn("hello");
        when(decoratorMock.decorateResult(same(foo), eq(getSomethingMethod), argThat(new SameArrayMatcher(toArray("ignored"))), eq("hello"))).thenReturn("world");

        // execute
        String result = foo.getSomething("before");

        // verify
        assertEquals("world", result);
        verify(decoratorMock).beforeMethodStarts(any(Object.class), any(Method.class), any(Object[].class));
        verify(fooMock).getSomething(any(String.class));
        verify(decoratorMock).decorateResult(same(foo), eq(getSomethingMethod), argThat(new SameArrayMatcher(toArray("ignored"))), eq("hello"));

    }

    public static class MyException extends RuntimeException {
    }

    @Test
    public void shouldInterceptTargetException() throws Exception {
        when(decoratorMock.beforeMethodStarts(any(Object.class), any(Method.class), any(Object[].class))).thenReturn(toArray("ignored"));
        MyException exception = new MyException();
        MyException decoratedException = new MyException();

        // expect
        when(fooMock.getSomething(anyString())).thenThrow(exception);
        when(decoratorMock.decorateTargetException(same(foo), eq(getSomethingMethod), argThat(new SameArrayMatcher(toArray("ignored"))), same(exception))).thenReturn(decoratedException);
        // execute
        try {
            foo.getSomething("value");
            fail("Mock should have thrown exception");
        } catch (MyException oops) {
            assertSame(decoratedException, oops);
        }


        verify(decoratorMock).beforeMethodStarts(any(Object.class), any(Method.class), any(Object[].class));
        verify(fooMock).getSomething(anyString());
        verify(decoratorMock).decorateTargetException(same(foo), eq(getSomethingMethod), argThat(new SameArrayMatcher(toArray("ignored"))), same(exception));

    }

    public class MethodMissingImpl {
    }

    @Test(expected = MyException.class)
    public void shouldInterceptInvocationException() throws Exception {

        // setup
        final Throwable[] thrown = new Throwable[1]; // hack for inner class
        final MyException decoratedException = new MyException();

        foo = decoratable(Foo.class).with(new MethodMissingImpl(), new InvocationDecoratorSupport() {
            public Exception decorateInvocationException(Object proxy, Method method, Object[] args, Exception cause) {
                thrown[0] = cause;
                return decoratedException;
            }
        }).build();

        // execute

        foo.getSomething("value");
        fail("Mock should have thrown exception");

    }

    static class AssertingDecorator extends InvocationDecoratorSupport {
        private static final long serialVersionUID = 1L;

        public Object[] beforeMethodStarts(Object proxy, Method method, Object[] args) {
            assertTrue(args[0] instanceof NewProxyTestCase);
            return super.beforeMethodStarts(proxy, method, args);
        }

    }

    private void useSerializedProxy(Resetter resetter) {
        assertTrue(resetter.reset(this));
    }

    @Test
    public void serializeWithJDK() throws IOException, ClassNotFoundException {
        useSerializedProxy((Resetter) serializeWithJDK(decoratable(
                Resetter.class).with(new NoOperationResetter(), new AssertingDecorator()).build(getFactory())));
    }

    @Test
    public void serializeWithXStream() {
        useSerializedProxy((Resetter) serializeWithXStream(decoratable(
                Resetter.class).with(new NoOperationResetter(), new AssertingDecorator()).build(getFactory())));
    }

    @Test
    public void serializeWithXStreamInPureReflectionMode() {
        useSerializedProxy((Resetter) serializeWithXStreamAndPureReflection(decoratable(
                Resetter.class).with(new NoOperationResetter(), new AssertingDecorator()).build(getFactory())));
    }
}