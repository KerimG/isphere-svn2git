/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.shared;

import biz.isphere.core.internal.ISeries;
import biz.isphere.journalexplorer.core.handlers.ISelectedJournal;
import biz.isphere.journalexplorer.core.internals.QualifiedName;

public class Journal implements ISelectedJournal {

    private String connectionName;
    private QualifiedName qualifiedName;

    public Journal(String connectionName, String libraryName, String journalName) {
        this.connectionName = connectionName;
        this.qualifiedName = new QualifiedName(libraryName, journalName);
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getName() {
        return qualifiedName.getObjectName();
    }

    public String getLibrary() {
        return qualifiedName.getObjectName();
    }

    public String getObjectType() {
        return ISeries.JRN;
    }

    public String getQualifiedName() {
        return qualifiedName.getQualifiedName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionName == null) ? 0 : connectionName.hashCode());
        result = prime * result + ((qualifiedName == null) ? 0 : qualifiedName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Journal other = (Journal)obj;
        if (connectionName == null) {
            if (other.connectionName != null) return false;
        } else if (!connectionName.equals(other.connectionName)) return false;
        if (qualifiedName == null) {
            if (other.qualifiedName != null) return false;
        } else if (!qualifiedName.equals(other.qualifiedName)) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", qualifiedName.getQualifiedName(), getObjectType());
    }
}
