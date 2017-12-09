package cn.itcast.erp.biz.impl;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.core.io.ClassPathResource;

import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.exception.ErpException;
import net.sf.jxls.transformer.XLSTransformer;
/**
 * 订单业务逻辑类
 * @author Administrator
 *
 */
public class OrdersBiz extends BaseBiz<Orders> implements IOrdersBiz {

	private IOrdersDao ordersDao;
	private IEmpDao empDao;
	private ISupplierDao supplierDao;
	
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	public void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
		super.setBaseDao(this.ordersDao);
	}
	
	@Override
	public void add(Orders orders) {
		//创建日期
		orders.setCreatetime(new Date());
		
		//获取主角
		Subject subject = SecurityUtils.getSubject();
		if(Orders.TYPE_IN.equals(orders.getType())){
			if(!subject.isPermitted("我的采购订单")){
				throw new ErpException("没有权限");
			}
		}else if(Orders.TYPE_OUT.equals(orders.getType())){
			if(!subject.isPermitted("我的销售订单")){
				throw new ErpException("没有权限");
			}
		}else{
			throw new ErpException("非法操作");
		}
		//订单类型：采购订单
		//orders.setType(Orders.TYPE_IN);
		//订单的状态
		orders.setState(Orders.STATE_CREATE);
		
		double totalMoney = 0;
		
		for(Orderdetail od : orders.getOrderdetails()){
			//累计金额
			totalMoney += od.getMoney();
			//明细的状态
			od.setState(Orderdetail.STATE_NOT_IN);
			//设置明细对应的订单
			od.setOrders(orders);
		}
		//总金额
		orders.setTotalmoney(totalMoney);
		
		super.add(orders);
	}
	
	@Override
	public List<Orders> getListByPage(Orders t1, Orders t2, Object param, int firstResult, int maxResults) {
		List<Orders> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		
		//员工名字 缓存
		Map<Long, String> empNameMap = new HashMap<Long, String>();
		
		//供应商名字 缓存
		Map<Long, String> supplierNameMap = new HashMap<Long, String>();
		
		//循环给每个人的名称赋值
		for(Orders o : list){
			o.setCreaterName(getEmpName(o.getCreater(),empNameMap));
			o.setCheckerName(getEmpName(o.getChecker(),empNameMap));
			o.setStarterName(getEmpName(o.getStarter(),empNameMap));
			o.setEnderName(getEmpName(o.getEnder(),empNameMap));
			o.setSupplierName(getSupplierName(o.getSupplieruuid(),supplierNameMap));
		}
		
		return list;
	}
	
	
	
	
	/**
	 * 获取员工名称
	 * @param empuuid
	 * @param empNameMap
	 * @return
	 */
	private String getEmpName(Long empuuid, Map<Long, String> empNameMap){
		if(null == empuuid){
			return null;
		}
		String name = empNameMap.get(empuuid);
		if(null == name){
			Emp emp = empDao.get(empuuid);
			name = emp.getName();
			empNameMap.put(empuuid, name);
		}
		return name;
	}
	
	/**
	 * 获取员工名称
	 * @param empuuid
	 * @param empNameMap
	 * @return
	 */
	private String getSupplierName(Long supplieruuid, Map<Long, String> supplierNameMap){
		if(null == supplieruuid){
			return null;
		}
		String name = supplierNameMap.get(supplieruuid);
		if(null == name){
			Supplier supplier = supplierDao.get(supplieruuid);
			name = supplier.getName();
			supplierNameMap.put(supplieruuid, name);
		}
		return name;
	}

	@Override
	@RequiresPermissions("采购审核")
	public void doCheck(Long uuid, Long empuuid) {
		//获取订单，进入持久化状态
		Orders orders = ordersDao.get(uuid);
		
		if(!Orders.STATE_CREATE.equals(orders.getState())){
			throw new ErpException("该订单已审核过了");
		}
		
		//设置审核日期
		orders.setChecktime(new Date());
		//设置状态
		orders.setState(Orders.STATE_CHECK);
		//审核人
		orders.setChecker(empuuid);
		
	}

	@Override
	//@CacheEvict(value = "myCache", key="'OrdersDao_' + #uuid", beforeInvocation = true)
	@RequiresPermissions("采购确认")
	public void doStart(Long uuid, Long empuuid) {
		//获取订单，进入持久化状态
		Orders orders = ordersDao.get(uuid);
		
		if(!Orders.STATE_CHECK.equals(orders.getState())){
			throw new ErpException("该订单已确认过了");
		}
		
		//设置确认日期
		orders.setStarttime(new Date());
		//设置确认状态
		orders.setState(Orders.STATE_START);
		//确认人
		orders.setStarter(empuuid);
		
	}

	@Override
	public void exportById(Long uuid, OutputStream os) throws Exception {
		Orders orders = ordersDao.get(uuid);
		
		// 创建工作簿
		Workbook wk = new HSSFWorkbook();
		String shtName = "";
		if(Orders.TYPE_IN.equals(orders.getType())){
			shtName = "采 购 单";
		}
		if(Orders.TYPE_OUT.equals(orders.getType())){
			shtName = "销 售 单";
		}
		// 创建工作表
		Sheet sht = wk.createSheet(shtName);
		// 创建行
		Row row = sht.createRow(0);//行/列的索引从0开，
		
		// 标题的样式
		CellStyle style_title = wk.createCellStyle();
		
		//内容区域的样式
		CellStyle style_content = wk.createCellStyle();
		//***************对齐方式***************
		style_content.setAlignment(CellStyle.ALIGN_CENTER);//水平居中
		style_content.setVerticalAlignment(CellStyle.VERTICAL_CENTER);//垂直居中
		// 复制内容样式给标题的样式，只要居中对齐
		style_title.cloneStyleFrom(style_content);
		// 标题字体
		Font font_title = wk.createFont();
		font_title.setFontName("黑体");//字体名称
		font_title.setFontHeightInPoints((short)18);//字段大小
		style_title.setFont(font_title);
		
		style_content.setBorderTop(CellStyle.BORDER_THIN);//细边框， 上边框
		style_content.setBorderBottom(CellStyle.BORDER_THIN);//下边框
		style_content.setBorderLeft(CellStyle.BORDER_THIN);//左边框
		style_content.setBorderRight(CellStyle.BORDER_THIN);// 右边框
		
		// 内容的字体
		Font font_content = wk.createFont();
		font_content.setFontName("宋体");//字体名称
		font_content.setFontHeightInPoints((short)11);//字段大小
		style_content.setFont(font_content);
		
		//日期样式
		CellStyle style_date = wk.createCellStyle();
		style_date.cloneStyleFrom(style_content);
		DataFormat dateFormat = wk.createDataFormat();
		style_date.setDataFormat(dateFormat.getFormat("yyyy-MM-dd"));
		//计算 要创建的行
		int rowCount = 10 + orders.getOrderdetails().size();
		for(int i = 2; i < rowCount; i++){
			//创建行
			row = sht.createRow(i);
			for(int col = 0; col < 4; col ++){
				//创建单元格, 并设置样式
				row.createCell(col).setCellStyle(style_content);
			}
			row.setHeight((short)500);//内容区域的行高
		}
		
		//***************合并单元格***************
		// 标题, CellRangeAddress(开始行的索引，结束行的索引，开始列的索引，结束列的索引)
		sht.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
		// 供应商
		sht.addMergedRegion(new CellRangeAddress(2, 2, 1, 3));
		// 订单明细
		sht.addMergedRegion(new CellRangeAddress(7, 7, 0, 3));
		
		//***************赋值,设置固定文本内容***************
		// 标题
		Cell cell_title = sht.getRow(0).createCell(0);
		cell_title.setCellStyle(style_title);
		cell_title.setCellValue(shtName);
		// 供应商
		sht.getRow(2).getCell(0).setCellValue("供应商");
		sht.getRow(3).getCell(0).setCellValue("下单日期");
		sht.getRow(4).getCell(0).setCellValue("审核日期");
		sht.getRow(5).getCell(0).setCellValue("采购日期");
		sht.getRow(6).getCell(0).setCellValue("入库日期");
		
		sht.getRow(3).getCell(2).setCellValue("经办人");
		sht.getRow(4).getCell(2).setCellValue("经办人");
		sht.getRow(5).getCell(2).setCellValue("经办人");
		sht.getRow(6).getCell(2).setCellValue("经办人");
		
		//订单明细
		sht.getRow(7).getCell(0).setCellValue("订单明细");
		sht.getRow(8).getCell(0).setCellValue("商品名称");
		sht.getRow(8).getCell(1).setCellValue("数量");
		sht.getRow(8).getCell(2).setCellValue("价格");
		sht.getRow(8).getCell(3).setCellValue("金额");
		
		//***************行高与列宽***************
		// 标题的行高
		sht.getRow(0).setHeight((short)1000);
		// 列宽
		for(int i = 0; i < 4; i++){
			sht.setColumnWidth(i, 5000);
		}
		
		// 日期测试
		sht.getRow(3).getCell(1).setCellValue(new Date());
		// 设置日期的样式
		for(int i = 0; i < 4; i++){
			sht.getRow(3 + i).getCell(1).setCellStyle(style_date);;
		}
		
		
		// 订单信息赋值
		//***************供应商的名称***************
		sht.getRow(2).getCell(1).setCellValue(supplierDao.get(orders.getSupplieruuid()).getName());
		// 日期
		sht.getRow(3).getCell(1).setCellValue(orders.getCreatetime());//下单日期
		setDateData(orders.getChecktime(),sht.getRow(4).getCell(1));//审核日期
		setDateData(orders.getStarttime(),sht.getRow(5).getCell(1));//确认日期
		setDateData(orders.getEndtime(),sht.getRow(6).getCell(1));//入库日期
		
		sht.getRow(3).getCell(3).setCellValue(getName(orders.getCreater()));// 下单人
		sht.getRow(4).getCell(3).setCellValue(getName(orders.getChecker()));// 审核人
		sht.getRow(5).getCell(3).setCellValue(getName(orders.getStarter()));// 审核人
		sht.getRow(6).getCell(3).setCellValue(getName(orders.getEnder()));// 审核人
		
		//订单明细
		int i = 9;
		for(Orderdetail od : orders.getOrderdetails()){
			row = sht.getRow(i);//第一行就是一条明细记录
			row.getCell(0).setCellValue(od.getGoodsname());
			row.getCell(1).setCellValue(od.getNum());
			row.getCell(2).setCellValue(od.getPrice());
			row.getCell(3).setCellValue(od.getMoney());
			i++;
		}
		//合计
		sht.getRow(i).getCell(0).setCellValue("合计");//合计
		sht.getRow(i).getCell(3).setCellValue(orders.getTotalmoney());//合计金额		
		
		try {
			wk.write(os);
		} finally{
			try {
				wk.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 设置日期格式的数据
	 * @param date
	 * @param cell
	 */
	private void setDateData(Date date, Cell cell){
		if(null != date){
			cell.setCellValue(date);
		}
	}
	
	/**
	 * 通过员工编号获取名称
	 * @param uuid
	 * @return
	 */
	private String getName(Long uuid){
		if(null != uuid){
			return empDao.get(uuid).getName();
		}
		return "";
	}

	@Override
	public void exportByIdWithTemplate(Long uuid, OutputStream os) throws Exception {
		Orders orders = ordersDao.get(uuid);
		//
		orders.setCreaterName(getName(orders.getCreater()));// 下单人
		orders.setCheckerName(getName(orders.getChecker()));// 审核人
		orders.setStarterName(getName(orders.getStarter()));// 审核人
		orders.setEnderName(getName(orders.getEnder()));// 审核人
		orders.setSupplierName(supplierDao.get(orders.getSupplieruuid()).getName());
		//获取模板
		ClassPathResource template = new ClassPathResource("export_orders.xls");
		
		// 通过模板创建工作簿
		Workbook wk = new HSSFWorkbook(template.getInputStream());
		
		//构建模型数据
		Map<String,Object> dataModel = new HashMap<String,Object>();
		
		dataModel.put("o", orders);
		//excel的转换器
		XLSTransformer transfer = new XLSTransformer();
		//把模型数据里的订单信息填充到模板中
		transfer.transformWorkbook(wk, dataModel);
		try {
			wk.write(os);
		} finally{
			try {
				wk.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
