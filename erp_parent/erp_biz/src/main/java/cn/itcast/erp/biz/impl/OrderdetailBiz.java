package cn.itcast.erp.biz.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.redsun.bos.ws.impl.IWaybillWs;

import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.dao.IOrderdetailDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.entity.Storeoper;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.exception.ErpException;
/**
 * 订单明细业务逻辑类
 * @author Administrator
 *
 */
public class OrderdetailBiz extends BaseBiz<Orderdetail> implements IOrderdetailBiz {

	private IOrderdetailDao orderdetailDao;
	private IStoredetailDao storedetailDao;
	private IStoreoperDao storeoperDao;
	private IWaybillWs waybillWs;
	private ISupplierDao supplierDao;
	
	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}

	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}

	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
	}

	public void setOrderdetailDao(IOrderdetailDao orderdetailDao) {
		this.orderdetailDao = orderdetailDao;
		super.setBaseDao(this.orderdetailDao);
	}

	@Override
	@RequiresPermissions("采购入库")
	public void doInStore(Long uuid, Long storeuuid, Long empuuid) {
		//入库的操作 前端需要传2个条件：明细的编号，仓库的编号
		//***********************1. 明细表的操作***********************
		Orderdetail orderdetail = orderdetailDao.get(uuid);//明细进入持久化状态
		
		// 判断状态
		if(!Orderdetail.STATE_NOT_IN.equals(orderdetail.getState())){
			throw new ErpException("该明细已经入库了，不能重复入库!");
		}
		//	1.1 结束日期=系统日期
		orderdetail.setEndtime(new Date());
		//	1.2 库管员 = 当前登陆用户的编号
		orderdetail.setEnder(empuuid);
		//	1.3 设置仓库编号 = 前端 传过来
		orderdetail.setStoreuuid(storeuuid);
		//	1.3 状态=已入库 1
		orderdetail.setState(Orderdetail.STATE_IN);
		
		//***********************2. 商品仓库库存 操作***********************
		//	2.1 判断是否存在库存信息, 条件：商品编号(明细记录里有)与仓库编号(前端 传过来)
		//		查询库存表select * from storedetail where goodsuuid=? and storeuuid=?
		//	构建查询条件
		Storedetail storedetail = new Storedetail();
		// 设置查询条件：商品编号(明细记录里有)
		storedetail.setGoodsuuid(orderdetail.getGoodsuuid());
		// 设置查询条件：仓库编号(前端 传过来)
		storedetail.setStoreuuid(storeuuid);
		// 根据条件进行查询
		List<Storedetail> storedetailList = storedetailDao.getList(storedetail, null, null);
		if(storedetailList.size() > 0){
		//	2.2 如果存在库存信息，数量累加, storedetail进入持久化状态
			storedetail = storedetailList.get(0);
			//数量累加
			storedetail.setNum(storedetail.getNum() + orderdetail.getNum());
		}else{
		//	2.3 如果不存在，插入新的库存信息（商品编号(明细记录里有)， 仓库编号(前端 传过来)， 数量(明细记录里有)）
			storedetail.setNum(orderdetail.getNum());
			storedetailDao.add(storedetail);
		}
		//***********************3. 商品库存变更记录 操作***********************
		//	插入一条日志记录(
		//		操作员工编号=当前登陆用户的编号
		//		操作日期=系统日期
		//		仓库编号=前端 传过来
		//		商品编号=明细记录里有
		//		数量=明细记录里有
		//		操作类型=入库(1)
		//	)
		Storeoper log = new Storeoper();
		//		操作员工编号=当前登陆用户的编号
		log.setEmpuuid(empuuid);
		//		操作日期=系统日期 
		log.setOpertime(orderdetail.getEndtime());
		//		仓库编号=前端 传过来
		log.setStoreuuid(storeuuid);
		//		商品编号=明细记录里有
		log.setGoodsuuid(orderdetail.getGoodsuuid());
		//		数量=明细记录里有
		log.setNum(orderdetail.getNum());
		//		操作类型=入库(1)
		log.setType(Storeoper.TYPE_IN);
		storeoperDao.add(log);
		
		//***********************4. 订单表 操作***********************

		//明细对应的订单, 进入持久化状态
		Orders orders = orderdetail.getOrders();
		
		//	4.1 条件：所有的明细都已经完成入库？
		//		根据订单编号查看是否存在未入库的明细
		//		select count(1) from orderdetail where state='0' and ordersuuid=订单编号(明细记录里有)
		//  构建查询条件
		Orderdetail queryParam = new Orderdetail();
		// 未入库的明细
		queryParam.setState(Orderdetail.STATE_NOT_IN);
		// 订单
		queryParam.setOrders(orders);
		// 查询
		long count = orderdetailDao.getCount(queryParam, null, null);
		//	4.2 如果所有的明细都已经完成入库
		if(count == 0){
		//		4.2.1. 入库日期=系统日期
			orders.setEndtime(orderdetail.getEndtime());
		//		4.2.2. 库管员=当前登陆用户的编号
			orders.setEnder(empuuid);
		//		4.2.3. 订单状态=已入库(3)
			orders.setState(Orders.STATE_END);
		}		
	}

	@Override
	public void doOutStore(Long uuid, Long storeuuid, Long empuuid) {
		Map<String, Object> rtnMap = orderdetailDao.doOutStore(uuid, storeuuid, empuuid);
		int outFlag = (int)rtnMap.get("outFlag");
		if(outFlag > 0){
			throw new ErpException((String)rtnMap.get("outMessage"));
		}
	}

	@Override
	@RequiresPermissions("销售订单出库")
	public void doOutStoreJava(Long uuid, Long storeuuid, Long empuuid) {
		//出库的操作 前端需要传2个条件：明细的编号，仓库的编号
		//***********************1. 明细表的操作***********************
		Orderdetail orderdetail = orderdetailDao.get(uuid);//明细进出持久化状态
		
		// 判断状态
		if(!Orderdetail.STATE_NOT_OUT.equals(orderdetail.getState())){
			throw new ErpException("该明细已经出库了，不能重复出库!");
		}
		//	1.1 结束日期=系统日期
		orderdetail.setEndtime(new Date());
		//	1.2 库管员 = 当前登陆用户的编号
		orderdetail.setEnder(empuuid);
		//	1.3 设置仓库编号 = 前端 传过来
		orderdetail.setStoreuuid(storeuuid);
		//	1.3 状态=已出库 1
		orderdetail.setState(Orderdetail.STATE_OUT);
		
		//***********************2. 商品仓库库存 操作***********************
		//	2.1 判断是否存在库存信息, 条件：商品编号(明细记录里有)与仓库编号(前端 传过来)
		//		查询库存表select * from storedetail where goodsuuid=? and storeuuid=?
		//	构建查询条件
		Storedetail storedetail = new Storedetail();
		// 设置查询条件：商品编号(明细记录里有)
		storedetail.setGoodsuuid(orderdetail.getGoodsuuid());
		// 设置查询条件：仓库编号(前端 传过来)
		storedetail.setStoreuuid(storeuuid);
		// 根据条件进行查询
		List<Storedetail> storedetailList = storedetailDao.getList(storedetail, null, null);
		//商品仓库库存数量
	    long num = -1l;
	    //如果存在库存，检查库存是否足够
	    if(null != storedetailList && storedetailList.size() > 0){
	    	storedetail = storedetailList.get(0);
	        num = storedetail.getNum().longValue() - orderdetail.getNum().longValue();
	    }
	    if(num > 0){
	    	//库存充足，则更新库存数量
	    	storedetail.setNum(num);
	    }else{
	    	//库存不足，则提示用户
	        throw new ErpException("库存不足!");
	    }
		//***********************3. 商品库存变更记录 操作***********************
		//	插出一条日志记录(
		//		操作员工编号=当前登陆用户的编号
		//		操作日期=系统日期
		//		仓库编号=前端 传过来
		//		商品编号=明细记录里有
		//		数量=明细记录里有
		//		操作类型=出库(2)
		//	)
		Storeoper log = new Storeoper();
		//		操作员工编号=当前登陆用户的编号
		log.setEmpuuid(empuuid);
		//		操作日期=系统日期 
		log.setOpertime(orderdetail.getEndtime());
		//		仓库编号=前端 传过来
		log.setStoreuuid(storeuuid);
		//		商品编号=明细记录里有
		log.setGoodsuuid(orderdetail.getGoodsuuid());
		//		数量=明细记录里有
		log.setNum(orderdetail.getNum());
		//		操作类型=出库(2)
		log.setType(Storeoper.TYPE_OUT);
		storeoperDao.add(log);
		
		//***********************4. 订单表 操作***********************

		//明细对应的订单, 进出持久化状态
		Orders orders = orderdetail.getOrders();
		
		//	4.1 条件：所有的明细都已经完成出库？
		//		根据订单编号查看是否存在未出库的明细
		//		select count(1) from orderdetail where state='0' and ordersuuid=订单编号(明细记录里有)
		//  构建查询条件
		Orderdetail queryParam = new Orderdetail();
		// 未出库的明细
		queryParam.setState(Orderdetail.STATE_NOT_OUT);
		// 订单
		queryParam.setOrders(orders);
		// 查询
		long count = orderdetailDao.getCount(queryParam, null, null);
		//	4.2 如果所有的明细都已经完成出库
		if(count == 0){
		//		4.2.1. 出库日期=系统日期
			orders.setEndtime(orderdetail.getEndtime());
		//		4.2.2. 库管员=当前登陆用户的编号
			orders.setEnder(empuuid);
		//		4.2.3. 订单状态=已出库(1)
			orders.setState(Orders.STATE_OUT);
			//获取订单的客户信息
			Supplier customer = supplierDao.get(orders.getSupplieruuid());
			
			//Long userId, String toaddress, String addressee, String tele, String info
			//调用物流服务在线预约下单，此时返回运单号
			Long waybillSn = waybillWs.add(1L, customer.getAddress(), customer.getName(), customer.getTele(), "--");
			//设置订单中的运单号
			orders.setWaybillsn(waybillSn);
		}		
		
	}

}
