/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.lpex.action;

import org.eclipse.jface.dialogs.MessageDialog;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.strpreprc.Messages;
import biz.isphere.strpreprc.model.StrPrePrcParser;

import com.ibm.lpex.core.LpexView;

/**
 * This action removes the STRPREPRC header from a source member.
 */
public class RemoveHeaderAction extends AbstractHeaderAction {

    public static final String ID = "SprPrePrc.RemoveHeader";

    public void doAction(LpexView view) {

        try {

            StrPrePrcParser header = new StrPrePrcParser(null);
            if (!header.loadFromLpexView(view)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, "STRPREPRC header not found or incomplete.");
                return;
            }

            if (MessageDialog.openConfirm(getShell(), "Confirmation", "Remove SPRPREPRC header from source member?")) {
                header.removeFromLpexView(view);
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Could not remove STRPREPRC header from source. ***", e);
        }
    }

    public static String getLPEXMenuAction() {
        return "\"" + Messages.Menu_Remove_header + "\" " + RemoveHeaderAction.ID;
    }
}
