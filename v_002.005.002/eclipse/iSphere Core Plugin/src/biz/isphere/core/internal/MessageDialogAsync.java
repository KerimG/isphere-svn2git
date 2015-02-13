/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;

public class MessageDialogAsync {

    public static void displayError(Shell shell, String message) {
        int kind = MessageDialog.ERROR;
        MessageDialog dialog = new MessageDialog(shell, Messages.E_R_R_O_R, null, message, kind, getButtonLabels(kind), 0);
        MessageDialogUIJob job = new MessageDialogUIJob(shell.getDisplay(), dialog);
        job.schedule();
    }

    /**
     * @param kind
     * @return
     */
    static String[] getButtonLabels(int kind) {
        String[] dialogButtonLabels;
        switch (kind) {
        case MessageDialog.ERROR:
        case MessageDialog.INFORMATION:
        case MessageDialog.WARNING: {
            dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL };
            break;
        }
        case MessageDialog.QUESTION: {
            dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL };
            break;
        }
        default: {
            throw new IllegalArgumentException("Illegal value for kind in MessageDialog.open()"); //$NON-NLS-1$
        }
        }
        return dialogButtonLabels;
    }

}
