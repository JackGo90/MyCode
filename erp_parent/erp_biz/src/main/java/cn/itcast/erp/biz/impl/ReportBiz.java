package cn.itcast.erp.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IReportBiz;
import cn.itcast.erp.dao.IReportDao;

public class ReportBiz implements IReportBiz {
	
	private IReportDao reportDao;

	public void setReportDao(IReportDao reportDao) {
		this.reportDao = reportDao;
	}

	@Override
	public List<Map<String,Object>> orderReport(Date startDate, Date endDate) {
		return reportDao.orderReport(startDate, endDate);
	}

	@Override
	public List<Map<String, Object>> trendReport(int year) {
		List<Map<String, Object>> monthsDataInDb = reportDao.trendReport(year);
		//[{y=3249.9, name=10},{y=3249.9, name=11},{y=3249.9, name=12}]
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		//************************补数据************************
		Map<String, Object> data = null;
		//{y=3249.9, name=12}, i代表月份
		for(int i = 1; i <= 12; i++){
			data = null;
			//判断数据是否存在
			for(Map<String, Object> dbData : monthsDataInDb){
				int yue = (int)dbData.get("name");
				if(i == yue){
					data = dbData;//拿出数据库中的数据
					break;
				}
			}
			//不存在数据的情况下
			if(data == null){
				data = new HashMap<String, Object>();
				data.put("name", i);
				data.put("y", 0);
			}
			result.add(data);
		}
		
		return result;
	}
	

	@Override
	public String tr(int year) {
		String result = "[{\"name\":\"1\",\"y\":0}, {\"name\":\"2\",\"y\":0}, {\"name\":\"3\",\"y\":0}, {\"name\":\"4\",\"y\":0}, {\"name\":\"5\",\"y\":0}, {\"name\":\"6\",\"y\":0}, {\"name\":\"7\",\"y\":0}, {\"name\":\"8\",\"y\":0}, {\"name\":\"9\",\"y\":0}, {\"name\":\"10\",\"y\":0}, {\"name\":\"11\",\"y\":0}, {\"name\":\"12\",\"y\":0}]";
		List<Map<String, Object>> monthsDataInDb = reportDao.trendReport(year);
		for(Map<String, Object> monthData : monthsDataInDb){
			String replacement = String.format("{\"name\":\"%d\",\"y\":0}",(int)monthData.get("name"));
			String newValue = JSON.toJSONString(monthData);
			result = result.replace(replacement, newValue);
		}
		return result;
	}

	
	
	//退货趋势
	@Override
	public List<Map<String, Object>> returnTrendReport(int year) {
		List<Map<String, Object>> monthsDataInDb = reportDao.returnTrendReport(year);
		//[{y=3249.9, name=10},{y=3249.9, name=11},{y=3249.9, name=12}]
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		//************************补数据************************
		Map<String, Object> data = null;
		//{y=3249.9, name=12}, i代表月份
		for(int i = 1; i <= 12; i++){
			data = null;
			//判断数据是否存在
			for(Map<String, Object> dbData : monthsDataInDb){
				int yue = (int)dbData.get("name");
				if(i == yue){
					data = dbData;//拿出数据库中的数据
					break;
				}
			}
			//不存在数据的情况下
			if(data == null){
				data = new HashMap<String, Object>();
				data.put("name", i);
				data.put("y", 0);
			}
			result.add(data);
		}
		
		return result;
	}

}