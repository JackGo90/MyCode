package cn.itcast.erp.biz;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IReportBiz {

	List<Map<String,Object>> orderReport(Date startDate, Date endDate);
	
	
	//退货趋势
	public List<Map<String,Object>> returnTrendReport(int year);
	
	
	/**
	 * 销售趋势
	 * @param year
	 * @return
	 */
	List<Map<String,Object>> trendReport(int year);
	
	String tr(int year);
}
