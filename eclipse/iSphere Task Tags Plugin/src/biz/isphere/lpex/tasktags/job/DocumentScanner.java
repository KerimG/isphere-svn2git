/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.tasktags.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.lpex.tasktags.ISphereLpexTasksPlugin;
import biz.isphere.lpex.tasktags.model.LPEXTaskManager;

/**
 * This class represents a background job, that scans a given document for LPEX
 * task tags.
 * 
 * @author Thomas Raddatz
 */
public class DocumentScanner {

    private LPEXTaskManager manager;

    public DocumentScanner(LPEXTaskManager aManager) {
        manager = aManager;
    }

    public void runAsBackgroundProcess(String aName) {
        Job job = new Job(aName) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                runInternally(monitor);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    protected void runInternally(IProgressMonitor monitor) {

        // Calendar start = Calendar.getInstance();

        try {
            manager.removeMarkers();
            if (manager.markerAreEnabled()) {
                manager.createMarkers();
            }

        } catch (Exception e) {
            ISphereLpexTasksPlugin.logError("Failed to process document: " + manager.getDocumentName(), e);
        }

        // Calendar end = Calendar.getInstance();
        // System.out.println("ms: " + (end.getTimeInMillis() -
        // start.getTimeInMillis()));
    }

}
