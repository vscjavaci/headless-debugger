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

import static java.lang.Math.*;
public class StaticImportTest {
    public void test() {
        int x = 1;
        try {
            x++;
        } finally {
            System.out.println("template");
        }
        System.out.println(x);


        System.out.println(max(3, 6));
        System.out.println(abs(3 - 6));
    }
}
