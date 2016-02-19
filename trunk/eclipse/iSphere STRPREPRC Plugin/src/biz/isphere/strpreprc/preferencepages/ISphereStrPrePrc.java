/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.preferencepages;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.strpreprc.Messages;
import biz.isphere.strpreprc.model.HeaderTemplates;
import biz.isphere.strpreprc.preferences.Preferences;

public class ISphereStrPrePrc extends PreferencePage implements IWorkbenchPreferencePage {

    private Button checkboxUseParameterSections;
    private Button checkboxPriorizeImportantSection;
    private Button checkboxUseTemplateFolder;
    private Text textTemplateFolder;
    private Button buttonSelectTemplateFolder;
    private Button buttonExportTemplates;

    public ISphereStrPrePrc() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        createSectionTemplateDirectory(container);

        setScreenToValues();

        return container;
    }

    private void createSectionTemplateDirectory(Composite parent) {

        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(2, false));
        main.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label labelUseParameterSections = new Label(main, SWT.NONE);
        labelUseParameterSections.setLayoutData(createLabelLayoutData());
        labelUseParameterSections.setText("Use parameter sections:"); // TODO:
        labelUseParameterSections.setToolTipText("Specifies whether to use 'important', 'compile' and 'link' parameter sections."); // TODO:

        checkboxUseParameterSections = WidgetFactory.createCheckbox(main);
        checkboxUseParameterSections.setToolTipText("Specifies whether to use 'important', 'compile' and 'link' parameter sections."); // TODO:
        checkboxUseParameterSections.setLayoutData(createTextLayoutData());
        checkboxUseParameterSections.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                if (validateUseParameterSections()) {
                    validateAll();
                }
                setControlsEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Label labelPriorizeImportantSection = new Label(main, SWT.NONE);
        labelPriorizeImportantSection.setLayoutData(createLabelLayoutData());
        labelPriorizeImportantSection.setText("Priorize important section:"); // TODO:
        labelPriorizeImportantSection.setToolTipText("Specifies whether to priorize the 'important' over the 'compile' section."); // TODO:

        checkboxPriorizeImportantSection = WidgetFactory.createCheckbox(main);
        checkboxPriorizeImportantSection.setToolTipText("Specifies whether to priorize the 'important' over the 'compile' section."); // TODO:
        checkboxPriorizeImportantSection.setLayoutData(createTextLayoutData());
        checkboxPriorizeImportantSection.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                if (validatePriorizeImportantSection()) {
                    validateAll();
                }
                setControlsEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Group groupTemplates = new Group(main, SWT.NONE);
        groupTemplates.setLayout(new GridLayout(3, false));
        groupTemplates.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        groupTemplates.setText("Templates"); // TODO: NLS

        Label labelUseTemplateFolder = new Label(groupTemplates, SWT.NONE);
        labelUseTemplateFolder.setLayoutData(createLabelLayoutData());
        labelUseTemplateFolder.setText("Use template directory:"); // TODO:
                                                                   // NLS
        labelUseTemplateFolder.setToolTipText("Specifies whether to use the templates stored in the template directory."); // TODO:

        checkboxUseTemplateFolder = WidgetFactory.createCheckbox(groupTemplates);
        checkboxUseTemplateFolder.setToolTipText("Specifies whether to use the templates stored in the template directory."); // TODO:
        checkboxUseTemplateFolder.setLayoutData(createTextLayoutData(2));
        checkboxUseTemplateFolder.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                if (validateUseTemplateDirectory()) {
                    validateAll();
                }
                setControlsEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Label labelTemplatesDirectory = new Label(groupTemplates, SWT.NONE);
        labelTemplatesDirectory.setLayoutData(createLabelLayoutData());
        labelTemplatesDirectory.setText("Templates directory:"); // TODO:
        labelTemplatesDirectory.setToolTipText("Directory that contains the STRPREPRC template files."); // TODO:

        textTemplateFolder = WidgetFactory.createText(groupTemplates);
        textTemplateFolder.setToolTipText("Directory that contains the STRPREPRC template files."); // TODO:
        GridData sourceFileSearchSaveDirectoryLayoutData = createTextLayoutData();
        sourceFileSearchSaveDirectoryLayoutData.widthHint = 200;
        textTemplateFolder.setLayoutData(sourceFileSearchSaveDirectoryLayoutData);
        textTemplateFolder.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateTemplateDirectory()) {
                    validateAll();
                }
                setControlsEnablement();
            }
        });

        buttonSelectTemplateFolder = WidgetFactory.createPushButton(groupTemplates, Messages.Browse + "..."); //$NON-NLS-1$
        buttonSelectTemplateFolder.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {

                DirectoryDialog dialog = new DirectoryDialog(getShell());
                dialog.setFilterPath(getFilterPath());
                String directory = dialog.open();
                if (directory != null) {
                    textTemplateFolder.setText(directory);
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            private String getFilterPath() {
                if (!StringHelper.isNullOrEmpty(textTemplateFolder.getText())) {
                    File directory = new File(textTemplateFolder.getText());
                    if (directory.exists()) {
                        if (directory.isDirectory()) {
                            return directory.getAbsolutePath();
                        } else {
                            return directory.getParentFile().getAbsolutePath();
                        }
                    }
                }
                return Preferences.getInstance().getInitialTemplateDirectory();
            }
        });

        buttonExportTemplates = WidgetFactory.createPushButton(groupTemplates, "Export"); //$NON-NLS-1$
        buttonExportTemplates.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                String errorMessage = HeaderTemplates.getInstance().save(textTemplateFolder.getText());
                if (!StringHelper.isNullOrEmpty(errorMessage)) {
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, errorMessage);
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        buttonExportTemplates.setToolTipText("Saves all templates to the specified templates directory. Existing templates are preserved."); // TODO:
    }

    @Override
    protected void performApply() {
        reloadTemplates();
        setStoreToValues();
        super.performApply();
    }

    @Override
    protected void performDefaults() {
        setScreenToDefaultValues();
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        reloadTemplates();
        setStoreToValues();
        return super.performOk();
    }

    protected void setStoreToValues() {

        Preferences preferences = Preferences.getInstance();

        preferences.setUseParameterSections(checkboxUseParameterSections.getSelection());
        preferences.setPriorizeImportantSection(checkboxPriorizeImportantSection.getSelection());

        preferences.setTemplateDirectory(textTemplateFolder.getText());
        preferences.setUseTemplateDirectory(checkboxUseTemplateFolder.getSelection());
    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = Preferences.getInstance();

        checkboxUseParameterSections.setSelection(preferences.useParameterSections());
        checkboxPriorizeImportantSection.setSelection(preferences.priorizeImportantSection());

        textTemplateFolder.setText(preferences.getTemplateDirectory());
        checkboxUseTemplateFolder.setSelection(preferences.useTemplateDirectory());

        validateAll();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = Preferences.getInstance();

        checkboxUseParameterSections.setSelection(preferences.getInitialPriorizeImportantSection());
        checkboxPriorizeImportantSection.setSelection(preferences.getInitialPriorizeImportantSection());

        textTemplateFolder.setText(preferences.getInitialTemplateDirectory());
        checkboxUseTemplateFolder.setSelection(preferences.getInitialUseTemplateDirectory());

        validateAll();
        setControlsEnablement();
    }

    private void reloadTemplates() {

        Preferences preferences = Preferences.getInstance();

        boolean isDirty = false;

        if (checkboxUseTemplateFolder.getSelection() != preferences.useTemplateDirectory()) {
            isDirty = true;
        }

        if (!textTemplateFolder.getText().equalsIgnoreCase(preferences.getTemplateDirectory())) {
            isDirty = true;
        }

        if (isDirty) {
            HeaderTemplates.getInstance().clearTemplatesCache();
        }
    }

    private boolean validateUseParameterSections() {

        return true;
    }

    private boolean validatePriorizeImportantSection() {

        return true;
    }

    private boolean validateUseTemplateDirectory() {

        return true;
    }

    private boolean validateTemplateDirectory() {

        return validateDirectory(textTemplateFolder, !checkboxUseTemplateFolder.getSelection());
    }

    private boolean validateDirectory(Text textDirectory, boolean emptyAllowed) {

        String path = textDirectory.getText();
        if (StringHelper.isNullOrEmpty(path)) {
            if (!emptyAllowed) {
                setError(Messages.Directory_must_not_be_empty);
                return false;
            } else {
                return true;
            }
        }

        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }

        File directory = new File(path);
        if (!directory.exists()) {
            setError(Messages.The_specified_directory_does_not_exist);
            return false;
        }

        if (!directory.isDirectory()) {
            setError(Messages.The_specified_directory_does_not_exist);
            return false;
        }

        return clearError();
    }

    private boolean validateAll() {

        if (!validateUseParameterSections()) {
            return false;
        }

        if (!validatePriorizeImportantSection()) {
            return false;
        }

        if (!validateUseTemplateDirectory()) {
            return false;
        }

        if (!validateTemplateDirectory()) {
            return false;
        }

        return clearError();
    }

    private void setControlsEnablement() {

        if (checkboxUseParameterSections.getSelection()) {
            checkboxPriorizeImportantSection.setEnabled(true);
        } else {
            checkboxPriorizeImportantSection.setEnabled(false);
        }

        if (checkboxUseTemplateFolder.getSelection()) {
            textTemplateFolder.setEnabled(true);
            buttonSelectTemplateFolder.setEnabled(true);
        } else {
            textTemplateFolder.setEnabled(false);
            buttonSelectTemplateFolder.setEnabled(false);
        }

        if (isValid() && textTemplateFolder.isEnabled()) {
            buttonExportTemplates.setEnabled(true);
        } else {
            buttonExportTemplates.setEnabled(false);
        }
    }

    private boolean setError(String message) {
        setErrorMessage(message);
        setValid(false);
        return false;
    }

    private boolean clearError() {
        setErrorMessage(null);
        setValid(true);
        return true;
    }

    private GridData createLabelLayoutData() {
        return new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    }

    private GridData createTextLayoutData() {
        return createTextLayoutData(1);
    }

    private GridData createTextLayoutData(int horizontalSpan) {
        return new GridData(SWT.FILL, SWT.CENTER, true, false, horizontalSpan, 1);
    }

}
