/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.journalexplorer.core.internals.JoesdParser;
import biz.isphere.journalexplorer.core.internals.QualifiedName;
import biz.isphere.journalexplorer.core.model.dao.JournalOutputType;
import biz.isphere.journalexplorer.core.ui.widgets.contentassist.TableColumnContentAssistProposal;

import com.ibm.as400.access.Record;

/**
 * This class represents the meta data of a table. It contains the name and
 * library of the table and a list of its fields. Also it contains the name and
 * library of the table used to retrieve its structure. Most of the time the
 * attributes name and library will be equal to definitionName and
 * definitionLibrary, but this allows to override the table and library from
 * used as reference to retrieve the meta data. This can be useful if the
 * programmer wants to parse a table row with a different structure Specifying a
 * different definitionName and definitionLibrary than name and library, can
 * generate unexpected results, use with caution
 * 
 * @author Isaac Ramirez Herrera
 */
public class MetaTable {

    private String connectionName;
    private String library;
    private String name;
    private String objectType;

    private String definitionConnection;
    private String definitionName;
    private String definitionLibrary;
    private String definitionObjectType;

    private LinkedHashMap<String, MetaColumn> columns;

    private boolean loaded;
    private int parsingOffset;
    private JournalOutputType outfileType;
    private int countNullableFields;
    private int lastNullableFieldIndex;
    private int recordLength;

    private Set<String> warningMessages;

    public MetaTable(String connectionName, String name, String library, String objectType) {

        this.connectionName = connectionName;
        this.library = library.trim();
        this.name = name.trim();
        this.objectType = objectType.trim();

        setDefinitionConnectionName(this.connectionName);
        setDefinitionLibrary(this.library);
        setDefinitionName(this.name);
        setDefinitionObjectType(this.objectType);

        this.columns = new LinkedHashMap<String, MetaColumn>();
        this.loaded = false;
        this.parsingOffset = 0;
        this.countNullableFields = 0;
        this.lastNullableFieldIndex = 0;

        this.warningMessages = new HashSet<String>();
    }

    public boolean isJournalOutputFile() {

        if (!JournalOutputType.NONE.equals(getOutfileType())) {
            return true;
        }

        return false;
    }

    public void addWarningMessage(String message) {
        warningMessages.add(message);
    }

    public boolean hasWarningMessage(String message) {
        return warningMessages.contains(message);
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getLibrary() {
        return library;
    }

    public String getName() {
        return name;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setDefinitionConnectionName(String definitionConection) {
        this.definitionConnection = definitionConection.trim();
    }

    public void setDefinitionLibrary(String definitionLibrary) {
        this.definitionLibrary = definitionLibrary.trim();
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName.trim();
    }

    public void setDefinitionObjectType(String objectType) {
        this.definitionObjectType = objectType.trim();
    }

    public String getDefinitionConnectionName() {
        return definitionConnection;
    }

    public String getDefinitionLibrary() {
        return definitionLibrary;
    }

    public String getDefinitionName() {
        return definitionName;
    }

    public String getDefinitionObjectType() {
        return definitionObjectType;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {

        Record record = null;

        if (loaded) {
            try {
                JoesdParser parser = new JoesdParser(this);
                record = parser.getJoesdRecordFormat().getNewRecord();
            } catch (Exception e) {
                ISpherePlugin.logError("*** Failed to set 'loaded' attribute ***", e); //$NON-NLS-1$
                loaded = false;
            }
        }

        this.loaded = loaded;

        if (loaded) {
            recordLength = record.getRecordLength();
        } else {
            recordLength = 0;
            warningMessages.clear();
        }
    }

    public ContentAssistProposal[] getContentAssistProposals() {

        MetaColumn[] metaColumns = getColumns();

        ContentAssistProposal[] proposals = new ContentAssistProposal[metaColumns.length];
        for (int i = 0; i < proposals.length; i++) {
            MetaColumn metaColumn = metaColumns[i];
            proposals[i] = new TableColumnContentAssistProposal(metaColumn.getName(), metaColumn.getType().toString(), metaColumn.getText());
        }

        return proposals;
    }

    public int getRecordLength() {
        return recordLength;
    }

    public int getLastNullableFieldIndex() {
        return lastNullableFieldIndex;
    }

    public boolean hasNullableFields() {

        if (countNullableFields > 0) {
            return true;
        }

        return false;
    }

    public int getParsingOffset() {
        return parsingOffset;
    }

    public void setParsingOffset(int parsingOffset) {
        this.parsingOffset = parsingOffset;
    }

    public void addColumn(MetaColumn column) {
        columns.put(column.getName(), column);

        if (column.isNullable()) {
            countNullableFields++;
            lastNullableFieldIndex = columns.size();
        }

    }

    public boolean hasColumns() {

        if (columns.size() > 0) {
            return true;
        }

        return false;
    }

    public MetaColumn getColumn(String name) {
        return columns.get(name);
    }

    public MetaColumn[] getColumns() {
        return columns.values().toArray(new MetaColumn[columns.size()]);
    }

    public void clearColumns() {
        this.columns.clear();
    }

    public boolean hasColumn(String columnName) {
        return columns.containsKey(columnName);
    }

    public String getQualifiedName() {
        return QualifiedName.getName(getLibrary(), getName());
    }

    public JournalOutputType getOutfileType() {

        if (outfileType == null) {

            if (hasColumn("JOPGMLIB")) { //$NON-NLS-1$
                // Added with *TYPE5
                outfileType = JournalOutputType.TYPE5;
            } else if (hasColumn("JOJID")) { //$NON-NLS-1$
                // Added with *TYPE4
                outfileType = JournalOutputType.TYPE4;
            } else if (hasColumn("JOTSTP")) { //$NON-NLS-1$
                // Added with *TYPE3
                outfileType = JournalOutputType.TYPE3;
            } else if (hasColumn("JOUSPF")) { //$NON-NLS-1$
                // Added with *TYPE2
                outfileType = JournalOutputType.TYPE2;
            } else if (hasColumn("JOUSPF")) { //$NON-NLS-1$
                outfileType = JournalOutputType.TYPE1;
            } else {
                outfileType = JournalOutputType.NONE;
            }

        }

        return outfileType;
    }

    @Override
    public String toString() {

        String qualifiedName = QualifiedName.getName(getLibrary(), getName());
        String qualifiedDefinitionName = QualifiedName.getName(getLibrary(), getName());

        StringBuilder buffer = new StringBuilder(qualifiedName);
        if (!qualifiedName.equals(qualifiedDefinitionName)) {
            buffer.append(" ==> "); //$NON-NLS-1$
            buffer.append(qualifiedDefinitionName);
        }

        return buffer.toString();
    }
}
