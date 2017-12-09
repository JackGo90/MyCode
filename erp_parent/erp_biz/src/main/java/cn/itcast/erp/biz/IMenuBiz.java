package cn.itcast.erp.biz;
import java.util.List;

import cn.itcast.erp.entity.Menu;
/**
 * 菜单业务逻辑层接口
 * @author Administrator
 *
 */
public interface IMenuBiz extends IBaseBiz<Menu>{
	
	/**
	 * 获取用户的所有权限菜单
	 * @param empuuid
	 * @return
	 */
	List<Menu> getMenusByEmpuuid(Long empuuid);
	
	/**
	 * 读取用户的所有权限菜单
	 * @param empuuid
	 * @return
	 */
	Menu readMenusByEmpuuid(Long empuuid);
}

