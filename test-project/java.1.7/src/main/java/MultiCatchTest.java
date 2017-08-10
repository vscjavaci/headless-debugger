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

public class MultiCatchTest {
    static void rethrow(String s) throws FirstException, SecondException,
            ThirdException {
        try {
            if (s.equals("First"))
                throw new FirstException("First");
            else if (s.equals("Second"))
                throw new SecondException("Second");
            else
                throw new ThirdException("Third");
        } catch (Exception e) {
            throw e;
        }
    }

    static class FirstException extends Exception {

        public FirstException(String msg) {
            super(msg);
        }
    }

    static class SecondException extends Exception {

        public SecondException(String msg) {
            super(msg);
        }
    }

    static class ThirdException extends Exception {

        public ThirdException(String msg) {
            super(msg);
        }
    }

    public void test() {
        try{
            rethrow("abc");
        }catch(FirstException | SecondException | ThirdException e){
            //e = new Exception();
            System.out.println(e.getMessage());
        }
    }
}
