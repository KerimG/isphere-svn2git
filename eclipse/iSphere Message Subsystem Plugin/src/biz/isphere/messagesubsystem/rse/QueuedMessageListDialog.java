/*******************************************************************************
 * Copyright (c) 2005 SoftLanding Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SoftLanding - initial API and implementation
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/
package biz.isphere.messagesubsystem.rse;

import java.text.DateFormat;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.messagesubsystem.Messages;

public class QueuedMessageListDialog extends XDialog {

    private MonitoringAttributes monitoringAttributes;
    private ReceivedMessage[] queuedMessages;

    private TableViewer tableViewer;
    private StatusLineManager statusLineManager;

    private static final int COLUMN_DATE = 0;
    private static final int COLUMN_TIME = 1;
    private static final int COLUMN_MESSAGE_ID = 2;
    private static final int COLUMN_MESSAGE_TYPE = 3;
    private static final int COLUMN_MESSAGE_TEXT = 4;

    public QueuedMessageListDialog(Shell shell, MonitoringAttributes monitoringAttributes, ReceivedMessage[] queuedMessages) {
        super(shell);
        this.monitoringAttributes = monitoringAttributes;
        this.queuedMessages = queuedMessages;
    }

    public void setContent(ReceivedMessage[] queuedMessages) {

        this.queuedMessages = queuedMessages;
    }

    /**
     * Overridden to set the window title.
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Queued_Messages_title);
    }

    @Override
    public Control createDialogArea(Composite parent) {

        Composite mainArea = (Composite)super.createDialogArea(parent);

        mainArea.setLayout(new GridLayout());
        mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tableViewer = new TableViewer(mainArea, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        addTableColumn(tableViewer, "Date");
        addTableColumn(tableViewer, "Time");
        addTableColumn(tableViewer, "Message ID", 80);
        addTableColumn(tableViewer, "Message Type", 100);
        addTableColumn(tableViewer, "Message text", 250);

        tableViewer.setContentProvider(new ContentProviderMessageItems());
        tableViewer.setLabelProvider(new LabelProviderMessageItems());
        tableViewer.addDoubleClickListener(new DoubleClickListenerMessateItems());

        createStatusLine(mainArea);

        loadScreenValues();

        return mainArea;
    }

    private TableColumn addTableColumn(TableViewer table, String label) {
        return addTableColumn(table, label, 70);
    }

    private TableColumn addTableColumn(TableViewer tableViewer, String label, int width) {
        TableColumn column = new TableColumn(tableViewer.getTable(), SWT.NONE);
        column.setWidth(width);
        column.setText(label);

        return column;
    }

    private void createStatusLine(Composite parent) {
        statusLineManager = new StatusLineManager();
        statusLineManager.createControl(parent, SWT.NONE);
        Control statusLine = statusLineManager.getControl();
        final GridData gridDataStatusLine = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        statusLine.setLayoutData(gridDataStatusLine);
    }

    private void setStatusMessage(String message) {
        if (message != null) {
            statusLineManager.setMessage(null, message);
        } else {
            statusLineManager.setMessage(null, null);
        }
    }

    private void loadScreenValues() {

        tableViewer.setInput(queuedMessages);

        if (monitoringAttributes.isRemoveInformationalMessages()) {
            setStatusMessage("Informational messages are removed on closing the dialog.");
        }
    }

    @Override
    public void okPressed() {
        super.okPressed();
    }

    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        if (id == IDialogConstants.CANCEL_ID) {
            return null;
        }
        return super.createButton(parent, id, label, false);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return new Point(600, 400);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    /**
     * Content provider for the member list table.
     */
    private class ContentProviderMessageItems implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof ReceivedMessage[]) {
                return (ReceivedMessage[])inputElement;
            }
            return new Object[0];
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    /**
     * Content provider for the member list table.
     */
    private class LabelProviderMessageItems extends LabelProvider implements ITableLabelProvider {

        DateFormat dateFormatter = Preferences.getInstance().getDateFormatter();
        DateFormat timeFormatter = Preferences.getInstance().getTimeFormatter();

        public LabelProviderMessageItems() {
            // TODO Auto-generated constructor stub
        }

        public String getColumnText(Object element, int columnIndex) {

            ReceivedMessage member = (ReceivedMessage)element;

            switch (columnIndex) {
            case COLUMN_DATE:
                return dateFormatter.format(member.getDate().getTime());
            case COLUMN_TIME:
                return timeFormatter.format(member.getDate().getTime());
            case COLUMN_MESSAGE_ID:
                return member.getID();
            case COLUMN_MESSAGE_TYPE:
                return member.getMessageType();
            case COLUMN_MESSAGE_TEXT:
                return member.getText();

            default:
                return Messages.EMPTY;
            }
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }

    private class DoubleClickListenerMessateItems implements IDoubleClickListener {

        public void doubleClick(DoubleClickEvent event) {

            if (event.getSelection() instanceof IStructuredSelection) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                if (selection.isEmpty()) {
                    return;
                }

                ReceivedMessage receivedMessage = (ReceivedMessage)selection.getFirstElement();
                QueuedMessageDialog dialog = new QueuedMessageDialog(getShell(), receivedMessage);
                dialog.open();
            }
        }

    }

}
