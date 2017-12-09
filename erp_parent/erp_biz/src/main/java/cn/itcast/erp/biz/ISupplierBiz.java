package cn.itcast.erp.biz;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.itcast.erp.entity.Supplier;
/**
 * 供应商业务逻辑层接口
 * @author Administrator
 *
 */
public interface ISupplierBiz extends IBaseBiz<Supplier>{

	/**
	 * 数据导出
	 * @param t1
	 * @param os
	 */
	void export(Supplier t1, OutputStream os) throws IOException;
	
	/**
	 * 导入数据
	 * @param is
	 */
	void doImport(InputStream is) throws Exception; 
}

