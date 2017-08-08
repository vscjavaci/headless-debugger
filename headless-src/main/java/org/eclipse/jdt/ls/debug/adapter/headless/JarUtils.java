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
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;


@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class JarUtils {
    public static ThreadLocal zipFiles = new ThreadLocal();

    public static boolean containsFile(String jarPath, String fullName) {
        ZipFile zip = getZipFile(jarPath);
        ZipEntry entry = zip.getEntry(fullName);
        return entry != null && !entry.isDirectory();
    }

    public static String readFileToString(String jarPath, String fullName, String charsetName) throws IOException {
        ZipFile zip = getZipFile(jarPath);
        ZipEntry entry = zip.getEntry(fullName);
        if (entry != null && !entry.isDirectory()) {
            try (InputStream in = zip.getInputStream(entry)) {
                return IOUtils.toString(zip.getInputStream(entry), charsetName);
            }
        } else {
            return null;
        }
    }


    public static URI getJarEntryURI(String jarPath, String fullName) throws IOException {
        ZipFile zip = getZipFile(jarPath);
        ZipEntry entry = zip.getEntry(fullName);
        if (entry != null && !entry.isDirectory()) {
            return URI.create("jar://" + jarPath+ "|" + fullName);
        } else {
            return null;
        }
    }
    public static boolean containsDirectory(String jarPath, String fullName) {
        ZipFile zip = getZipFile(jarPath);
        ZipEntry entry = zip.getEntry(fullName);
        return entry != null && entry.isDirectory();
    }

    private static ZipFile getZipFile(String jarPath) {
        ZipCache zipCache;
        ZipFile zipFile;
        if ((zipCache = (ZipCache) zipFiles.get()) != null
                && (zipFile = zipCache.getCache(jarPath)) != null) {
            return zipFile;
        }
        File localFile = new File(jarPath);
        if (ZIP_ACCESS_VERBOSE) {
            System.out.println("(" + Thread.currentThread() //$NON-NLS-1$
                    + ") [JarUtils.getZipFile(IPath)] Creating ZipFile on " + localFile); //$NON-NLS-1$
        }
        try {
            zipFile = new ZipFile(localFile);
            if (zipCache != null) {
                zipCache.setCache(jarPath, zipFile);
            }

            return zipFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cacheZipFiles(Object owner) {
        ZipCache zipCache = (ZipCache) zipFiles.get();
        if (zipCache != null) {
            return;
        }
        // the owner will be responsible for flushing the cache
        zipFiles.set(new ZipCache(owner));
    }



    public static boolean ZIP_ACCESS_VERBOSE = false;

    /**
     * Define a zip cache object.
     */
    static class ZipCache {
        private Map map;
        Object owner;

        ZipCache(Object owner) {
            this.map = new HashMap();
            this.owner = owner;
        }

        public void flush() {
            Thread currentThread = Thread.currentThread();
            Iterator iterator = this.map.values().iterator();
            while (iterator.hasNext()) {
                try {
                    ZipFile zipFile = (ZipFile)iterator.next();
                    if (JarUtils.ZIP_ACCESS_VERBOSE) {
                        System.out.println("(" + currentThread + ") [JarUtils.flushZipFiles()] Closing ZipFile on " +zipFile.getName()); //$NON-NLS-1$//$NON-NLS-2$
                    }
                    zipFile.close();
                } catch (IOException e) {
                    // problem occurred closing zip file: cannot do much more
                }
            }
        }

        public ZipFile getCache(String path) {
            return (ZipFile) this.map.get(path);
        }

        public void setCache(String path, ZipFile zipFile) {
            this.map.put(path, zipFile);
        }
    }
}
