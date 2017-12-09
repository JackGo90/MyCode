package cn.itcast.erp.dao;

import java.util.List;

import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;
/**
 * 仓库库存数据访问接口
 * @author Administrator
 *
 */
public interface IStoredetailDao extends IBaseDao<Storedetail>{

	/**
	 * 库存预警信息列表
	 * @return
	 */
	List<Storealert> storealertList();
}
