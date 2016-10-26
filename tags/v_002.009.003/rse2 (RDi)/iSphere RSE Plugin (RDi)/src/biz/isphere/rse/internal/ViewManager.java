/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import org.eclipse.rse.core.RSECorePlugin;

import biz.isphere.core.internal.viewmanager.AbstractViewManager;
import biz.isphere.core.internal.viewmanager.IViewManager;

public class ViewManager extends AbstractViewManager {

    public ViewManager(String name) {
        super(name);

        if (!IViewManager.DATA_SPACE_MONITOR_VIEWS.equals(name) && !IViewManager.DATA_QUEUE_MONITOR_VIEWS.equals(name) && !IViewManager.TN5250J_SESSION_VIEWS.equals(name)) {
            throw new RuntimeException("'name' does not match one of the constants of IViewManager"); //$NON-NLS-1$
        }
    }

    @Override
    public boolean isInitialized(int timeout) {

        final int THREAD_SLEEP_TIME = 100;

        try {
            while (timeout > 0 && !RSECorePlugin.getDefault().getPersistenceManager().isRestoreComplete()) {
                Thread.sleep(THREAD_SLEEP_TIME);
                timeout = timeout - THREAD_SLEEP_TIME;
            }
        } catch (InterruptedException e) {
        }

        if (RSECorePlugin.getDefault().getPersistenceManager().isRestoreComplete()) {
            return true;
        }

        return false;
    }
}
