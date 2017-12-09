package cn.itcast.erp.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表dao
 */
public interface IReportDao {

	
	
	
	public List<Map<String,Object>> returnTrendReport(int year);
	
	
	
	
	/**
	 * 销售统计报表
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Map<String,Object>> orderReport(Date startDate, Date endDate);
	
	/**
	 * 销售趋势
	 * @param year
	 * @return
	 */
	List<Map<String,Object>> trendReport(int year);
	
	
	
	
	
}
