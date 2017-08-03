package org.bac.gati.tools.journalexplorer.ui.dialogs;

import org.bac.gati.tools.journalexplorer.internals.Messages;
import org.bac.gati.tools.journalexplorer.ui.JournalExplorerPlugin;
import org.bac.gati.tools.journalexplorer.ui.labelProviders.IBMiConnectionLabelProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.journalexplorer.rse.shared.model.ConnectionDelegate;

public class AddJournalDialog extends XDialog {

    private static final String CONNECTION = "CONNECTION";
    private static final String LIBRARY = "LIBRARY";
    private static final String FILE = "FILE";

    private Text txtLibrary;

    private Text txtFileName;

    private ComboViewer cmbConnections;

    private String library;

    private String fileName;

    private ConnectionDelegate connection;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public AddJournalDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {

        super.create();
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite)super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout)container.getLayout();
        gridLayout.numColumns = 2;
        gridLayout.verticalSpacing = 5;

        Label lblConnections = new Label(container, SWT.NONE);
        lblConnections.setText(Messages.AddJournalDialog_Conection);

        this.cmbConnections = new ComboViewer(container, SWT.READ_ONLY);
        this.configureConnectionsCombo();

        Label lblLibrary = new Label(container, SWT.NONE);
        lblLibrary.setText(Messages.AddJournalDialog_Library);

        this.txtLibrary = new Text(container, SWT.BORDER);
        this.txtLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblFileName = new Label(container, SWT.NONE);
        lblFileName.setText(Messages.AddJournalDialog_FileName);

        this.txtFileName = new Text(container, SWT.BORDER);
        this.txtFileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        loadValues();

        return container;
    }

    private void loadValues() {

        String connectionName = loadValue(CONNECTION, null);
        if (connectionName == null) {
            Object object = cmbConnections.getElementAt(0);
            if (ConnectionDelegate.instanceOf(object)) {
                ConnectionDelegate connection = new ConnectionDelegate(object);
                connectionName = connection.getConnectionName();
            }
        }

        if (connectionName != null) {
            cmbConnections.setSelection(new StructuredSelection(ConnectionDelegate.getConnection(connectionName)));
        }

        txtLibrary.setText(loadValue(LIBRARY, ""));
        txtFileName.setText(loadValue(FILE, ""));
    }

    private void storeValues() {

        storeValue(FILE, fileName);
        storeValue(LIBRARY, library);
        storeValue(CONNECTION, connection.getConnectionName());
    }

    private void configureConnectionsCombo() {

        this.cmbConnections.setContentProvider(new ArrayContentProvider());
        this.cmbConnections.setLabelProvider(new IBMiConnectionLabelProvider());
        this.cmbConnections.setInput(ConnectionDelegate.getConnections());
        this.cmbConnections.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {

                IStructuredSelection selection = (IStructuredSelection)event.getSelection();

                if (selection.size() > 0) {
                    AddJournalDialog.this.connection = new ConnectionDelegate(selection.getFirstElement());
                }
            }
        });
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(216, 184);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.AddJournalDialog_OpenJournal);

    }

    @Override
    protected void okPressed() {

        if (this.saveInput()) {
            super.okPressed();
        }
    };

    private boolean saveInput() {

        if (this.txtFileName.getText().trim() != "" && this.txtLibrary.getText().trim() != "" && this.connection != null) { //$NON-NLS-1$ //$NON-NLS-2$
            this.fileName = this.txtFileName.getText();
            this.library = this.txtLibrary.getText();

            storeValues();

            return true;

        } else {
            MessageDialog.openError(this.getShell(), Messages.Error, Messages.AddJournalDialog_AllDataRequired); //$NON-NLS-1$
            return false;
        }
    }

    public String getFileName() {

        return this.fileName.toUpperCase();
    }

    public String getLibrary() {

        return this.library.toUpperCase();
    }

    public String getConnectionName() {

        return this.connection.getConnectionName();
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
        return getShell().computeSize(500, SWT.DEFAULT, true);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(JournalExplorerPlugin.getDefault().getDialogSettings());
    }
}
