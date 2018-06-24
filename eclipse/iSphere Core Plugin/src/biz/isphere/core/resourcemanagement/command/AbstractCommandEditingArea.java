/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.command;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;
import biz.isphere.core.resourcemanagement.AbstractEditingArea;
import biz.isphere.core.resourcemanagement.AbstractResource;

public abstract class AbstractCommandEditingArea extends AbstractEditingArea {

    private boolean singleCompileType;

    public AbstractCommandEditingArea(Composite parent, AbstractResource[] resources, boolean both, boolean singleCompileType) {
        super(parent, resources, both, new CommandQualifier(singleCompileType));
        this.singleCompileType = singleCompileType;
    }

    @Override
    public void addTableColumns(Table tableResources) {

        CommandQualifier qualifier = (CommandQualifier)tableResources.getData("Qualifier");

        if (!qualifier.isSingleCompileType()) {
            TableColumn columnName = new TableColumn(tableResources, SWT.NONE);
            columnName.setWidth(Size.getSize(100));
            columnName.setText(Messages.Compile_type);
        }

        TableColumn columnName = new TableColumn(tableResources, SWT.NONE);
        columnName.setWidth(Size.getSize(100));
        columnName.setText(Messages.Label);

        TableColumn columnType = new TableColumn(tableResources, SWT.NONE);
        columnType.setWidth(Size.getSize(600));
        columnType.setText(Messages.Command);

    }

    @Override
    public String getTableColumnText(Object resource, int columnIndex) {

        int counter = 0;
        if (!singleCompileType) {
            counter++;
            if (columnIndex == 0) {
                return (((RSECommand)resource).getCompileType().getName());
            }
        }

        if (columnIndex == 0 + counter) {
            return (((RSECommand)resource).getLabel());
        } else if (columnIndex == 1 + counter) {
            return (((RSECommand)resource).getCommandString());
        } else if (columnIndex == 2 + counter) {
            return "???";
        } else {
            return "";
        }

    }

    @Override
    protected Image getTableColumnImage(Object resource, int columnIndex) {

        RSECommand command = (RSECommand)resource;

        int counter = 0;
        if (!singleCompileType) {
            counter++;
        }

        if (columnIndex == 0 + counter) {
            if (command.isEditable()) {
                return ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_UNLOCKED);
            } else {
                return ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_LOCKED);
            }
        } else {
            return null;
        }
    }

    @Override
    public int compareResources(Object resource1, Object resource2) {

        if (!singleCompileType) {
            int result = (((RSECommand)resource1).getCompileType().getName()).compareTo((((RSECommand)resource2).getCompileType().getName()));
            if (result != 0) {
                return result;
            }
        }

        return (((RSECommand)resource1).getLabel()).compareTo((((RSECommand)resource2).getLabel()));

    }

}
