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

package org.eclipse.jdt.ls.debug.adapter.headless;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachineManager;

import java.util.Map;
import org.eclipse.jdt.ls.debug.adapter.IProviderContext;
import org.eclipse.jdt.ls.debug.adapter.ISourceLookUpProvider;
import org.eclipse.jdt.ls.debug.adapter.IVirtualMachineManagerProvider;

public class HeadlessProviderContext implements IProviderContext {
    private HeadlessSourceLookUpProvider sourceLookUpProvider = new HeadlessSourceLookUpProvider();
    @Override
    public ISourceLookUpProvider getSourceLookUpProvider() {
        return sourceLookUpProvider;
    }

    @Override
    public IVirtualMachineManagerProvider getVirtualMachineManagerProvider() {
        return new IVirtualMachineManagerProvider() {

            @Override
            public void initialize(Map<String, Object> props) {

            }

            @Override
            public VirtualMachineManager getVirtualMachineManager() {
                return Bootstrap.virtualMachineManager();
            }

        };
    }

}
