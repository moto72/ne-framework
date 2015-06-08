package org.neframework.mvc.core;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neframework.jpa.page.Page;
import org.neframework.jpa.sql.OrmComponent;
import org.neframework.jpa.sql.param.NeParamList;
import org.neframework.mvc.plugin.Tip;

public class BaseController {

	protected HttpServletRequest req;
	protected HttpServletResponse resp;

	protected Logger logger = Logger.getLogger("ne framework");

	public final OrmComponent oc = new OrmComponent();

	public Page page = new Page();
	// 操作结果提示与说明(例如:操作成功)
	public Tip tip = new Tip();
	// 查询参数
	public NeParamList params = NeParamList.makeParams();
	// 排序字段
	public Map<String, String> sort_params = new HashMap<String, String>();
	// 返回结果
	public Map<String, Object> result = new HashMap<String, Object>();
	public String return_url = null;

	// ========= get / set ()=========================
	public void setTipMsg(String msg) {
		this.setTipMsg(msg, Tip.Type.information);
	}

	public void setTipMsg(String msg, Tip.Type type) {
		this.setTipMsg(true, msg, type);
	}

	public void setTipMsg(boolean b, String msg, Tip.Type type) {
		tip.setMsg(msg);
		tip.setType(type);

		result.put("success", b);
		result.put("tip", tip);
	}

	public void setReq(HttpServletRequest req) {
		this.req = req;
	}

	public void setResp(HttpServletResponse resp) {
		this.resp = resp;
	}

}
