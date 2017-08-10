/*******************************************************************************
 * Copyright (c) 2017 Microsoft Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Microsoft Corporation - initial API and implementation
 *******************************************************************************/
import java.util.HashMap;
import java.util.Map;


class GenericClass<T extends Number> {
    public T object;
}
class GenericMethod {
    public static <K, V> void test(Map<K, V> p1, Map<K, V> p2) {
        p1.keySet().addAll(p2.keySet());
    }
}
public class GenericTest {
    public void test() {
        GenericClass<Integer> stringGenericClass = new GenericClass<Integer>();
        stringGenericClass.object = new Integer(100);

        Map<String, String> props1 = new HashMap<String, String>();
        Map<String, String> props2 = new HashMap<String, String>();
        GenericMethod.test(props1, props2);
    }
}
