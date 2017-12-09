package cn.itcast.erp.dao.impl;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.ParameterMode;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureOutputs;
import org.springframework.orm.hibernate5.HibernateCallback;

import cn.itcast.erp.dao.IOrderdetailDao;
import cn.itcast.erp.entity.Orderdetail;
/**
 * 订单明细数据访问类
 * @author Administrator
 *
 */
public class OrderdetailDao extends BaseDao<Orderdetail> implements IOrderdetailDao {

	/**
	 * 构建查询条件
	 * @param dep1
	 * @param dep2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Orderdetail orderdetail1,Orderdetail orderdetail2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Orderdetail.class);
		if(orderdetail1!=null){
			if(null != orderdetail1.getGoodsname() && orderdetail1.getGoodsname().trim().length()>0){
				dc.add(Restrictions.like("goodsname", orderdetail1.getGoodsname(), MatchMode.ANYWHERE));
			}
			if(null != orderdetail1.getState() && orderdetail1.getState().trim().length()>0){
				dc.add(Restrictions.eq("state", orderdetail1.getState()));
			}
			//根据订单查询
			if(null != orderdetail1.getOrders() && null != orderdetail1.getOrders().getUuid()){
				dc.add(Restrictions.eq("orders", orderdetail1.getOrders()));
			}

		}
		return dc;
	}
	
	@Override
	public Map<String, Object> doOutStore(final Long uuid, final Long storeuuid, final Long empuuid) {
		// TODO Auto-generated method stub
		return this.getHibernateTemplate().execute(new HibernateCallback<Map<String, Object>>() {

			@Override
			public Map<String, Object> doInHibernate(Session session) throws HibernateException {
				Map<String, Object> rtnMap = new HashMap<String, Object>();
				ProcedureCall pc = session.createStoredProcedureCall("PROC_DOOUTSTORE");
				//注册输入参建
				pc.registerParameter(1, long.class, ParameterMode.IN).bindValue(uuid);
				pc.registerParameter(2, long.class, ParameterMode.IN).bindValue(storeuuid);
				pc.registerParameter(3, long.class, ParameterMode.IN).bindValue(empuuid);
				
				//注册输出参数
				pc.registerParameter(4, int.class, ParameterMode.OUT);//outFlag
				pc.registerParameter(5, String.class, ParameterMode.OUT);//outMessage
				
				ProcedureOutputs outputs = pc.getOutputs();//获取执行的结果
				int outFlag = (int)outputs.getOutputParameterValue(4);//outFlag
				String outMessage = (String)outputs.getOutputParameterValue(5);//outMessage
				rtnMap.put("outFlag", outFlag);
				rtnMap.put("outMessage", outMessage);
				return rtnMap;
			}
		});
	}
}
