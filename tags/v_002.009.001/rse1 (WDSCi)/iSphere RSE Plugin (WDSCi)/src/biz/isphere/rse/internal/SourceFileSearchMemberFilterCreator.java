/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import biz.isphere.core.internal.ISourceFileSearchMemberFilterCreator;
import biz.isphere.core.sourcefilesearch.SearchResult;

import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.core.api.ISeriesConnection;

public class SourceFileSearchMemberFilterCreator implements ISourceFileSearchMemberFilterCreator {

    public boolean createMemberFilter(String connectionName, String filterName, SearchResult[] searchResults) {

        ISeriesMemberFilterString[] filterStrings = new ISeriesMemberFilterString[searchResults.length];

        for (int idx = 0; idx < searchResults.length; idx++) {

            ISeriesMemberFilterString filterString = new ISeriesMemberFilterString();
            filterString.setLibrary(searchResults[idx].getLibrary());
            filterString.setFile(searchResults[idx].getFile());
            filterString.setMember(searchResults[idx].getMember());
            filterString.setMemberType("*");

            filterStrings[idx] = filterString;

        }

        ISeriesConnection connection = ISeriesConnection.getConnection(connectionName);
        if (RSEHelper.createMemberFilter((ISeriesConnection)connection, filterName, filterStrings) == null) {
            return false;
        } else {
            return true;
        }

    }

}
