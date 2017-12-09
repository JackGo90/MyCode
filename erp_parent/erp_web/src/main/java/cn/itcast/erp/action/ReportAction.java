package cn.itcast.erp.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.itcast.erp.action.util.WebUtil;
import cn.itcast.erp.biz.IReportBiz;

/**
 * 报表action
 *
 */
public class ReportAction {

	private Date startDate;
	private Date endDate;
	private int year;
	public void setYear(int year) {
		this.year = year;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	private IReportBiz reportBiz;
	public void setReportBiz(IReportBiz reportBiz) {
		this.reportBiz = reportBiz;
	}
	
	/**
	 * 销售统计报表
	 */
	public void orderReport(){
		List<Map<String,Object>> orderReport = reportBiz.orderReport(startDate, endDate);
		WebUtil.write(orderReport);
	}
	
	/**
	 * 销售趋势
	 */
	public void trendReport(){
		List<Map<String, Object>> list = reportBiz.trendReport(year);
		WebUtil.write(list);
	}
	
	/**
	 * 销售趋势
	 */
	public void tr(){
		WebUtil.write(reportBiz.tr(year));
	}
	
	/**
	 * 退货趋势
	 */
	public void returnTrendReport(){
		List<Map<String, Object>> returnTrendReport = reportBiz.returnTrendReport(year);
		WebUtil.write(returnTrendReport);
	}
	
	
}
