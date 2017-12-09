package cn.itcast.erp.dao;

import java.util.Map;

import cn.itcast.erp.entity.Orderdetail;
/**
 * 订单明细数据访问接口
 * @author Administrator
 *
 */
public interface IOrderdetailDao extends IBaseDao<Orderdetail>{

	/**
	 * 出库
	 * @param uuid
	 * @param storeuuid
	 * @param empuuid
	 * @return
	 */
	Map<String,Object> doOutStore(Long uuid, Long storeuuid,Long empuuid);
}
