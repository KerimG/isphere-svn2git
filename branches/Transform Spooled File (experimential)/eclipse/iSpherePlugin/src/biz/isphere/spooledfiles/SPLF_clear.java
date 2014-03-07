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

public class SPLF_clear {

	public int run(
			AS400 _as400,
			int handle) {

		int errno = 0;
		
		try {
			
			ProgramCallDocument pcml = 
				new ProgramCallDocument(
						_as400, 
						"biz.isphere.spooledfiles.SPLF_clear", 
						this.getClass().getClassLoader());

			pcml.setIntValue("SPLF_clear.handle", handle);
			
			boolean rc = pcml.callProgram("SPLF_clear");

			if (rc == false) {
				
				AS400Message[] msgs = pcml.getMessageList("SPLF_clear");
				for (int idx = 0; idx < msgs.length; idx++) {
					System.out.println(msgs[idx].getID() + " - " + msgs[idx].getText());
				}
				System.out.println("*** Call to SPLF_clear failed. See messages above ***");
				
				errno = -1;
				
			}
			else {
				
				errno = 1;
				
			}
			
		}
		catch (PcmlException e) {
		
			errno = -1;
			
		//	System.out.println(e.getLocalizedMessage());    
		//	e.printStackTrace();
		//	System.out.println("*** Call to SPLF_clear failed. ***");
		//	return null;
			
		}
		
		return errno;
		
	} 

}