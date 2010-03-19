/*
 * Created 19.03.2010 by Joerg Schaible.
 * 
 * (c) 2010 ThoughtWorks
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.privilege;

import static com.thoughtworks.proxy.toys.privilege.Privileging.privilegable;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.thoughtworks.proxy.AbstractProxyTest;

public class PrivilegingTest extends AbstractProxyTest
{
	public interface Foo {
		void call();
		void call(int i, String s);
		int call(String s) throws NoSuchElementException;
	}

	@Test
	public void callIsDelegated() throws Exception
	{
		Foo fooMock = mock(Foo.class);
		
		Foo foo = privilegable(Foo.class).with(fooMock).executedBy(new DirectExecutor()).build(getFactory());
		foo.call();
		
		verify(fooMock).call();
	}

	@Test
	public void callWithArgumentsIsDelegated() throws Exception
	{
		Foo fooMock = mock(Foo.class);
		
		Foo foo = privilegable(Foo.class).with(fooMock).executedBy(new DirectExecutor()).build(getFactory());
		foo.call(42, "Arthur");
		
		verify(fooMock).call(42, "Arthur");
	}

	@Test
	public void callWillReturnValue() throws Exception
	{
		Foo fooMock = mock(Foo.class);
		when(fooMock.call("Arthur")).thenReturn(42);
		
		Foo foo = privilegable(Foo.class).with(fooMock).executedBy(new DirectExecutor()).build(getFactory());
		assertEquals(42, foo.call("Arthur"));
		
		verify(fooMock).call("Arthur");
	}

	@Test(expected=NoSuchElementException.class)
	public void callWillThrowCheckedException() throws Exception
	{
		Foo fooMock = mock(Foo.class);
		when(fooMock.call("Arthur")).thenThrow(new NoSuchElementException("JUnit"));
		
		Foo foo = privilegable(Foo.class).with(fooMock).executedBy(new DirectExecutor()).build(getFactory());
		foo.call("Arthur");
	}

	@Test(expected=ArithmeticException.class)
	public void callWillThrowRuntimeException() throws Exception
	{
		Foo fooMock = mock(Foo.class);
		when(fooMock.call("Arthur")).thenThrow(new ArithmeticException("JUnit"));
		
		Foo foo = privilegable(Foo.class).with(fooMock).executedBy(new DirectExecutor()).build(getFactory());
		foo.call("Arthur");
	}

	@Test
	public void callWillCallToString() throws Exception
	{
		Foo fooMock = mock(Foo.class);
		when(fooMock.toString()).thenReturn("Arthur");
		
		Foo foo = privilegable(Foo.class).with(fooMock).executedBy(new DirectExecutor()).build(getFactory());
		assertEquals("Arthur", foo.toString());
		
		// TODO: Mockito.verify cannot handle toString() 
		// verify(fooMock).toString();
	}
}
