/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.compareeditor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.compareeditor.CompareDialog;
import biz.isphere.core.internal.Member;
import biz.isphere.rse.Messages;
import biz.isphere.rse.internal.RSEMember;

import com.ibm.etools.iseries.rse.ui.widgets.IBMiConnectionCombo;
import com.ibm.etools.iseries.rse.ui.widgets.QSYSMemberPrompt;
import com.ibm.etools.iseries.services.qsys.api.IQSYSFile;
import com.ibm.etools.iseries.services.qsys.api.IQSYSLibrary;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class RSECompareDialog extends CompareDialog {

    private Group ancestorGroup;

    private IBMiConnectionCombo leftConnectionCombo;
    private QSYSMemberPrompt leftMemberPrompt;
    private IBMiConnection leftConnection;
    private String leftLibrary;
    private String leftFile;
    private String leftMember;

    private IBMiConnectionCombo rightConnectionCombo;
    private QSYSMemberPrompt rightMemberPrompt;
    private IBMiConnection rightConnection;
    private String rightLibrary;
    private String rightFile;
    private String rightMember;

    private IBMiConnectionCombo ancestorConnectionCombo;
    private QSYSMemberPrompt ancestorMemberPrompt;
    private IBMiConnection ancestorConnection;
    private String ancestorLibrary;
    private String ancestorFile;
    private String ancestorMember;

    /**
     * Creates a three-way compare dialog.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option
     *        "Open for browse/edit" is displayed
     * @param leftMember - the left selected member
     * @param rightMember - the right selected member
     * @param ancestorMember - the ancestor member
     */
    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember leftMember, RSEMember rightMember, RSEMember ancestorMember) {
        super(parentShell, selectEditable, leftMember, rightMember, ancestorMember);
        initializeLeftMember(leftMember);
        initializeRightMember(rightMember);
    }

    /**
     * Creates the compare dialog, for 3 and more selected member.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option
     *        "Open for browse/edit" is displayed
     * @param selectedMembers - the selected members that go to the left side of
     *        the compare dialog
     */
    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember[] selectedMembers) {
        super(parentShell, selectEditable, selectedMembers);
        initializeLeftMember(selectedMembers[0]);
        initializeRightMember(selectedMembers[0]);
    }

    /**
     * Creates the compare dialog, for 2 selected member.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option
     *        "Open for browse/edit" is displayed
     * @param leftMember - the left selected member
     * @param rightMember - the right selected member
     */
    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember leftMember, RSEMember rightMember) {
        super(parentShell, selectEditable, leftMember, rightMember);
        initializeLeftMember(leftMember);
        initializeRightMember(rightMember);
    }

    /**
     * Creates the compare dialog, for 1 selected member.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option
     *        "Open for browse/edit" is displayed
     * @param leftMember - the left selected member
     */
    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember leftMember) {
        super(parentShell, selectEditable, leftMember);
        initializeLeftMember(leftMember);
    }

    /**
     * Creates the compare dialog, for 0 selected members.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option
     *        "Open for browse/edit" is displayed
     */
    public RSECompareDialog(Shell parentShell, boolean selectEditable) {
        super(parentShell, selectEditable);
    }

    private void initializeLeftMember(RSEMember leftMember) {
        this.leftConnection = leftMember.getRSEConnection();
        this.leftLibrary = leftMember.getLibrary();
        this.leftFile = leftMember.getSourceFile();
        this.leftMember = leftMember.getMember();
    }

    private void initializeRightMember(RSEMember rightMember) {
        this.rightConnection = rightMember.getRSEConnection();
        this.rightLibrary = rightMember.getLibrary();
        this.rightFile = rightMember.getSourceFile();
        this.rightMember = rightMember.getMember();
    }

    @Override
    protected void createEditableLeftArea(Composite parent) {

        Group leftGroup = new Group(parent, SWT.NONE);
        leftGroup.setText(Messages.Right);
        GridLayout rightLayout = new GridLayout();
        rightLayout.numColumns = 1;
        leftGroup.setLayout(rightLayout);
        leftGroup.setLayoutData(getGridData());

        leftConnectionCombo = new IBMiConnectionCombo(leftGroup, getLeftConnection(), false);
        leftConnectionCombo.setLayoutData(getGridData());
        leftConnectionCombo.getCombo().setLayoutData(getGridData());

        leftConnectionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getOkButton().setEnabled(canFinish());
                leftMemberPrompt.setSystemConnection(leftConnectionCombo.getHost());
            }
        });

        leftMemberPrompt = new QSYSMemberPrompt(leftGroup, SWT.NONE, false, true, QSYSMemberPrompt.FILETYPE_SRC);
        if (hasLeftMember()) {
            leftMemberPrompt.setSystemConnection(getLeftConnection().getHost());
            leftMemberPrompt.setLibraryName(getLeftLibrary());
            leftMemberPrompt.setFileName(getLeftFile());
            leftMemberPrompt.setMemberName(getLeftMember());
        } else {
            leftMemberPrompt.setSystemConnection(null);
            leftMemberPrompt.setLibraryName("");
            leftMemberPrompt.setFileName("");
            leftMemberPrompt.setMemberName("");
        }

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getOkButton().setEnabled(canFinish());
            }
        };

        leftMemberPrompt.getMemberCombo().addModifyListener(modifyListener);
        leftMemberPrompt.getFileCombo().addModifyListener(modifyListener);
        leftMemberPrompt.getLibraryCombo().addModifyListener(modifyListener);
        leftMemberPrompt.getLibraryCombo().setFocus();
    }

    @Override
    protected void createEditableRightArea(Composite parent) {

        Group rightGroup = new Group(parent, SWT.NONE);
        rightGroup.setText(Messages.Right);
        GridLayout rightLayout = new GridLayout();
        rightLayout.numColumns = 1;
        rightGroup.setLayout(rightLayout);
        rightGroup.setLayoutData(getGridData());

        // Initialize right connection with left connection
        rightConnectionCombo = new IBMiConnectionCombo(rightGroup, getLeftConnection(), false);
        rightConnectionCombo.setLayoutData(getGridData());
        rightConnectionCombo.getCombo().setLayoutData(getGridData());

        rightConnectionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getOkButton().setEnabled(canFinish());
                rightMemberPrompt.setSystemConnection(rightConnectionCombo.getHost());
            }
        });

        rightMemberPrompt = new QSYSMemberPrompt(rightGroup, SWT.NONE, false, true, QSYSMemberPrompt.FILETYPE_SRC);
        rightMemberPrompt.setSystemConnection(rightConnectionCombo.getHost());
        // Initialize right member with left member
        rightMemberPrompt.setLibraryName(getLeftLibrary());
        rightMemberPrompt.setFileName(getLeftFile());

        if (hasMultipleRightMembers()) {
            rightMemberPrompt.setMemberName(SPECIAL_MEMBER_NAME_LEFT);
        } else {
            rightMemberPrompt.setMemberName(getLeftMember());
        }

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getOkButton().setEnabled(canFinish());
            }
        };

        rightMemberPrompt.getMemberCombo().addModifyListener(modifyListener);
        rightMemberPrompt.getFileCombo().addModifyListener(modifyListener);
        rightMemberPrompt.getLibraryCombo().addModifyListener(modifyListener);
        rightMemberPrompt.getLibraryCombo().setFocus();

        setRightMemberPromptEnablement(!hasMultipleRightMembers());
    }

    @Override
    protected void createEditableAncestorArea(Composite parent) {

        ancestorGroup = new Group(parent, SWT.NONE);
        ancestorGroup.setText(Messages.Ancestor);
        GridLayout ancestorLayout = new GridLayout();
        ancestorLayout.numColumns = 1;
        ancestorGroup.setLayout(ancestorLayout);
        ancestorGroup.setLayoutData(getGridData());

        // Initialize ancestor connection with left connection
        ancestorConnectionCombo = new IBMiConnectionCombo(ancestorGroup, getLeftConnection(), false);
        ancestorConnectionCombo.setLayoutData(getGridData());
        ancestorConnectionCombo.getCombo().setLayoutData(getGridData());

        ancestorConnectionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getOkButton().setEnabled(canFinish());
                ancestorMemberPrompt.setSystemConnection(ancestorConnectionCombo.getHost());
            }
        });

        ancestorMemberPrompt = new QSYSMemberPrompt(ancestorGroup, SWT.NONE, false, true, QSYSMemberPrompt.FILETYPE_SRC);
        ancestorMemberPrompt.setSystemConnection(ancestorConnectionCombo.getHost());
        // Initialize ancestor member with left member
        ancestorMemberPrompt.setLibraryName(getLeftLibrary());
        ancestorMemberPrompt.setFileName(getLeftFile());
        ancestorMemberPrompt.setMemberName(getLeftMember());

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getOkButton().setEnabled(canFinish());
            }
        };

        ancestorMemberPrompt.getMemberCombo().addModifyListener(modifyListener);
        ancestorMemberPrompt.getFileCombo().addModifyListener(modifyListener);
        ancestorMemberPrompt.getLibraryCombo().addModifyListener(modifyListener);

    }

    @Override
    protected void setAncestorVisible(boolean visible) {
        ancestorGroup.setVisible(visible);
        setFocus();
    }

    @Override
    public void setFocus() {

        if (leftMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentLeftLibraryName())) {
            leftMemberPrompt.getLibraryCombo().setFocus();
            return;
        }
        if (leftMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentLeftFileName())) {
            leftMemberPrompt.getFileCombo().setFocus();
            return;
        }
        if (leftMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentLeftMemberName())) {
            leftMemberPrompt.getMemberCombo().setFocus();
            return;
        }

        if (rightMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentRightLibraryName())) {
            rightMemberPrompt.getLibraryCombo().setFocus();
            return;
        }
        if (rightMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentRightFileName())) {
            rightMemberPrompt.getFileCombo().setFocus();
            return;
        }
        if (rightMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentRightMemberName())) {
            rightMemberPrompt.getMemberCombo().setFocus();
            return;
        }

        if (ancestorMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentAncestorLibraryName())) {
            ancestorMemberPrompt.getLibraryCombo().setFocus();
            return;
        }
        if (ancestorMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentAncestorFileName())) {
            ancestorMemberPrompt.getFileCombo().setFocus();
            return;
        }
        if (ancestorMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentAncestorMemberName())) {
            ancestorMemberPrompt.getMemberCombo().setFocus();
            return;
        }

    }

    @Override
    protected void okPressed() {

        if (!hasLeftMember()) {

            leftConnection = getCurrentLeftConnection();
            leftLibrary = getCurrentLeftLibraryName();
            leftFile = getCurrentLeftFileName();
            leftMember = getCurrentLeftMemberName();

            if (!validateMember(leftConnection, leftLibrary, leftFile, leftMember, leftMemberPrompt)) {
                return;
            }

        }

        if (!hasRightMember() || hasMultipleRightMembers()) {

            rightConnection = getCurrentRightConnection();
            rightLibrary = getCurrentRightLibraryName();
            rightFile = getCurrentRightFileName();

            if (hasMultipleRightMembers()) {
                rightMember = null;
            } else {
                rightMember = getCurrentRightMemberName();
            }

            if (!validateMember(rightConnection, rightLibrary, rightFile, rightMember, rightMemberPrompt)) {
                return;
            }

        }

        if (isThreeWay()) {

            ancestorConnection = getCurrentAncestorConnection();
            ancestorLibrary = getCurrentAncestorLibraryName();
            ancestorFile = getCurrentAncestorFileName();
            ancestorMember = getCurrentAncestorMemberName();

            if (!validateMember(ancestorConnection, ancestorLibrary, ancestorFile, ancestorMember, ancestorMemberPrompt)) {
                return;
            }

        }

        // Close dialog
        super.okPressed();
    }

    private boolean validateMember(IBMiConnection connection, String library, String file, String member, QSYSMemberPrompt memberPrompt) {

        if (!checkLibrary(connection, library)) {
            displayLibraryNotFoundMessage(library, memberPrompt);
            return false;
        }

        if (!checkFile(connection, library, file)) {
            displayFileNotFoundMessage(library, file, memberPrompt);
            return false;
        }

        if (member != null && !checkMember(connection, library, file, member)) {
            displayMemberNotFoundMessage(library, file, member, memberPrompt);
            return false;
        }

        return true;
    }

    private boolean checkLibrary(IBMiConnection connection, String libraryName) {

        IQSYSLibrary qsysLibrary = null;
        try {
            qsysLibrary = connection.getLibrary(libraryName, null);
        } catch (Exception e) {
        }

        if (qsysLibrary != null) {
            return true;
        }

        return false;
    }

    private void displayLibraryNotFoundMessage(String libraryName, QSYSMemberPrompt qsysMemberPrompt) {

        String message = biz.isphere.core.Messages.bind(Messages.Library_A_not_found, new Object[] { libraryName });
        MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, message);
        qsysMemberPrompt.getLibraryCombo().setFocus();
    }

    private boolean checkFile(IBMiConnection connection, String libraryName, String fileName) {

        IQSYSFile qsysFile = null;
        try {
            qsysFile = connection.getFile(libraryName, fileName, null);
        } catch (Exception e) {
        }

        if (qsysFile != null) {
            return true;
        }

        return false;
    }

    private void displayFileNotFoundMessage(String libraryName, String fileName, QSYSMemberPrompt qsysMemberPrompt) {

        String message = biz.isphere.core.Messages.bind(Messages.File_A_in_library_B_not_found, new Object[] { fileName, libraryName });
        MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, message);
        qsysMemberPrompt.getFileCombo().setFocus();
    }

    private boolean checkMember(IBMiConnection connection, String libraryName, String fileName, String memberName) {

        RSEMember rseMember = getRSEMember(connection, libraryName, fileName, memberName);
        if (rseMember == null) {
            return false;
        }

        if (rseMember.exists()) {
            return true;
        }

        return false;
    }

    private void displayMemberNotFoundMessage(String libraryName, String fileName, String memberName, QSYSMemberPrompt qsysMemberPrompt) {

        String message = biz.isphere.core.Messages.bind(biz.isphere.core.Messages.Member_2_of_file_1_in_library_0_not_found, new Object[] {
            libraryName, fileName, memberName });
        MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, message);
        qsysMemberPrompt.getMemberCombo().setFocus();

    }

    @Override
    protected boolean canFinish() {

        // Check left member is specified
        if (StringHelper.isNullOrEmpty(getCurrentLeftConnectionName()) || StringHelper.isNullOrEmpty(getCurrentLeftLibraryName())
            || StringHelper.isNullOrEmpty(getCurrentLeftFileName()) || StringHelper.isNullOrEmpty(getCurrentLeftMemberName())) {
            return false;
        }

        // Check right member is specified
        if (StringHelper.isNullOrEmpty(getCurrentRightConnectionName()) || StringHelper.isNullOrEmpty(getCurrentRightLibraryName())
            || StringHelper.isNullOrEmpty(getCurrentRightFileName()) || StringHelper.isNullOrEmpty(getCurrentRightMemberName())) {
            return false;
        }

        // Check ancestor member is specified
        if (isThreeWay()) {
            if (StringHelper.isNullOrEmpty(getCurrentAncestorConnectionName()) || StringHelper.isNullOrEmpty(getCurrentAncestorLibraryName())
                || StringHelper.isNullOrEmpty(getCurrentAncestorFileName()) || StringHelper.isNullOrEmpty(getCurrentAncestorMemberName())) {
                return false;
            }
        }

        // Ensure right and left members are different
        if (getCurrentRightConnectionName().equalsIgnoreCase(getCurrentLeftConnectionName())
            && getCurrentRightLibraryName().equalsIgnoreCase(getCurrentLeftLibraryName())
            && getCurrentRightFileName().equalsIgnoreCase(getCurrentLeftFileName())
            && getCurrentRightMemberName().equalsIgnoreCase(getCurrentLeftMemberName())) {
            return false;
        }

        if (isThreeWay()) {
            // Ensure ancestor member is different from right member
            if (getCurrentAncestorConnectionName().equalsIgnoreCase(getCurrentRightConnectionName())
                && getCurrentAncestorLibraryName().equalsIgnoreCase(getCurrentRightLibraryName())
                && getCurrentAncestorFileName().equalsIgnoreCase(getCurrentRightFileName())
                && getCurrentAncestorMemberName().equalsIgnoreCase(getCurrentRightMemberName())) {
                return false;
            }
            // Ensure ancestor member is different from left member
            if (getCurrentAncestorConnectionName().equalsIgnoreCase(getCurrentLeftConnectionName())
                && getCurrentAncestorLibraryName().equalsIgnoreCase(getCurrentLeftLibraryName())
                && getCurrentAncestorFileName().equalsIgnoreCase(getCurrentLeftFileName())
                && getCurrentAncestorMemberName().equalsIgnoreCase(getCurrentLeftMemberName())) {
                return false;
            }
        }

        return true;
    }

    private IBMiConnection getCurrentLeftConnection() {
        if (leftConnectionCombo == null) {
            // return value for read-only left member
            return leftConnection;
        }
        return IBMiConnection.getConnection(getCurrentLeftConnectionName());
    }

    private String getCurrentLeftConnectionName() {
        if (leftConnectionCombo == null) {
            // return value for read-only left member
            return getLeftConnection().getConnectionName();
        }
        return leftConnectionCombo.getHost().getName();
    }

    private String getCurrentLeftLibraryName() {
        if (leftMemberPrompt == null) {
            // return value for read-only left member
            return getLeftLibrary();
        }
        return leftMemberPrompt.getLibraryName();
    }

    private String getCurrentLeftFileName() {
        if (leftMemberPrompt == null) {
            // return value for read-only left member
            return getLeftFile();
        }
        return leftMemberPrompt.getFileName();
    }

    private String getCurrentLeftMemberName() {
        if (leftMemberPrompt == null) {
            // return value for read-only left member
            return getLeftMember();
        }
        return leftMemberPrompt.getMemberName();
    }

    private void setRightMemberPromptEnablement(boolean enabled) {
        rightMemberPrompt.getMemberCombo().setEnabled(enabled);
        rightMemberPrompt.getMemberBrowseButton().setEnabled(enabled);
    }

    private IBMiConnection getCurrentRightConnection() {
        return IBMiConnection.getConnection(getCurrentRightConnectionName());
    }

    private String getCurrentRightConnectionName() {
        return rightConnectionCombo.getHost().getName();
    }

    private String getCurrentRightLibraryName() {
        return rightMemberPrompt.getLibraryName();
    }

    private String getCurrentRightFileName() {
        return rightMemberPrompt.getFileName();
    }

    private String getCurrentRightMemberName() {
        return rightMemberPrompt.getMemberName();
    }

    private IBMiConnection getCurrentAncestorConnection() {
        return IBMiConnection.getConnection(getCurrentAncestorConnectionName());
    }

    private String getCurrentAncestorConnectionName() {
        return ancestorConnectionCombo.getHost().getName();
    }

    private String getCurrentAncestorLibraryName() {
        return ancestorMemberPrompt.getLibraryName();
    }

    private String getCurrentAncestorFileName() {
        return ancestorMemberPrompt.getFileName();
    }

    private String getCurrentAncestorMemberName() {
        return ancestorMemberPrompt.getMemberName();
    }

    public RSEMember getRightRSEMember() {
        return getRSEMember(rightConnection, rightLibrary, rightFile, rightMember);
    }

    public RSEMember getLeftRSEMember() {
        return getRSEMember(leftConnection, leftLibrary, leftFile, leftMember);
    }

    public RSEMember getAncestorRSEMember() {
        return getRSEMember(ancestorConnection, ancestorLibrary, ancestorFile, ancestorMember);
    }

    private RSEMember getRSEMember(IBMiConnection connection, String library, String file, String member) {

        try {
            return new RSEMember(connection.getMember(library, file, member, null));
        } catch (Exception e) {
            MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, e.getMessage());
            return null;
        }
    }

    @Override
    protected void switchLeftAndRightMember(Member leftMember, Member rightMember) {
        super.switchLeftAndRightMember(leftMember, rightMember);
        initializeRightMember((RSEMember)leftMember);
        initializeLeftMember((RSEMember)rightMember);
    }

    private IBMiConnection getLeftConnection() {
        return leftConnection;
    }

    private String getLeftLibrary() {
        if (leftLibrary == null) {
            return ""; //$NON-NLS-1$
        }
        return leftLibrary;
    }

    private String getLeftFile() {
        if (leftFile == null) {
            return ""; //$NON-NLS-1$
        }
        return leftFile;
    }

    private String getLeftMember() {
        if (leftMember == null) {
            return ""; //$NON-NLS-1$
        }
        return leftMember;
    }
}
