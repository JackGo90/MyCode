package cn.itcast.erp.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.entity.Menu;

public class FilterChainDefinitionMapFactoryBean implements FactoryBean<Map<String,String>> {
	
	private IMenuBiz menuBiz;

	public void setMenuBiz(IMenuBiz menuBiz) {
		this.menuBiz = menuBiz;
	}

	@Override
	public Map<String,String> getObject() throws Exception {
		LinkedHashMap<String,String> map = new LinkedHashMap<String,String>();
		init(map);
		List<Menu> menus = menuBiz.getList(null, null, null);
		//orders_add.action= perms["我的采购订单","我的销售订单"]
		Map<String,List<String>> valueMap = new HashMap<String,List<String>>();
		List<String> list = null;
		for (Menu menu : menus) {
			String url = menu.getUrl();
			if(!StringUtils.isEmpty(url)){
				if(url.indexOf("?") > -1){
					url = url.substring(0, url.indexOf("?"));
				}
				String key = "/" + url;
				list = valueMap.get(key);
				if(null == list){
					list = new ArrayList<String>();
					valueMap.put(key, list);
				}
				list.add(menu.getMenuname());
				map.put(key, "perms" + JSON.toJSONString(list));
			}
		}
		map.put("/**","authc");
		return map;
	}
	
	private void init(Map<String,String> map){
		map.put("/adminjs/**", "anon");
		map.put("/css/**", "anon");
		map.put("/images/**", "anon");
		map.put("/js/**", "anon");
		map.put("/ui/**", "anon");
		map.put("/login.html", "anon");
		map.put("/login_checkUser.action", "anon");
	}

	@Override
	public Class<?> getObjectType() {
		// TODO Auto-generated method stub
		return Map.class;
	}

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

}
