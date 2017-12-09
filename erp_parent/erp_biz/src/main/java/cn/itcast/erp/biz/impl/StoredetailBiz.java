package cn.itcast.erp.biz.impl;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.mail.MailUtil;
/**
 * 仓库库存业务逻辑类
 * @author Administrator
 *
 */
public class StoredetailBiz extends BaseBiz<Storedetail> implements IStoredetailBiz {

	private IStoredetailDao storedetailDao;
	private IStoreDao storeDao;
	private IGoodsDao goodsDao;
	
	//********邮件相关设置************
	private MailUtil mailUtil; //邮件工具
	private String subject; // 标题
	private String to; // 收件人
	private String content; // 内容
	//***************************

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}

	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
		super.setBaseDao(this.storedetailDao);
	}
	
	@Override
	public List<Storedetail> getListByPage(Storedetail t1, Storedetail t2, Object param, int firstResult,
			int maxResults) {
		List<Storedetail> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		for(Storedetail od : list){
			//商品名称
			od.setGoodsName(goodsDao.get(od.getGoodsuuid()).getName());
			//仓库名称
			od.setStoreName(storeDao.get(od.getStoreuuid()).getName());
		}
		return list;
	}

	@Override
	public List<Storealert> storealertList() {
		return storedetailDao.storealertList();
	}

	@Override
	public void sendStorealertMail() throws Exception {
		//获取需要预警的商品列表
		List<Storealert> list = storedetailDao.storealertList();
		if(null != list && list.size() > 0){
			//有需要预警的商品，发邮件
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String _subject = subject.replace("[time]", sdf.format(new Date()));
			String _content = content.replace("[count]", list.size() + "");
			//发邮件
			mailUtil.sendMail(_subject, to, _content);
		}else{
			throw new ErpException("没有需要预警的商品");
		}
	}
	
}
