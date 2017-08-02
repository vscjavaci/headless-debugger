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

package org.eclipse.jdt.ls.debug.adapter;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ls.core.internal.JDTUtils;
import org.eclipse.jdt.ls.core.internal.handlers.JsonRpcHelpers;
import org.eclipse.jdt.ls.debug.internal.JavaDebuggerServerPlugin;
import org.eclipse.jdt.ls.debug.internal.Logger;

public class AdapterUtils {

    /**
     * Finds the immediate parent class where the breakpoint locates
     * and returns the corresponding fully qualified name.
     * @param sourceFile
     *              the source file
     * @param lines
     *              breakpoint line number array
     * @return the fully qualified name array
     */
    public static String[] getFullyQualifiedName(String sourceFile, int[] lines) {
        String[] fqns = new String[lines.length];
        Path sourcePath = Paths.get(sourceFile);
        ICompilationUnit cu = JDTUtils.resolveCompilationUnit(sourcePath.toUri());
        
        for (int i = 0; i < lines.length; i++) {
            String fqn = null;
            try {
                int offset = JsonRpcHelpers.toOffset(cu.getBuffer(), lines[i], 0);
                IJavaElement javaElement = cu.getElementAt(offset);
                if (javaElement instanceof SourceField || javaElement instanceof SourceMethod) {
                    IType type = ((IMember) javaElement).getDeclaringType();
                    fqn = type.getFullyQualifiedName();
                } else if (javaElement instanceof SourceType) {
                    fqn = ((SourceType) javaElement).getFullyQualifiedName();
                }
            } catch (JavaModelException e) {
                Logger.logException("Add breakpoint exception", e);
            }
            fqns[i] = fqn;
        }
        return fqns;
    }

    /**
     * Search the absolute path of the java file under the specified source path directory.
     * @param sourcePaths
     *                  the project source directories
     * @param sourceName
     *                  the java file path
     * @return the absolute file path
     */
    public static String sourceLookup(String[] sourcePaths, String sourceName) {
        for (String path : sourcePaths) {
            Path fullpath = Paths.get(path, sourceName);
            if (Files.isRegularFile(fullpath)) {
                return fullpath.toString();
            }
        }
        return null;
    }

    /**
     * Get java project from name.
     * 
     * @param projectName
     *            project name
     * @return java project
     * @throws CoreException
     *             CoreException
     */
    public static IJavaProject getJavaProjectFromName(String projectName) throws CoreException {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(projectName);
        if (!project.exists()) {
            throw new CoreException(new Status(IStatus.ERROR, JavaDebuggerServerPlugin.PLUGIN_ID, "Not an existed project."));
        }
        if (!project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
            throw new CoreException(new Status(IStatus.ERROR, JavaDebuggerServerPlugin.PLUGIN_ID, "Not a project with java nature."));
        }
        IJavaProject javaProject = JavaCore.create(project);
        return javaProject;
    }

    /**
     * Get java project from type.
     * 
     * @param typeFullyQualifiedName
     *            fully qualified name of type
     * @return java project
     * @throws CoreException
     *             CoreException
     */
    public static List<IJavaProject> getJavaProjectFromType(String typeFullyQualifiedName) throws CoreException {
        SearchPattern pattern = SearchPattern.createPattern(
                typeFullyQualifiedName,
                IJavaSearchConstants.TYPE,
                IJavaSearchConstants.DECLARATIONS,
                SearchPattern.R_EXACT_MATCH);
        IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
        ArrayList<IJavaProject> projects = new ArrayList<>();
        SearchRequestor requestor = new SearchRequestor() {
            @Override
            public void acceptSearchMatch(SearchMatch match) {
                Object element = match.getElement();
                if (element instanceof IJavaElement) {
                    projects.add(((IJavaElement)element).getJavaProject());
                }
            }
        };
        SearchEngine searchEngine = new SearchEngine();
        searchEngine.search(
                pattern,
                new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() },
                scope,
                requestor,
                null /* progress monitor */);

        return projects;
    }

    /**
     * Accord to the project name and the main class, compute runtime classpath.
     * @param projectName
     *                   project name
     * @param mainClass
     *                   full qualified class name
     * @return class path
     * @throws CoreException
     *                      CoreException
     */
    public static String computeClassPath(String projectName, String mainClass) throws CoreException {
        IJavaProject project = null;
        // if type exists in multiple projects, debug configuration need provide project name.
        if (projectName != null) {
            project = getJavaProjectFromName(projectName);
        } else {
            List<IJavaProject> projects = AdapterUtils.getJavaProjectFromType(mainClass);
            if (projects.size() == 0 || projects.size() > 1) {
                throw new CoreException(new Status(IStatus.ERROR, JavaDebuggerServerPlugin.PLUGIN_ID, "project count is zero or more than one."));
            }
            project = projects.get(0);
        }
        return computeClassPath(project);
    }

    /**
     * Compute runtime classpath of a java project.
     * 
     * @param javaProject
     *            java project
     * @return class path
     * @throws CoreException
     *             CoreException
     */
    public static String computeClassPath(IJavaProject javaProject) throws CoreException {
        if (javaProject == null) {
            throw new IllegalArgumentException("javaProject is null");
        }
        String[] classPathArray = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);
        String classPath = String.join(System.getProperty("path.separator"), classPathArray);
        return classPath;
    }

    /**
     * Searches the source file from the fully qualified name and returns the uri string.
     * @param project
     *               project instance
     * @param fullyQualifiedName
     *               fully qualified name
     * @return the uri string of source file
     */
    public static String getURI(IProject project, String fullyQualifiedName) {
        IJavaSearchScope searchScope = project != null ? JDTUtils.createSearchScope(JavaCore.create(project)) : SearchEngine.createWorkspaceScope();
        
        int lastDot = fullyQualifiedName.lastIndexOf(".");
        String packageName = lastDot > 0 ? fullyQualifiedName.substring(0, lastDot) : "";
        String className = lastDot > 0 ? fullyQualifiedName.substring(lastDot + 1) : fullyQualifiedName;
        ClassUriExtractor extractor = new ClassUriExtractor();
        
        try {
            new SearchEngine().searchAllTypeNames(packageName.toCharArray(),SearchPattern.R_EXACT_MATCH,
                    className.toCharArray(), SearchPattern.R_EXACT_MATCH,
                    IJavaSearchConstants.TYPE,
                    searchScope,
                    extractor,
                    IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
                    new NullProgressMonitor());
        } catch (JavaModelException e) {
            return null;
        }
        return extractor.uri;
    }

    /**
     * Searches the source file from the full qualified name and returns the uri string.
     * @param fqcn
     *            full qualified class name
     * @return the uri string of source file
     */
    public static String getURI(String fqcn) {
        return getURI(null, fqcn);
    }

    /**
     * Gets the source contents of the uri path.
     * @param uri
     *           the uri string
     * @return the source contents
     */
    public static String getContents(String uri) {
        IClassFile cf = JDTUtils.resolveClassFile(uri);
        return getContents(cf);
    }

    /**
     * Gets the source contents of the uri.
     * @param uri
     *            uri object
     * @return the source contents
     */
    public static String getContents(URI uri) {
        IClassFile cf = JDTUtils.resolveClassFile(uri);
        return getContents(cf);
    }

    /**
     * Gets the source contents of the class file.
     * @param cf
     *          class file
     * @return the source contents
     */
    public static String getContents(IClassFile cf) {
        String source = null;
        if (cf != null) {
            try {
                IBuffer buffer = cf.getBuffer();
                if (buffer != null) {
                    source = buffer.getContents();
                }
                if (source == null) {
                    source = JDTUtils.disassemble(cf);
                }
            } catch (JavaModelException e) {
                Logger.logException("Failed to parse the source contents of the class file", e);
            }
            if (source == null) {
                source = "";
            }
        }
        return source;
    }

    private static class ClassUriExtractor extends TypeNameMatchRequestor {

        String uri;

        @Override
        public void acceptTypeNameMatch(TypeNameMatch match) {
            if (match.getType().isBinary()) {
                uri = JDTUtils.getFileURI(match.getType().getClassFile());
            }  else {
                uri = JDTUtils.getFileURI(match.getType().getResource());
            }
        }
    }
}
