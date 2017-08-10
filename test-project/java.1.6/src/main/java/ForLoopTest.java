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

public class ForLoopTest {
    public void test() {
        Integer[] array = new Integer[]{1, 2, 3, 4, 5, 6};
        for (Integer ele : array) {
            System.out.println(ele);
        }


        String fruits[] = {"apple", "orange", "strawberry"};

        for (String fruit : fruits) {
            System.out.println(fruit);
        }
    }
}
