package cn.itcast.erp.action;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;
import com.redsun.bos.ws.Waybilldetail;
import com.redsun.bos.ws.impl.IWaybillWs;

import cn.itcast.erp.action.util.WebUtil;
import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.exception.ErpException;

/**
 * 订单Action 
 * @author Administrator
 *
 */
public class OrdersAction extends BaseAction<Orders> {

	private IOrdersBiz ordersBiz;
	private IWaybillWs waybillWs;
	
	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}

	private String jsonData;

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public void setOrdersBiz(IOrdersBiz ordersBiz) {
		this.ordersBiz = ordersBiz;
		super.setBaseBiz(this.ordersBiz);
	}
	
	/**
	 * 新增订单
	 */
	@Override
	public void add() {
		Orders orders = getT();
		//获取当前登陆用户
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "亲，您还没有登陆!");
			return;
		}
		try {
			orders.setCreater(loginUser.getUuid());
			List<Orderdetail> orderdetails = JSON.parseArray(jsonData,Orderdetail.class);
			orders.setOrderdetails(orderdetails);
			ordersBiz.add(orders);
			WebUtil.ajaxReturn(true, "新增订单成功");
		} catch (ErpException e) {
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "新增订单失败");
		}
	}
	
	/**
	 * 审核
	 */
	public void doCheck(){
		//获取当前登陆用户
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "亲，您还没有登陆!");
			return;
		}
		try {
			ordersBiz.doCheck(getId(),loginUser.getUuid());
			WebUtil.ajaxReturn(true, "审核成功");
		} catch (ErpException e) {
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (UnauthorizedException e) {
			WebUtil.ajaxReturn(false, "没有权限");
		} catch (Exception e) {
			WebUtil.ajaxReturn(false, "审核失败");
			e.printStackTrace();
		}
	}
	
	/**
	 * 确认
	 */
	public void doStart(){
		//获取当前登陆用户
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "亲，您还没有登陆!");
			return;
		}
		try {
			ordersBiz.doStart(getId(),loginUser.getUuid());
			WebUtil.ajaxReturn(true, "确认成功");
		} catch (ErpException e) {
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (UnauthorizedException e) {
			WebUtil.ajaxReturn(false,  "没有权限");
		} catch (Exception e) {
			WebUtil.ajaxReturn(false, "确认失败");
			e.printStackTrace();
		}
	}
	
	/**
	 * 我的订单
	 */
	public void myListByPage() {
		//获取当前登陆用户
		Emp loginUser = WebUtil.getLoginUser();
		if(null != loginUser){
			//获取查询条件
			if(null == getT1()){
				setT1(new Orders());
			}
			//设置查询条件，订单的创建为登陆用户
			getT1().setCreater(loginUser.getUuid());
			super.listByPage();
		}
	}
	
	/**
	 * 导出订单信息
	 */
	public void exportById(){
		String filename = "orders_" + getId() + ".xls";
		HttpServletResponse res = ServletActionContext.getResponse();
		res.setHeader("Content-Disposition", "attachment;filename="+filename);
		try {
			ordersBiz.exportById(getId(), res.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导出订单信息
	 */
	public void ex(){
		String filename = "orders_" + getId() + ".xls";
		HttpServletResponse res = ServletActionContext.getResponse();
		res.setHeader("Content-Disposition", "attachment;filename="+filename);
		try {
			ordersBiz.exportByIdWithTemplate(getId(), res.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Long waybillsn;//运单号
	
	public void setWaybillsn(Long waybillsn) {
		this.waybillsn = waybillsn;
	}

	/**
	 * 获取运单详情
	 */
	public void waybilldetailList(){
		List<Waybilldetail> waybilldetailList = waybillWs.waybilldetailList(waybillsn);
		WebUtil.write(waybilldetailList);
	}

}
