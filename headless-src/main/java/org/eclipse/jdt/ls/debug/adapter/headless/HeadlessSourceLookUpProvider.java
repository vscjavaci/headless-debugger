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

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.ls.debug.adapter.ISourceLookUpProvider;

public class HeadlessSourceLookUpProvider implements ISourceLookUpProvider {

    private ClassPathContainer container;

    @Override
    public void initialize(Map<String, Object> props) {
        this.container = (ClassPathContainer) props.get("container");
    }

    @Override
    public String[] getFullyQualifiedName(String sourceFilePath, int[] lines, int[] columns) {
        return HeadlessAdapterUtils.getFullyQualifiedName(sourceFilePath, lines);
    }

    @Override
    public String getSourceFileURI(String fullyQualifiedName) {
        return HeadlessAdapterUtils.getURI(fullyQualifiedName, container);
    }

    @Override
    public String getSourceContents(String uri) {
        try {
            return FileUtils.readFileToString(FileUtils.toFile(new URL(uri)), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
