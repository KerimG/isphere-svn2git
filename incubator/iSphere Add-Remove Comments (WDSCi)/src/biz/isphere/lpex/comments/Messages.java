/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.lpex.comments.messages"; //$NON-NLS-1$

    public static String Menu_Source;
    public static String Menu_Comment_Lines;
    public static String Menu_Uncomment_Lines; 
    public static String Menu_Toggle_Comment_Lines;

    public static String Line_A_has_already_been_commented_The_operation_has_been_canceled;
    public static String Selection_has_already_been_commented_The_operation_has_been_canceled;
    public static String Text_limit_would_have_been_exceeded_on_line_A_The_operation_has_been_canceled;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
