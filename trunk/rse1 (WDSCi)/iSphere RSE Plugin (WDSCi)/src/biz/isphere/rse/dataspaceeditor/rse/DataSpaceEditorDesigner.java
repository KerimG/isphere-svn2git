/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.dataspaceeditor.rse;

import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspaceeditor.rse.AbstractDataSpaceEditorDesigner;
import biz.isphere.core.dataspaceeditor.rse.AbstractDropDataObjectListerner;
import biz.isphere.core.dataspaceeditor.rse.IDropObjectListener;
import biz.isphere.core.dataspaceeditor.rse.RemoteObject;
import biz.isphere.rse.dataareaeditor.WrappedDataSpace;

public class DataSpaceEditorDesigner extends AbstractDataSpaceEditorDesigner {

    @Override
    protected AbstractDropDataObjectListerner createDropListener(IDropObjectListener editor) {
        return new DropDataObjectListener(editor);
    }

    @Override
    protected AbstractWrappedDataSpace createDataSpaceWrapper(RemoteObject remoteObject) throws Exception {
        return new WrappedDataSpace(remoteObject);
    }
}
