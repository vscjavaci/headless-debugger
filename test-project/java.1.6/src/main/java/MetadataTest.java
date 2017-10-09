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

@Note(note = "Finish this class!", version = 1)
class NoteDemo {

}

public class MetadataTest {
    public void test() {
        Note note = NoteDemo.class.getAnnotation(Note.class);
        if (note != null) {
            System.out.println("Author=" + note.author());
            System.out.println("Note=" + note.note());
            System.out.println("Version=" + note.version());
        } else {
            System.out.println("Note not found.");
        }
    }
}
