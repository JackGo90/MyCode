package cn.itcast.erp.biz.impl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import cn.itcast.erp.biz.ISupplierBiz;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Supplier;
/**
 * 供应商业务逻辑类
 * @author Administrator
 *
 */
public class SupplierBiz extends BaseBiz<Supplier> implements ISupplierBiz {

	private ISupplierDao supplierDao;
	
	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
		super.setBaseDao(this.supplierDao);
	}

	@Override
	public void export(Supplier t1, OutputStream os) throws IOException {
		//查询符合条件的所有数据
		List<Supplier> list = supplierDao.getList(t1, null, null);
		
		// 创建工作簿
		Workbook wk = new HSSFWorkbook();
		
		String shtName = "";
		if(Supplier.TYPE_SUPPLIER.equals(t1.getType())){
			shtName = "供应商";
		}
		if(Supplier.TYPE_CUSTOMER.equals(t1.getType())){
			shtName = "客户";
		}
		try{
			// 创建工作表
			Sheet sht = wk.createSheet(shtName);
			// 创建0行，放表头
			Row row = sht.createRow(0);//行/列的索引从0开, 行和单元必须创建后才能使用
			String[] headerNames = {"名称","地址","联系人","电话","Email"};
			int[] columnsWidths = {4000,8000,2000,3000,8000};
			Cell cell = null;
			int i = 0;
			for(; i < headerNames.length; i++){
				cell = row.createCell(i);
				cell.setCellValue(headerNames[i]);
				//设置列宽
				sht.setColumnWidth(i, columnsWidths[i]);
			}
			i = 1;
			//循环结果集
			for(Supplier supplier : list){
				row = sht.createRow(i);
				
				row.createCell(0).setCellValue(supplier.getName());
				row.createCell(1).setCellValue(supplier.getAddress());
				row.createCell(2).setCellValue(supplier.getContact());
				row.createCell(3).setCellValue(supplier.getTele());
				row.createCell(4).setCellValue(supplier.getEmail());
				i++;//行号增加
			}
			wk.write(os);
		} finally{
			wk.close();
		}
	}

	@Override
	public void doImport(InputStream is) throws Exception {
		Workbook wk = null;
		try {
			//读取工作簿
			wk = new HSSFWorkbook(is);
			//获得第一个工作
			Sheet sht = wk.getSheetAt(0);
			//工作表名称
			String shtName = sht.getSheetName();
			String type = "";
			if("供应商".equals(shtName)){
				type = "1";
			}
			if("客户".equals(shtName)){
				type = "2";
			}
			Row row = null;
			//getLastRowNum 最后的行号
			Supplier supplier = null;
			for(int i = 1; i <= sht.getLastRowNum(); i++){
				row = sht.getRow(i);//读取一行
				if(row == null){
					continue;
				}
				supplier = new Supplier();
				//供应商的名称
				String name = row.getCell(0).getStringCellValue();
				supplier.setName(name);
				//通过名称查询是否存在数据
				List<Supplier> list = supplierDao.getList(null, supplier, null);
				if(list.size() > 0){
					supplier = list.get(0);//此时进入持久化状态
				}
				supplier.setAddress(row.getCell(1).getStringCellValue());//地址
				supplier.setContact(row.getCell(2).getStringCellValue());//联系人
				supplier.setTele(row.getCell(3).getStringCellValue());//电话
				supplier.setEmail(row.getCell(4).getStringCellValue());//邮箱
				
				//不存在的情况，新增记录
				if(list.size() == 0){
					//类型
					supplier.setType(type);
					supplierDao.add(supplier);
				}
			}
		} finally {
			// TODO Auto-generated catch block
			if(null != wk){
				wk.close();
			}
		}
		
	}
	
}
