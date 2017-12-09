package cn.itcast.erp.action.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.entity.Emp;

public class WebUtil {

	/**
	 * //{"name":"管理员组","tele":"000011","uuid":1} 
	 * @param jsonString JSON数据字符串
	 * @param prefix 要加上的前缀
	 * @return  {"t.name":"管理员组","t.tele":"000011","t.uuid":1} 
	 */
	public static String mapData(String jsonString, String prefix){
		Map<String, Object> map = JSON.parseObject(jsonString);
		
		//存储key加上前缀后的值
		Map<String, Object> dataMap = new HashMap<String, Object>();
		//给每key值加上前缀
		for(String key : map.keySet()){
			//员工时，部门是一个对象 key为t.dep
			/*var data ={"t.address":"建材城西路中腾商务大厦","t.birthday":"1949-10-01",
			"t.dep":{"name":"管理员组","tele":"000000999","uuid":1},
			"t.email":"admin@itcast.cn","t.gender":1,"t.name":"超级管理员","t.tele":"12345678","t.username":"admin","t.uuid":1};
			
			转成:
			
			{"t.address":"建材城西路中腾商务大厦","t.birthday":"1949-10-01",
			"t.dep.name":"管理员组","t.dep.tele":"000000999","t.dep.uuid":1,
			"t.email":"admin@itcast.cn","t.gender":1,"t.name":"超级管理员","t.tele":"12345678","t.username":"admin","t.uuid":1};
			*/
			if(map.get(key) instanceof Map){
				//key="t.dep
				
				@SuppressWarnings("unchecked")
				Map<String,Object> depMap = (Map<String,Object>)map.get(key);
				//{"name":"管理员组","tele":"000000999","uuid":1}
				for(String depKey : depMap.keySet()){
					String newKey = prefix + "." + key + "." + depKey;
					//"t.dep.name":"管理员组"
					dataMap.put(newKey, depMap.get(depKey));
				}
			}else{
				dataMap.put(prefix + "." + key, map.get(key));
			}
		}
		return JSON.toJSONString(dataMap);
	}
	
	/**
	 * 返回前端操作结果
	 * @param success
	 * @param message
	 */
	public static void ajaxReturn(boolean success, String message){
		//返回前端的JSON数据
		Map<String, Object> rtn = new HashMap<String, Object>();
		rtn.put("success",success);
		rtn.put("message",message);
		write(JSON.toJSONString(rtn));
	}
	
	public static void write(Object obj){
		WebUtil.write(JSON.toJSONString(obj));
	}
	
	/**
	 * 输出字符串到前端
	 * @param jsonString
	 */
	public static void write(String jsonString){
		try {
			//响应对象
			HttpServletResponse response = ServletActionContext.getResponse();
			//设置编码
			response.setContentType("text/html;charset=utf-8"); 
			//输出给页面
			response.getWriter().write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取当前登陆用户
	 * @return
	 */
	public static Emp getLoginUser(){
		return (Emp)SecurityUtils.getSubject().getPrincipal();
	}
}
