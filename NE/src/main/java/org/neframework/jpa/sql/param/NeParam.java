package org.neframework.jpa.sql.param;

import java.io.Serializable;

import org.neframework.jpa.util.ChkTools;

public class NeParam implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isNull;
	private Object value;

	public boolean getIsNull() {
		return isNull;
	}

	public void setIsNull(boolean isNull) {
		this.isNull = isNull;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		if (ChkTools.isNull(value)) {
			this.setIsNull(true);
		}
		this.value = value;
	}

}
