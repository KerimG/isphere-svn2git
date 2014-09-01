package biz.isphere.core.rsemanagement.filter;

import biz.isphere.core.Messages;
import biz.isphere.core.rsemanagement.AbstractResource;

public class RSEFilter extends AbstractResource {

	public static final String TYPE_LIBRARY = "LIBRARY";
	public static final String TYPE_OBJECT = "OBJECT";
	public static final String TYPE_MEMBER = "MEMBER";
	public static final String TYPE_UNKNOWN = "UNKNOWN";

	private RSEFilterPool filterPool;
	private String name;
	private String type;
	private String[] filterStrings;
	private Object origin;

	public RSEFilter(boolean editable) {
		super(editable);
		this.filterPool = null;
		this.name = null;
		this.type = null;
		this.filterStrings = null;
		this.origin = null;
	}
	
	public RSEFilter(RSEFilterPool filterPool, String name, String type, String[] filterStrings, boolean editable, Object origin) {
		super(editable);
		this.filterPool = filterPool;
		this.name = name;
		this.type = type;
		this.filterStrings = filterStrings;
		this.origin = origin;
	}

	public RSEFilterPool getFilterPool() {
		return filterPool;
	}

	public void setFilterPool(RSEFilterPool filterPool) {
		this.filterPool = filterPool;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getFilterStrings() {
		return filterStrings;
	}

	public void setFilterStrings(String[] filterStrings) {
		this.filterStrings = filterStrings;
	}

	public Object getOrigin() {
		return origin;
	}

	public void setOrigin(Object origin) {
		this.origin = origin;
	}

	public String getDisplayFilterString() {
		StringBuffer buffer = new StringBuffer("");
		for (int idx = 0; idx < filterStrings.length; idx++) {
			if (idx > 0) {
				buffer.append("     ");
			}
			buffer.append(filterStrings[idx]);
		}
		return buffer.toString();
	}

	@Override
	public String getKey() {
		return name;
	}

	@Override
	public String getValue() {
		return type + ":" + getDisplayFilterString();
	}

	public static String getTypeText(String type) {
	    if (type.equals(TYPE_LIBRARY)) {
	        return Messages.Library;
	    }
	    else if (type.equals(TYPE_OBJECT)) {
	        return Messages.Object;
	    }
	    else if (type.equals(TYPE_MEMBER)) {
	        return Messages.Member;
	    }
	    else if (type.equals(TYPE_UNKNOWN)) {
	        return Messages.Unknown;
	    }
	    else {
	        return "*UNKNOWN";
	    }
	}
	
}
