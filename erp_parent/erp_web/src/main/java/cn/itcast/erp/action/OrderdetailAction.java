package cn.itcast.erp.action;
import org.apache.shiro.authz.UnauthorizedException;

import cn.itcast.erp.action.util.WebUtil;
import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.exception.ErpException;

/**
 * 订单明细Action 
 * @author Administrator
 *
 */
public class OrderdetailAction extends BaseAction<Orderdetail> {

	private IOrderdetailBiz orderdetailBiz;

	public void setOrderdetailBiz(IOrderdetailBiz orderdetailBiz) {
		this.orderdetailBiz = orderdetailBiz;
		super.setBaseBiz(this.orderdetailBiz);
	}
	
	private Long storeuuid;
	
	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}

	/**
	 * 入库
	 */
	public void doInStore(){
		//获取当前登陆用户
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "亲，您还没有登陆!");
			return;
		}
		
		try {
			orderdetailBiz.doInStore(getId(), storeuuid, loginUser.getUuid());
			WebUtil.ajaxReturn(true, "入库成功");
		} catch (ErpException e) {
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (UnauthorizedException e) {
			WebUtil.ajaxReturn(false,  "没有权限");
		}  catch (Exception e) {
			WebUtil.ajaxReturn(false, "入库失败");
			e.printStackTrace();
		}
	}
	
	/**
	 * 出库
	 */
	public void doOutStore(){
		//获取当前登陆用户
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "亲，您还没有登陆!");
			return;
		}
		
		try {
			orderdetailBiz.doOutStoreJava(getId(), storeuuid, loginUser.getUuid());
			WebUtil.ajaxReturn(true, "出库成功");
		} catch (ErpException e) {
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (UnauthorizedException e) {
			WebUtil.ajaxReturn(false,  "没有权限");
		}  catch (Exception e) {
			WebUtil.ajaxReturn(false, "出库失败");
			e.printStackTrace();
		}
	}
	
	/**
	 * 出库
	 */
	public void doOutStoreJava(){
		//获取当前登陆用户
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "亲，您还没有登陆!");
			return;
		}
		
		try {
			orderdetailBiz.doOutStoreJava(getId(), storeuuid, loginUser.getUuid());
			WebUtil.ajaxReturn(true, "出库成功");
		} catch (ErpException e) {
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (Exception e) {
			WebUtil.ajaxReturn(false, "出库失败");
			e.printStackTrace();
		}
	}

}
