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

import com.github.javaparser.JavaParser;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.utils.PositionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class HeadlessAdapterUtils {
    public static String[] getFullyQualifiedName(String sourceFile, int[] debuggerLines) {
        try {
            Map<Integer, String> map = getClassNameForLines(FileUtils.openInputStream(new File(sourceFile)),
                    debuggerLines);
            return ArrayUtils.toStringArray(Arrays.stream(debuggerLines).mapToObj(l -> {
                if (map.containsKey(l))
                    return map.get(l);
                else
                    return null;
            }).toArray(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getURI(String name, ClassPathContainer container) {

        URI uri = container.getSourceContentUri(name);
        if (uri == null)
            return null;
        return uri.toString();
    }
    
    private static boolean rangeContainsLineNumber(Range range, int line) {
        return range.begin.line <= line && range.end.line >= line;
    }

    private static boolean containsLineNumber(Node node, int line) {
        if (node.getRange().isPresent()) {
            return rangeContainsLineNumber(node.getRange().get(), line);
        }
        return false;
    }

    private static boolean isInterface(Node decl) {
        if (decl instanceof ClassOrInterfaceDeclaration) {
            return ((ClassOrInterfaceDeclaration) decl).isInterface();
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    private static String getEnclosedTypeName(Node n) {
        List<TypeDeclaration> list = new ArrayList<>();
        while (n.getParentNode().isPresent()) {
            if (n instanceof TypeDeclaration) {
                list.add((TypeDeclaration) n);
            }
            if (n.getParentNode().isPresent()) {
                n = n.getParentNode().get();
            } else {
                break;
            }
        }
        return list.size() > 0 ? list.get(list.size() - 1).getNameAsString() : null;
    }

    private static void resolveClassnameOnLine(CompilationUnit cu, int line, Map<Integer, String> lineMap,
            String packageName) {
        List<Node> list = cu.getChildNodesByType(Node.class).stream()
                .filter(c -> c instanceof TypeDeclaration && !(c instanceof EnumDeclaration) && !isInterface(c)
                        && !(c instanceof AnnotationExpr) && containsLineNumber(c, line))
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            return;
        }
        PositionUtils.sortByBeginPosition(list);
        String className = getEnclosedTypeName(list.get(0));
        lineMap.put(line, getFullName(packageName, className));
    }

    private static String getFullName(String packageName, String className) {
        return StringUtils.isBlank(packageName) ? className : packageName + "." + className;
    }

    private static Map<Integer, String> getClassNameForLines(InputStream in, int[] lines) throws IOException {
        CompilationUnit cu = JavaParser.parse(in);
        final String packageName = cu.getPackageDeclaration().isPresent()
                ? cu.getPackageDeclaration().get().getNameAsString()
                : null;

        Map<Integer, String> res = new HashMap<Integer, String>();
        Arrays.stream(lines).forEach(line -> resolveClassnameOnLine(cu, line, res, packageName));

        return res;
    }

}
