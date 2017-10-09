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
enum Language {
    JAVA   (".java"),
    CPP    (".cpp"),
    CSHARP (".cs"),
    VBNET  (".vb");

    private String extension;

    Language(String ext) {
        extension = ext;
    }

    public String getExtension() {
        return extension;
    }
}

public class EnumTest {
    public static void doSomething(String handle, Language language) {
        if (language == Language.JAVA) {
            System.out.println("Hope you already know 1.5!");
        }
    }

    public void test() {
        doSomething("abc", Language.JAVA);
        System.out.println ("Available Languages:");
        for(Language l : Language.values())
            System.out.println(l);
        String selectedLang = "CSHARP";
        doSomething("abc", Language.valueOf(selectedLang));

        String extension;
        switch (Language.valueOf(selectedLang)) {
            case JAVA: extension = ".java"; break;
            case CPP: extension = ".cpp"; break;
            case CSHARP: extension = ".cs"; break;
            case VBNET: extension = ".vb"; break;
            default: throw new IllegalArgumentException
                    ("don't know that language extension");
        }

        extension = Language.valueOf(selectedLang).getExtension();

    }
}
