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

package biz.isphere.messagefilesearch;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import biz.isphere.ISpherePlugin;


public class SearchElement {

	private String library;
	private String messageFile;
	private String description;
	private Object data;

	public SearchElement() {
		library = "";
		messageFile = "";
		description = "";
		data = null;
	}

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getMessageFile() {
		return messageFile;
	}

	public void setMessageFile(String messageFile) {
		this.messageFile = messageFile;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public static void setSearchElements(Connection jdbcConnection, int handle, ArrayList<SearchElement> _searchElements) {
		
		String _separator;
		try {
			_separator = jdbcConnection.getMetaData().getCatalogSeparator();
		} 
		catch (SQLException e) {
			_separator = ".";
			e.printStackTrace();
		}
		
		if (_searchElements.size() > 0) {
			
			int _start;
			int _end;
			int _elements = 500;
			
			_start = 1;
			
			do {
				
				_end = _start + _elements - 1;
				
				if (_end > _searchElements.size()) {
					_end = _searchElements.size();
				}

				StringBuffer sqlInsert = new StringBuffer();
				sqlInsert.append("INSERT INTO " + ISpherePlugin.getISphereLibrary() + _separator + "XFNDSTRI (XIHDL, XILIB, XIMSGF) VALUES");
				boolean first = true;
				
				for (int idx = _start - 1; idx <= _end - 1; idx++) {
				
					if (first) {
						first = false;
						sqlInsert.append(" ");
					}
					else {
						sqlInsert.append(", ");
					}
					
					sqlInsert.append(
							"('" + Integer.toString(handle) + "', " +
							"'" + _searchElements.get(idx).getLibrary() + "', " +
							"'" + _searchElements.get(idx).getMessageFile() + "')");
					
				}
				
				String _sqlInsert = sqlInsert.toString();
					
				Statement statementInsert = null;
				
				try {
					statementInsert = jdbcConnection.createStatement();
					statementInsert.executeUpdate(_sqlInsert);
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
				
				if (statementInsert != null) {
					try {
						statementInsert.close();
					} 
					catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
				_start = _start + _elements;
				
			}
			while (_end < _searchElements.size());
			
		}
		
	}
	
}
