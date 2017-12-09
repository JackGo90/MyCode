package cn.itcast.erp.biz.impl;
import java.util.List;

import cn.itcast.erp.biz.IStoreoperBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.entity.Storeoper;
/**
 * 仓库操作记录业务逻辑类
 * @author Administrator
 *
 */
public class StoreoperBiz extends BaseBiz<Storeoper> implements IStoreoperBiz {

	private IStoreoperDao storeoperDao;
	private IEmpDao empDao;
	private IStoreDao storeDao;
	private IGoodsDao goodsDao;
	
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}

	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
		super.setBaseDao(this.storeoperDao);
	}
	
	@Override
	public List<Storeoper> getListByPage(Storeoper t1, Storeoper t2, Object param, int firstResult, int maxResults) {
		List<Storeoper> list = super.getListByPage(t1, t2, param,firstResult,maxResults);
		for(Storeoper so : list){
			so.setEmpName(empDao.get(so.getEmpuuid()).getName());
			so.setStoreName(storeDao.get(so.getStoreuuid()).getName());
			so.setGoodsName(goodsDao.get(so.getGoodsuuid()).getName());
		}
		return list;
	}
	
}
