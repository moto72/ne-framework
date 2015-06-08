package org.neframework.jpa.page;

/**
 * 公共page类
 * 
 * @author 冯晓东
 * 
 * @date : Dec 20, 2010 12:13:57 PM
 */
public class Page {
	// 翻页有关的变量。
	public int pageSize = 10; // 每一页的记录条数。
	public int pageNum = 1; // 当前页号。
	public int pageTotal = 0; // 总页数。
	public long rowTotal = 0; // 总记录数
	public int beginResult = 0;
	public long time = 0; // 查询时间
	public int endResult = 0;

	// ============== get / set () =====================

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}

	public long getRowTotal() {
		return rowTotal;
	}

	public void setRowTotal(long rowTotal) {
		this.rowTotal = rowTotal;
	}

	public int getBeginResult() {
		return beginResult;
	}

	public void setBeginResult(int beginResult) {
		this.beginResult = beginResult;
	}

	public int getEndResult() {
		return endResult;
	}

	public void setEndResult(int endResult) {
		this.endResult = endResult;
	}

	public long getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

}
