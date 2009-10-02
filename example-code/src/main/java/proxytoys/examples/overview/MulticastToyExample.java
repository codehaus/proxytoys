/*
 * Copyright (C) 2005 J�rg Schaible
 * Created on 26-Jul-2005 by J�rg Schaible
 * See license.txt for license details
 */
package proxytoys.examples.overview;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.multicast.Multicast;
import static com.thoughtworks.proxy.toys.multicast.Multicasting.multicastable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;


/**
 * @author J&ouml;rg Schaible
 */
public class MulticastToyExample {

    public static void packageOverviewExample1() {
        ArrayList arrayList = new ArrayList();
        LinkedList linkedList = new LinkedList();
        List listCombined = (List)multicastable(arrayList, linkedList).build();
        if (listCombined.add("Hello")) {
            System.out.println("List 1: " + arrayList.toString());
            System.out.println("List 2: " + linkedList.toString());
        }
    }

    public static void packageOverviewExample2() {
        try {
            List list1 = new ArrayList();
            list1.add(new Integer(5));
            list1.add(new Integer(100));
            List list2 = new LinkedList();
            list2.add(new Integer(3));
            List listCombined = (List)multicastable(list1, list2).build();
            Multicast values = (Multicast)listCombined.get(0);
            System.out.println("Sum of the first integers: "
                    + values.multicastTargets(Integer.class, "intValue", null).toString());
        } catch (NoSuchMethodException e) {
            // Integer.class has a intValue method
        }
    }

    public static void packageOverviewExample3() {
        File workingDir = new File(".");
        List files = Arrays.asList(workingDir.list());
        Object multicast = multicastable(
                 workingDir, files).withTypes(File.class, List.class).build(new CglibProxyFactory());
        System.out.println("Current working directory: " + ((File)multicast).getAbsolutePath());
        System.out.println("Files in working directory: " + ((List)multicast).size());
    }

    public static void packageOverviewExample4() {
        try {
            Method method = String.class.getMethod("length");
            Multicast multicast = (Multicast)multicastable("ProxyToys", "is", "great").build();
            System.out.println("Total number of characters: " + multicast.multicastTargets(method, null));
            String[] strings = (String[])multicast.getTargetsInArray(String.class);
            for (int i = 0; i < strings.length; i++) {
                System.out.println("String[" + i + "]: " + strings[i]);
            }
        } catch (NoSuchMethodException e) {
            // String.class has a length method
        }
    }

    public static void packageOverviewExample5() {
        List list = new ArrayList();
        Set set = new HashSet();
        list.add("ProxyToys");
        set.add(null);
        Collection collection = (Collection)multicastable(list, set).build();
        Iterator iter = collection.iterator();
        String value = (String)iter.next();
        System.out.println("Element gained from the iterator: " + value);
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println();
        System.out.println("Running Multicasting Toy Examples");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
        System.out.println();
        System.out.println("Example 2 of Package Overview:");
        packageOverviewExample2();
        System.out.println();
        System.out.println("Example 3 of Package Overview:");
        packageOverviewExample3();
        System.out.println();
        System.out.println("Example 4 of Package Overview:");
        packageOverviewExample4();
        System.out.println();
        System.out.println("Example 5 of Package Overview:");
        packageOverviewExample5();
    }
}