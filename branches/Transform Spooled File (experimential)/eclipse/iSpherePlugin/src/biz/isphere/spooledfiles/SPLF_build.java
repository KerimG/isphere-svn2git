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

package biz.isphere.spooledfiles;

import com.ibm.as400.data.ProgramCallDocument;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;

public class SPLF_build {

	public int run(
			AS400 _as400) {

		int handle = 0;
		
		try {
			
			ProgramCallDocument pcml = 
				new ProgramCallDocument(
						_as400, 
						"biz.isphere.spooledfiles.SPLF_build", 
						this.getClass().getClassLoader());

			boolean rc = pcml.callProgram("SPLF_build");

			if (rc == false) {
				
				AS400Message[] msgs = pcml.getMessageList("SPLF_build");
				for (int idx = 0; idx < msgs.length; idx++) {
					System.out.println(msgs[idx].getID() + " - " + msgs[idx].getText());
				}
				System.out.println("*** Call to SPLF_build failed. See messages above ***");
				
				handle = -1;
				
			}
			else {
				
				handle = pcml.getIntValue("SPLF_build.handle");
				
			}
			
		}
		catch (PcmlException e) {
		
			handle = -1;
			
		//	System.out.println(e.getLocalizedMessage());    
		//	e.printStackTrace();
		//	System.out.println("*** Call to SPLF_build failed. ***");
		//	return null;
			
		}
		
		return handle;
		
	} 

}