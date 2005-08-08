/*
 * Created on 21-Mar-2004
 *
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.proxy.toys.nullobject;

import com.thoughtworks.proxy.ProxyTestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author <a href="mailto:aslak@thoughtworks.com">Aslak Helles&oslash;y</a>
 */
public class NullTest extends ProxyTestCase {
    protected NullInvoker nil;

    protected void setUp() throws Exception {
        super.setUp();
        nil = new NullInvoker(null, getFactory());
    }

    public interface SomePrimitives {
        boolean getBoolean();

        byte getByte();

        char getChar();

        int getInt();

        long getLong();

        float getFloat();

        double getDouble();

        Boolean getBoxedBoolean();

        Byte getBoxedByte();

        Character getBoxedChar();

        Integer getBoxedInt();

        Long getBoxedLong();

        Float getBoxedFloat();

        Double getBoxedDouble();
    }

    public void testShouldReturnDefaultValuesForPrimitives() throws Exception {
        // execute
        SomePrimitives nullObject = (SomePrimitives)Null.object(SomePrimitives.class, getFactory());

        // verify
        assertNotNull(nullObject);
        assertEquals(false, nullObject.getBoolean());
        assertEquals(0, nullObject.getByte());
        assertEquals(0, nullObject.getChar());
        assertEquals(0, nullObject.getInt());
        assertEquals(0, nullObject.getLong());
        assertEquals(0.0, nullObject.getFloat(), 0.0);
        assertEquals(0.0, nullObject.getDouble(), 0.0);

        assertEquals(Boolean.FALSE, nullObject.getBoxedBoolean());
        assertEquals(new Byte((byte)0), nullObject.getBoxedByte());
        assertEquals(new Character((char)0), nullObject.getBoxedChar());
        assertEquals(new Integer(0), nullObject.getBoxedInt());
        assertEquals(new Long(0), nullObject.getBoxedLong());
        assertEquals(new Float(0.0), nullObject.getBoxedFloat());
        assertEquals(new Double(0.0), nullObject.getBoxedDouble());
    }

    public interface SomeArrays {
        int[] getIntArray();

        Object[] getObjectArray();
    }

    public void testShouldReturnEmptyArrayForArrayMethods() throws Exception {
        // execute
        SomeArrays nullObject = (SomeArrays)Null.object(SomeArrays.class, getFactory());

        // verify
        assertEquals(0, nullObject.getIntArray().length);
        assertEquals(0, nullObject.getObjectArray().length);
    }

    public interface SomeCollections {
        Map getMap();

        List getList();

        Set getSet();

        SortedSet getSortedSet();

        SortedMap getSortedMap();
    }

    public void testShouldReturnStandardNullObjectsForCollections() throws Exception {
        // execute
        SomeCollections nullObject = (SomeCollections)Null.object(SomeCollections.class, getFactory());

        // verify
        assertSame(Collections.EMPTY_MAP, nullObject.getMap());
        assertSame(Collections.EMPTY_LIST, nullObject.getList());
        assertSame(Collections.EMPTY_SET, nullObject.getSet());
        assertSame(Null.NULL_SORTED_SET, nullObject.getSortedSet());
        assertSame(Null.NULL_SORTED_MAP, nullObject.getSortedMap());
    }

    public interface InterfaceWithVoidMethod {
        void doVoidMethod();
    }

    public void testShouldExecuteVoidMethods() throws Exception {
        // execute
        InterfaceWithVoidMethod nullObject = (InterfaceWithVoidMethod)Null.object(InterfaceWithVoidMethod.class, getFactory());

        // verify
        nullObject.doVoidMethod();
    }

    public interface InterfaceWithObjectMethod {
        Object getObject();
    }

    public void testShouldNotReturnNullForMethodsThatReturnAnObject() throws Exception {
        // execute
        InterfaceWithObjectMethod nullObject = (InterfaceWithObjectMethod)Null
                .object(InterfaceWithObjectMethod.class, getFactory());

        // verify
        assertNotNull(nullObject.getObject());
    }

    public void testShouldRecogniseNullCollectionsAsNullObjects() throws Exception {
        assertTrue("Map", Null.isNullObject(Null.object(Map.class, getFactory())));
        assertTrue("Set", Null.isNullObject(Null.object(Set.class, getFactory())));
        assertTrue("List", Null.isNullObject(Null.object(List.class, getFactory())));
        assertTrue("SortedSet", Null.isNullObject(Null.object(SortedSet.class, getFactory())));
        assertTrue("SortedMap", Null.isNullObject(Null.object(SortedMap.class, getFactory())));
        assertTrue("Object", Null.isNullObject(Null.object(Object.class, getFactory())));
    }

    public void testShouldThrowUnsupportedOperationWhenAddToNullSortedSet() throws Exception {
        SortedSet sortedSet = (SortedSet)Null.object(SortedSet.class, getFactory());
        Object object = new Object();

        try {
            sortedSet.add(object);
            fail("add");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            sortedSet.addAll(Collections.singleton(object));
            fail("addAll");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testShouldIgnoreRemovingFromNullSortedSet() throws Exception {
        SortedSet sortedSet = (SortedSet)Null.object(SortedSet.class, getFactory());
        Object object = new Object();

        sortedSet.clear();
        sortedSet.remove(object);
        sortedSet.removeAll(Collections.singleton(object));
        sortedSet.retainAll(Collections.singleton(object));
    }

    public void testShouldThrowUnsupportedOperationWhenAddingToNullSortedMap() throws Exception {
        SortedMap map = (SortedMap)Null.object(SortedMap.class, getFactory());

        try {
            map.put("should fail", "really");
            fail("put");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            map.putAll(Collections.singletonMap("should fail", "really"));
            fail("putAll");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testShouldIgnoreRemvingFromMutatingNullSortedMap() throws Exception {
        SortedMap map = (SortedMap)Null.object(SortedMap.class, getFactory());

        map.clear();
        map.remove("should fail");
    }

    public void testShouldReturnImmutableNullCollectionsForNullSortedMap() throws Exception {
        SortedMap map = (SortedMap)Null.object(SortedMap.class, getFactory());

        assertSame(Collections.EMPTY_SET, map.keySet());
        assertSame(Collections.EMPTY_LIST, map.values());
        assertSame(Collections.EMPTY_SET, map.entrySet());
    }

    public interface InterfaceWithInterfaceMethod {
        InterfaceWithInterfaceMethod getSubInterface();
    }

    public void testShouldReturnNullObjectIfMethodReturnsAnInterface() throws Exception {
        // execute
        InterfaceWithInterfaceMethod nullObject = (InterfaceWithInterfaceMethod)Null.object(
                InterfaceWithInterfaceMethod.class, getFactory());

        // verify
        assertNotNull(nullObject.getSubInterface());
        assertNotNull(nullObject.getSubInterface().getSubInterface());
    }

    public interface SimpleInterface {
    }

    public void testShouldCreateObjectWhichIsIdentifiableAsNullObject() throws Exception {
        // execute
        SimpleInterface nullObject = (SimpleInterface)Null.object(SimpleInterface.class, getFactory());

        // verify
        assertTrue(Null.isNullObject(nullObject, getFactory()));
        assertFalse(Null.isNullObject(new Object()));
    }

    public void testShouldReturnNonNullStringForToStringMethod() throws Exception {
        // execute
        SimpleInterface nullObject = (SimpleInterface)Null.object(SimpleInterface.class);

        // verify
        assertNotNull(nullObject.toString());
    }

    public void testShouldCompareEqualToAnotherNullObjectOfTheSameType() throws Exception {
        assertEquals(Null.object(SimpleInterface.class, getFactory()), Null.object(SimpleInterface.class, getFactory()));
    }

    private static void assertNotEquals(Object o1, Object o2) {
        assertFalse("Should be different", o1.equals(o2));
    }

    public void testShouldCompareUnequalToAnotherNullObjectOfDifferentType() throws Exception {
        assertNotEquals(Null.object(Collection.class, getFactory()), Null.object(List.class, getFactory()));
        assertNotEquals(Null.object(List.class, getFactory()), Null.object(Collection.class, getFactory()));
    }

    public void testShouldHaveSameHashcodeAsAnotherNullObjectOfTheSameType() throws Exception {
        assertEquals(Null.object(Collection.class, getFactory()).hashCode(), Null.object(Collection.class, getFactory()).hashCode());
    }

    private static void assertNotEquals(int i1, int i2) {
        assertFalse(i1 + ", " + i2 + " should be different", i1 == i2);
    }

    public void testShouldUsuallyHaveDifferentHashcodeFromAnotherNullObjectOfDifferentType() throws Exception {
        assertNotEquals(Null.object(Collection.class, getFactory()).hashCode(), Null.object(List.class, getFactory()).hashCode());
    }

    public void testShouldCreateEmptyStringForNullString() {
        assertEquals("", Null.object(String.class, getFactory()));
    }

    public void testShouldCreateSameObjectForNullObject() {
        assertSame(Null.object(Object.class, getFactory()), Null.object(Object.class, getFactory()));
        assertNotNull(Null.object(Object.class, getFactory()));
    }

    public interface ShouldSerialize extends Serializable {
    }

    public void testShouldBeSerializableIfInterfaceIsSerializable() throws Exception {
        // setup
        Object nullObject = Null.object(ShouldSerialize.class, getFactory());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);

        // execute
        out.writeObject(nullObject);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        Object result = in.readObject();

        // verify
        assertNotNull("not null", result);
        assertTrue("is Null Object", Null.isNullObject(result));
        assertTrue("is correct type", result instanceof ShouldSerialize);
    }

    public interface ShouldNotSerialize {
    }

    public void testShouldNotBeSerializableIfInterfaceIsNotSerializable() throws Exception {
        // setup
        ShouldNotSerialize nullObject = (ShouldNotSerialize)Null.object(ShouldNotSerialize.class, getFactory());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);

        // execute
        try {
            out.writeObject(nullObject);
            fail("Should throw exception");
        } catch (IOException expected) {
        }

        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
            in.readObject();
            fail("Should throw exception");
        } catch (IOException expected) {
        }
    }
}
