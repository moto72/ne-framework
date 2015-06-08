package org.neframework.jpa.sql.param;

import java.util.ArrayList;
import java.util.List;

import org.neframework.jpa.util.ChkTools;

public class NeParamList {

	private List<NeParam> paramList = new ArrayList<NeParam>();
	// 最终值列表
	private List<Object> paramValueList = new ArrayList<Object>();

	public NeParamList add(String value) {
		NeParam p = new NeParam();
		p.setValue(value);
		paramList.add(p);

		// 非空,加入[值] list
		if (!p.getIsNull()) {
			paramValueList.add(value);
		}

		return this;
	}

	public NeParamList addLike(String paramValue) {
		if (ChkTools.isNotNull(paramValue)) {
			paramValue = "%" + paramValue + "%";
		}
		return add(paramValue);
	}

	// 获取最终值的参数
	public Object[] getParamValues() {
		return paramValueList.toArray();
	}// #getParamValues

	public List<NeParam> getParamList() {
		return paramList;
	}

	public static NeParamList makeParams() {
		NeParamList params = new NeParamList();
		return params;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (Object value : paramValueList) {
			sb.append(value).append(" ");
		}
		sb.append("]");
		return sb.toString();
	}

	public static void main(String[] args) {

	}

}
