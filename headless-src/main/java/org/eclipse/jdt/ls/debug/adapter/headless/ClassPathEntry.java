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

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class ClassPathEntry {
    private String path;
    private String sourcePath;
    private File file;
    private File sourceFile;

    public ClassPathEntry(String path, String sourcePath) {
        this.path = path;
        this.file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException(path + " doesn't exist.");
        }
        this.sourcePath = sourcePath;
        if (!StringUtils.isBlank(sourcePath)) {
            this.sourceFile = new File(sourcePath);
            if (!this.sourceFile.exists()) {
                throw new IllegalArgumentException(sourcePath + " doesn't exist.");
            }
        }

    }

    public String getPath() {
        return this.path;
    }

    public boolean containsClass(String fullName) {
        String classFilePath = convertClassName(fullName, "class");
        return file.isDirectory() ? new File(file, classFilePath).exists() : JarUtils.containsFile(path, classFilePath);
    }

    public URI getSourceContentURI(String fullName) throws IOException {
        if (this.sourceFile == null) {
            return null;
        }
        String javaFilePath = convertClassName(fullName, "java");
        if (sourceFile.isDirectory()) {
            File javaFile = new File(this.sourceFile, javaFilePath);
            if (javaFile.exists()) {
                return javaFile.toURI();
            } else {
                return null;
            }
        } else {
            return JarUtils.getJarEntryURI(this.sourcePath, javaFilePath);
        }

    }

    public String getSourceContent(String fullName, String encoding) throws IOException {
        if (this.sourceFile == null) {
            return null;
        }
        String javaFilePath = convertClassName(fullName, "java");
        if (sourceFile.isDirectory()) {
            File javaFile = new File(this.sourceFile, javaFilePath);
            if (javaFile.exists()) {
                return FileUtils.readFileToString(javaFile, encoding);
            } else {
                return null;
            }
        } else {
            return JarUtils.readFileToString(this.sourcePath, javaFilePath, encoding);
        }

    }

    private static String convertClassName(String fullName, String suffix) {
        return FilenameUtils.normalize(fullName.replace('.', File.separatorChar) + "." + suffix, true);
    }
}
