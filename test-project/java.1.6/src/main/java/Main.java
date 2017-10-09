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

class Main {
    public static void main(String []args) {
        System.out.println("test begin");
        new GenericTest().test();
        new BoxTest().test();
        new VariableParameterTest().test();
        new StaticImportTest().test();
        new ForLoopTest().test();
        new MetadataTest().test();
        new EnumTest().test();
        new InnerClassTest().test();
        new ThreadTester().test();
        System.out.println("test end");
    }
}