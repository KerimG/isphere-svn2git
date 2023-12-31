/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.FieldDescription;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.file.description.RecordFormatDescription;
import biz.isphere.core.file.description.RecordFormatDescriptionsStore;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.memberrename.RenameMemberActor;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.sourcemembercopy.rse.CopyMemberService;
import biz.isphere.core.sourcemembercopy.rse.ExistingMemberAction;

public class CopyMemberValidator extends Thread {

    public static final int ERROR_NONE = -1;
    public static final int ERROR_TO_CONNECTION = 1;
    public static final int ERROR_TO_FILE = 2;
    public static final int ERROR_TO_LIBRARY = 3;
    public static final int ERROR_TO_MEMBER = 4;
    public static final int ERROR_CANCELED = 5;
    public static final int ERROR_EXCEPTION = 6;

    private DoValidateMembers doValidateMembers;
    private IValidateMembersPostRun postRun;

    private int errorItem;
    private String errorMessage;

    private boolean isActive;

    public CopyMemberValidator(CopyMemberService jobDescription, ExistingMemberAction existingMemberAction, boolean ignoreDataLostError,
        boolean ignoreUnsavedChangesError, boolean fullErrorCheck, IValidateMembersPostRun postRun) {
        doValidateMembers = new DoValidateMembers(jobDescription, existingMemberAction, ignoreDataLostError, ignoreUnsavedChangesError,
            fullErrorCheck);
        this.postRun = postRun;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void run() {

        startProcess();

        try {

            doValidateMembers.start();

            while (doValidateMembers.isAlive()) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

            }

        } finally {
            postRun.returnResult(errorItem, errorMessage);
            endProcess();
        }

    }

    public void cancel() {
        doValidateMembers.cancel();
    }

    private void startProcess() {
        isActive = true;
    }

    private void endProcess() {
        isActive = false;
    }

    private class DoValidateMembers extends Thread {

        private CopyMemberService jobDescription;
        private ExistingMemberAction existingMemberAction;
        private boolean ignoreDataLostError;
        private boolean ignoreUnsavedChangesError;
        private boolean fullErrorCheck;
        private boolean isCanceled;

        public DoValidateMembers(CopyMemberService jobDescription, ExistingMemberAction existingMemberAction, boolean ignoreDataLostError,
            boolean ignoreUnsavedChangesError, boolean fullErrorCheck) {
            this.jobDescription = jobDescription;
            this.existingMemberAction = existingMemberAction;
            this.ignoreDataLostError = ignoreDataLostError;
            this.ignoreUnsavedChangesError = ignoreUnsavedChangesError;
            this.fullErrorCheck = fullErrorCheck;
            this.isCanceled = false;
        }

        @Override
        public void run() {

            errorItem = ERROR_NONE;
            errorMessage = null;

            jobDescription.updateMembersWithTargetSourceFile();

            if (!isCanceled && errorItem == ERROR_NONE) {
                validateTargetFile(jobDescription.getToConnectionName(), jobDescription.getToLibrary(), jobDescription.getToFile());
            }

            if (!isCanceled && errorItem == ERROR_NONE) {
                validateMembers(jobDescription.getFromConnectionName(), jobDescription.getToConnectionName(), existingMemberAction,
                    ignoreDataLostError, ignoreUnsavedChangesError, fullErrorCheck);
            }

            if (isCanceled) {
                errorItem = ERROR_CANCELED;
                errorMessage = Messages.Operation_has_been_canceled_by_the_user;
            }
        }

        public void cancel() {
            isCanceled = true;
        }

        private boolean validateTargetFile(String toConnectionName, String toLibrary, String toFile) {

            if (CopyMemberService.TO_FILE_FROMFILE.equals(toFile)) {
                return true;
            }

            Validator nameValidator = Validator.getNameInstance(jobDescription.getToConnectionCcsid());

            boolean isError = false;

            if (!hasConnection(jobDescription.getToConnectionName())) {
                errorItem = ERROR_TO_CONNECTION;
                errorMessage = Messages.bind(Messages.Connection_A_not_found, jobDescription.getToConnectionName());
                isError = true;
            } else if (!nameValidator.validate(toLibrary)) {
                errorItem = ERROR_TO_LIBRARY;
                errorMessage = Messages.bind(Messages.Invalid_library_name, toLibrary);
                isError = true;
            } else {
                AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);
                if (!ISphereHelper.checkLibrary(toSystem, toLibrary)) {
                    errorItem = ERROR_TO_LIBRARY;
                    errorMessage = Messages.bind(Messages.Library_A_not_found, toLibrary);
                    isError = true;
                } else if (!nameValidator.validate(toFile)) {
                    errorItem = ERROR_TO_FILE;
                    errorMessage = Messages.bind(Messages.Invalid_file_name, toFile);
                    isError = true;
                } else if (!ISphereHelper.checkFile(toSystem, toLibrary, toFile)) {
                    errorItem = ERROR_TO_FILE;
                    errorMessage = Messages.bind(Messages.File_A_not_found, toFile);
                    isError = true;
                }
            }

            return !isError;
        }

        private boolean hasConnection(String connectionName) {

            Set<String> connectionNames = new HashSet<String>(Arrays.asList(IBMiHostContributionsHandler.getConnectionNames()));
            boolean hasConnection = connectionNames.contains(connectionName);

            return hasConnection;
        }

        private boolean validateMembers(String fromConnectionName, String toConnectionName, ExistingMemberAction existingMemberAction,
            boolean ignoreDataLostError, boolean ignoreUnsavedChangesError, boolean fullErrorCheck) {

            boolean isError = false;
            boolean isSeriousError = false;

            AS400 fromSystem = IBMiHostContributionsHandler.getSystem(fromConnectionName);
            AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);

            RecordFormatDescriptionsStore fromSourceFiles = new RecordFormatDescriptionsStore(fromSystem);
            RecordFormatDescriptionsStore toSourceFiles = new RecordFormatDescriptionsStore(toSystem);

            IMemberRenamingRule newNameRule = Preferences.getInstance().getMemberRenamingRule();
            RenameMemberActor actor = new RenameMemberActor(toSystem, newNameRule);

            Set<String> targetMembers = new HashSet<String>();

            try {

                Set<String> dirtyFiles;
                if (!ignoreUnsavedChangesError) {
                    dirtyFiles = getDirtyFiles();
                } else {
                    dirtyFiles = new HashSet<String>();
                }

                for (CopyMemberItem member : jobDescription.getItems()) {

                    if (isCanceled) {
                        break;
                    }

                    if (member.isCopied()) {
                        continue;
                    }

                    if (isSeriousError) {
                        member.setErrorMessage(Messages.Canceled_due_to_previous_error);
                        continue;
                    }

                    String libraryName = member.getFromLibrary();
                    String fileName = member.getFromFile();
                    String memberName = member.getFromMember();
                    String srcType = member.getFromSrcType();

                    IFile localResource = new IBMiHostContributionsHandler().getLocalResource(fromConnectionName, libraryName, fileName, memberName,
                        srcType);
                    String localResourcePath = localResource.getLocation().makeAbsolute().toOSString();
                    String from = member.getFromQSYSName();
                    String to = member.getToQSYSName();

                    if (dirtyFiles.contains(localResourcePath)) {
                        member.setErrorMessage(Messages.Member_is_open_in_editor_and_has_unsaved_changes);
                        isError = true;
                    } else if (from.equals(to) && fromConnectionName.equalsIgnoreCase(toConnectionName)) {
                        member.setErrorMessage(Messages.bind(Messages.Cannot_copy_A_to_the_same_name, from));
                        isError = true;
                    } else if (targetMembers.contains(to)) {
                        member.setErrorMessage(Messages.Can_not_copy_member_twice_to_same_target_member);
                        isError = true;
                    } else if (!ISphereHelper.checkMember(IBMiHostContributionsHandler.getSystem(fromConnectionName), member.getFromLibrary(),
                        member.getFromFile(), member.getFromMember())) {
                        member.setErrorMessage(Messages.bind(Messages.From_member_A_not_found, from));
                        isError = true;
                    }

                    if (!isError) {
                        if (fullErrorCheck) {
                            if (ISphereHelper.checkMember(IBMiHostContributionsHandler.getSystem(jobDescription.getToConnectionName()),
                                member.getToLibrary(), member.getToFile(), member.getToMember())) {

                                if (existingMemberAction.equals(ExistingMemberAction.REPLACE)) {
                                    // that is fine, go ahead
                                } else if (existingMemberAction.equals(ExistingMemberAction.RENAME)) {

                                    try {
                                        actor.produceNewMemberName(member.getToLibrary(), member.getToFile(), member.getToMember()); // $NON-NLS-1$
                                    } catch (NoMoreNamesAvailableException e) {
                                        member.setErrorMessage(Messages.Error_No_more_names_available_Delete_old_backups);
                                        isError = true;
                                    } catch (Exception e) {
                                        member.setErrorMessage(e.getLocalizedMessage());
                                        isError = true;
                                    }

                                } else {
                                    member.setErrorMessage(Messages.bind(Messages.Target_member_A_already_exists, to));
                                    isError = true;
                                }
                            }
                        }
                    }

                    if (!isError) {
                        if (!ignoreDataLostError) {

                            RecordFormatDescription fromRecordFormatDescription = fromSourceFiles.get(member.getFromFile(), member.getFromLibrary());
                            RecordFormatDescription toRecordFormatDescription = toSourceFiles.get(member.getToFile(), member.getToLibrary());

                            FieldDescription fromSrcDta = fromRecordFormatDescription.getFieldDescription("SRCDTA");
                            if (fromSrcDta == null) {
                                member.setErrorMessage(Messages.bind(Messages.Could_not_retrieve_field_description_of_field_C_of_file_B_A,
                                    new String[] { member.getFromFile(), member.getFromLibrary(), "SRCDTA" }));
                                isError = true;
                                isSeriousError = true;
                            } else {

                                FieldDescription toSrcDta = toRecordFormatDescription.getFieldDescription("SRCDTA");
                                if (toSrcDta == null) {
                                    member.setErrorMessage(Messages.bind(Messages.Could_not_retrieve_field_description_of_field_C_of_file_B_A,
                                        new String[] { member.getToFile(), member.getToLibrary(), "SRCDTA" }));
                                    isError = true;
                                    isSeriousError = true;
                                } else {

                                    if (fromSrcDta.getLength() > toSrcDta.getLength()) {
                                        member.setErrorMessage(Messages.Data_lost_error_From_source_line_is_longer_than_target_source_line);
                                        isError = true;
                                    }
                                }
                            }
                        }
                    }

                    if (!isError) {
                        member.setErrorMessage(Messages.EMPTY);
                    } else {
                        errorItem = ERROR_TO_MEMBER;
                        errorMessage = Messages.Validation_ended_with_errors_Request_canceled;
                    }

                    targetMembers.add(to);
                }

            } catch (Exception e) {
                errorItem = ERROR_EXCEPTION;
                errorMessage = Messages.Validation_ended_with_errors_Request_canceled;
                MessageDialogAsync.displayError(Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            }

            return !isError;
        }

        private Set<String> getDirtyFiles() throws Exception {

            Set<String> openFiles = new HashSet<String>();

            try {

                IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
                for (IWorkbenchWindow window : windows) {
                    IWorkbenchPage[] pages = window.getPages();
                    for (IWorkbenchPage page : pages) {
                        IEditorReference[] editors = page.getEditorReferences();
                        for (IEditorReference editorReference : editors) {
                            if (editorReference.isDirty()) {
                                IEditorInput input = editorReference.getEditorInput();
                                if (input instanceof FileEditorInput) {
                                    FileEditorInput fileInput = (FileEditorInput)input;
                                    openFiles.add(fileInput.getFile().getLocation().makeAbsolute().toOSString());
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                String message = "*** Failed retrieving list of open editors ***";
                ISpherePlugin.logError(message, e); // $NON-NLS-1$
                throw new Exception(message);
            }

            return openFiles;
        }
    }
};
