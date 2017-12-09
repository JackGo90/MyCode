package cn.itcast.erp.action;
import java.util.List;

import cn.itcast.erp.action.util.WebUtil;
import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.exception.ErpException;

/**
 * 仓库库存Action 
 * @author Administrator
 *
 */
public class StoredetailAction extends BaseAction<Storedetail> {

	private IStoredetailBiz storedetailBiz;

	public void setStoredetailBiz(IStoredetailBiz storedetailBiz) {
		this.storedetailBiz = storedetailBiz;
		super.setBaseBiz(this.storedetailBiz);
	}
	
	/**
	 * 获取库存预警列表
	 */
	public void storealertList(){
		List<Storealert> list = storedetailBiz.storealertList();
		WebUtil.write(list);
	}
	
	/**
	 * 发送预警邮件
	 */
	public void sendStorealertMail(){
		try {
			storedetailBiz.sendStorealertMail();
			WebUtil.ajaxReturn(true, "发预警送邮件成功");
		} catch (ErpException e) {
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "发预警送邮件失败");
		}
	}

}
