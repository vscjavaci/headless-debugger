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
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class ClassPathContainer {
    private List<ClassPathEntry> classPathEntries;

    public ClassPathContainer(List<ClassPathEntry> classPathEntries) {
        this.classPathEntries = classPathEntries;
    }

    public String getSourceContent(String fullName, String encoding) {
        Optional<String> opts = this.classPathEntries.stream().filter(cp -> cp.containsClass(fullName)).map(cp -> {
            try {
                return cp.getSourceContent(fullName, encoding);
            } catch (IOException e) {
                return null;
            }
        }).filter(StringUtils::isNotEmpty).findFirst();
        return opts.isPresent() ? opts.get() : null;
    }

    public URI getSourceContentUri(String fullName) {
        Optional<URI> opts = this.classPathEntries.stream().filter(cp -> cp.containsClass(fullName)).map(cp -> {
            try {
                return cp.getSourceContentURI(fullName);
            } catch (IOException e) {
                return null;
            }
        }).filter(uri -> uri != null).findFirst();
        return opts.isPresent() ? opts.get() : null;
    }
}
