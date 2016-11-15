/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.perspective.rse;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public abstract class AbstractJobLogExplorerPerspectiveLayout implements IPerspectiveFactory {

    public static final String ID = "biz.isphere.joblogexplorer.rse.perspective.JobLogExplorerPerspectiveLayout";//$NON-NLS-1$

    private static final String NAV_FOLDER_ID = "biz.isphere.joblogexplorer.rse.perspective.JobLogExplorerPerspectiveLayout.NavFolder";//$NON-NLS-1$

    public void createInitialLayout(IPageLayout layout) {

        defineLayout(layout);
    }

    private void defineLayout(IPageLayout layout) {

        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        // Place remote system view to left of
        // editor area.
        IFolderLayout folder = layout.createFolder(NAV_FOLDER_ID, IPageLayout.LEFT, 0.25F, editorArea);

        folder.addView(getRemoveSystemsViewID());

        layout.addShowViewShortcut(getRemoveSystemsViewID());

        layout.addPerspectiveShortcut(ID);
    }

    protected abstract String getRemoveSystemsViewID();
}