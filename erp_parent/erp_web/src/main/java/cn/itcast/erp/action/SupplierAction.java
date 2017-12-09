package cn.itcast.erp.action;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import cn.itcast.erp.action.util.WebUtil;
import cn.itcast.erp.biz.ISupplierBiz;
import cn.itcast.erp.entity.Supplier;

/**
 * 供应商Action 
 * @author Administrator
 *
 */
public class SupplierAction extends BaseAction<Supplier> {

	private ISupplierBiz supplierBiz;
	
	private String q;
	
	public void setFile(File file) {
		this.file = file;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	private File file;//跟前端form表单中的name一致
	private String fileContentType;//文件内容格式
	private String fileFileName;//文件名

	public void setQ(String q) {
		this.q = q;
	}

	public void setSupplierBiz(ISupplierBiz supplierBiz) {
		this.supplierBiz = supplierBiz;
		super.setBaseBiz(this.supplierBiz);
	}
	
	@Override
	public void list() {
		if(!StringUtils.isEmpty(q)){
			if(null == getT1()){
				//构建查询条件
				setT1(new Supplier());
			}
			getT1().setName(q);
		}
		super.list();
	}
	
	/**
	 * 导出数据
	 */
	public void export(){
		String filename = "";
		if(Supplier.TYPE_CUSTOMER.equals(getT1().getType())){
			filename = "客户.xls";
		}
		if(Supplier.TYPE_SUPPLIER.equals(getT1().getType())){
			filename = "供应商.xls";
		}
		
		try {
			//utf-8 大 iso-8859-1  大转小 字符丢掉 乱码, 
			filename = new String(filename.getBytes(),"ISO-8859-1");
			//响应
			HttpServletResponse res = ServletActionContext.getResponse();
			res.setHeader("Content-Disposition", "attachment;filename=" + filename);
			supplierBiz.export(getT1(), res.getOutputStream());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导入数据
	 */
	public void doImport(){
		//校验文件格式
		if(!"application/vnd.ms-excel".equals(fileContentType)){
			if(!fileFileName.endsWith(".xls")){
				WebUtil.ajaxReturn(false, "文件格式不正确，只接收97-2003版的excel文件");
			}
		}
		
		try {
			supplierBiz.doImport(new FileInputStream(file));
			WebUtil.ajaxReturn(true, "导入成功");
		} catch (Exception e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "导入失败");
		}
	}

}
