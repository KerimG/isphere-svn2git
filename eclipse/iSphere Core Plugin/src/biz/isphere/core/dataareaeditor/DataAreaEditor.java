/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataareaeditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.EditorPart;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.dataareaeditor.delegates.AbstractDataAreaEditorDelegate;
import biz.isphere.core.dataareaeditor.delegates.CharacterDataAreaEditorDelegate;
import biz.isphere.core.dataareaeditor.delegates.DecimalDataAreaEditorDelegate;
import biz.isphere.core.dataareaeditor.delegates.LogicalDataAreaEditorDelegate;
import biz.isphere.core.dataareaeditor.delegates.UnsupportedDataAreaEditorDelegate;

import com.ibm.as400.access.AS400;

public class DataAreaEditor extends EditorPart implements IFindReplaceTarget {

    public static final String ID = "biz.isphere.core.dataareaeditor.DataAreaEditor";

    private boolean isDirty;
    WrappedDataArea wrappedDataArea;
    AbstractDataAreaEditorDelegate editorDelegate;

    public DataAreaEditor() {
        isDirty = false;
    }

    @Override
    public void createPartControl(Composite aParent) {

        Composite editorParent = new Composite(aParent, SWT.NONE);
        GridLayout editorParentLayout = new GridLayout(2, false);
        editorParentLayout.marginTop = 5;
        editorParentLayout.marginWidth = 10;
        editorParent.setLayout(editorParentLayout);

        Label headline = new Label(editorParent, SWT.NONE);
        GridData headlineLayoutData = new GridData();
        headlineLayoutData.horizontalAlignment = GridData.BEGINNING;
        headlineLayoutData.horizontalSpan = 2;
        headline.setLayoutData(headlineLayoutData);
        headline.setText(getHeadlineText());

        aParent.getClientArea();

        editorDelegate = createEditorDelegate();
        editorDelegate.createPartControl(editorParent);
        editorDelegate.setStatusBar(new StatusBar(editorParent));

        registerDelegateActions();
    }

    @Override
    public void doSave(IProgressMonitor aMonitor) {

        editorDelegate.doSave(aMonitor);
    }

    @Override
    public void doSaveAs() {
        // not supported
    }

    @Override
    public void init(IEditorSite aSite, IEditorInput anInput) throws PartInitException {
        setSite(aSite);
        setInput(anInput);
        setPartName(anInput.getName());
        setTitleImage(((DataAreaEditorInput)anInput).getTitleImage());

        DataAreaEditorInput input = (DataAreaEditorInput)anInput;
        wrappedDataArea = new WrappedDataArea(input.getAS400(), input.getObjectLibrary(), input.getObjectName());
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void setFocus() {
        editorDelegate.setInitialFocus();
    }

    public WrappedDataArea getWrappedDataArea() {
        return wrappedDataArea;
    }

    public void setDirty(boolean anIsDirty) {
        isDirty = anIsDirty;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    private AbstractDataAreaEditorDelegate createEditorDelegate() {
        if (WrappedDataArea.CHARACTER.equals(getWrappedDataArea().getType())) {
            return new CharacterDataAreaEditorDelegate(this);
        } else if (WrappedDataArea.DECIMAL.equals(getWrappedDataArea().getType())) {
            return new DecimalDataAreaEditorDelegate(this);
        } else if (WrappedDataArea.LOGICAL.equals(getWrappedDataArea().getType())) {
            return new LogicalDataAreaEditorDelegate(this);
        } 

        return new UnsupportedDataAreaEditorDelegate(this);
    }

    private String getHeadlineText() {
        String text = wrappedDataArea.getText();
        if (StringHelper.isNullOrEmpty(text)) {
            text = ":";
        }
        if (!text.endsWith(":")) {
            text = text + ":";
        }
        return text;
    }

    public void registerDelegateActions() {

        final Action findReplaceAction = editorDelegate.getFindReplaceAction(this);
        if (findReplaceAction != null) {
            IHandlerService handlerService = (IHandlerService)getEditorSite().getService(IHandlerService.class);
            IHandler handler = new AbstractHandler() {
                public Object execute(ExecutionEvent event) throws ExecutionException {
                    findReplaceAction.run();
                    return null;
                }
            };
            handlerService.activateHandler("org.eclipse.ui.edit.findReplace", handler);
        }

        if (editorDelegate.getCutAction() != null) {
            IActionBars actionBars = getEditorSite().getActionBars();
            actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), editorDelegate.getCutAction());
        }

        if (editorDelegate.getPasteAction() != null) {
            IActionBars actionBars = getEditorSite().getActionBars();
            actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), editorDelegate.getPasteAction());
        }
    }

    public static void openEditor(AS400 anAS400, String aConnection, String aLibrary, String aDataArea, String aMode) {

        try {

            DataAreaEditorInput editorInput = new DataAreaEditorInput(anAS400, aConnection, aLibrary, aDataArea, aMode);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, DataAreaEditor.ID);

        } catch (PartInitException e) {
        }

    }

    @Override
    public void dispose() {
        editorDelegate.dispose();
        super.dispose();
    }

    public boolean canPerformFind() {
        return editorDelegate.canPerformFind();
    }

    public int findAndSelect(int aWidgetOffset, String aFindString, boolean aSearchForward, boolean aCaseSensitive, boolean aWholeWord) {
        return editorDelegate.findAndSelect(aWidgetOffset, aFindString, aSearchForward, aCaseSensitive, aWholeWord);
    }

    public Point getSelection() {
        return editorDelegate.getSelection();
    }

    public String getSelectionText() {
        return editorDelegate.getSelectionText();
    }

    public boolean isEditable() {
        return editorDelegate.isEditable();
    }

    public void replaceSelection(String aText) {
        editorDelegate.replaceSelection(aText);
    }

}
