/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.popupmenu.extension.point;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.annotations.ISphereDeprecated;
import biz.isphere.core.spooledfiles.SpooledFile;

public interface ISpooledFilePopupMenuContributionItem {

    public String getText();

    public String getTooltipText();

    public Image getImage();

    @ISphereDeprecated(info = "SpooledFile will be changed to ISpooledFileId in the next release.")
    public void setSelection(Shell shell, SpooledFile[] spooledFiles);

    public boolean isEnabled();

    public void execute() throws Exception;

}
