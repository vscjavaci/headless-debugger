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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AutomaticResourceManagementTest {
    public void test() {
        try {
            File tempFile = File.createTempFile("test", "arm");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                try (DataOutputStream dos = new DataOutputStream(fos)) {
                    dos.writeUTF("Java 7 Block Buster");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            tempFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
