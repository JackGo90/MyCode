package cn.itcast.erp.biz;
import java.util.List;

import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;
/**
 * 仓库库存业务逻辑层接口
 * @author Administrator
 *
 */
public interface IStoredetailBiz extends IBaseBiz<Storedetail>{

	/**
	 * 库存预警信息列表
	 * @return
	 */
	List<Storealert> storealertList();
	
	/**
	 * 发送库存预警邮件
	 */
	void sendStorealertMail() throws Exception;
}

