/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.internal;

import biz.isphere.Messages;

public class DialogActionTypes {
	
	public static final int CREATE = 1;
	public static final int CHANGE = 2;
	public static final int COPY = 3;
	public static final int DELETE = 4;
	public static final int DISPLAY = 5;
	
	public static String getText(int actionType) {
		switch (actionType) {
			case CREATE: {
				return Messages.getString("CREATEX");
			}
			case CHANGE: {
				return Messages.getString("CHANGEX");
			}
			case COPY: {
				return Messages.getString("COPYX");
			}
			case DELETE: {
				return Messages.getString("DELETEX");
			}
			case DISPLAY: {
				return Messages.getString("DISPLAYX");
			}
		}
		return "";
	}
	
}
