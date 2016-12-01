/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer.filters;

import org.eclipse.jface.viewers.Viewer;

import biz.isphere.joblogexplorer.model.JobLogMessage;

public abstract class AbstractIntegerFilter extends AbstractMessagePropertyFilter {

    public static final int NULL_VALUE = -1; //$NON-NLS-1$

    private int intValue;

    public void setValue(String value) {
        super.setValue(value);

        if (UI_SPCVAL_ALL.equals(this.value)) {
            // Does not matter
        } else if (UI_SPCVAL_BLANK.equals(this.value)) {
            this.intValue = NULL_VALUE;
        } else {
            this.intValue = Integer.parseInt(value);
        }
    }

    @Override
    public boolean select(Viewer tableViewer, Object parentElement, JobLogMessage element) {

        if (super.select(tableViewer, parentElement, element)) {
            return true;
        }

        JobLogMessage jobLogMessage = (JobLogMessage)element;
        int currentValue = getCurrentValue(jobLogMessage);
        return intValue == currentValue;
    }

    protected abstract int getCurrentValue(JobLogMessage jobLogMessage);
}
