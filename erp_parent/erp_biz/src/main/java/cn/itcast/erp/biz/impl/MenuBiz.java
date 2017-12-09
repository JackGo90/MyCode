package cn.itcast.erp.biz.impl;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.entity.Menu;
/**
 * 菜单业务逻辑类
 * @author Administrator
 *
 */
public class MenuBiz extends BaseBiz<Menu> implements IMenuBiz {

	private IMenuDao menuDao;
	
	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
		super.setBaseDao(this.menuDao);
	}

	@Override
	public List<Menu> getMenusByEmpuuid(Long empuuid) {
		return menuDao.getMenusByEmpuuid(empuuid);
	}

	@Override
	public Menu readMenusByEmpuuid(Long empuuid) {
		// 模板 所有的菜单
		Menu root = menuDao.get("0");
		
		// 用户所拥有的菜单
		List<Menu> empMenus = menuDao.getMenusByEmpuuid(empuuid);
		
		//返回的菜单
		Menu result = cloneMenu(root);
		
		//循环复制
		List<Menu> l1_menus = root.getMenus();//取出所有的一级菜单
		//循环一级菜单
		for(Menu l1 : l1_menus){
			//复制中
			Menu _m1 = cloneMenu(l1);
			//取出一级菜单下的二级菜单
			List<Menu> l2_menus = l1.getMenus();
			//循环二级菜单
			for(Menu l2 : l2_menus){
				
				//如果用户的菜单中，包含这个菜单,就应该复制过来
				if(empMenus.contains(l2)){
					Menu _m2 = cloneMenu(l2);
					//加到复制的一级菜单中
					_m1.getMenus().add(_m2);
				}
			}
			
			if(_m1.getMenus().size() > 0){
				//一级菜单下有二级菜单，才要放进来
				result.getMenus().add(_m1);
			}
		}
		
		return result;
	}
	
	/**
	 * 复制菜单
	 * @param src
	 * @return
	 */
	private Menu cloneMenu(Menu src){
		Menu _m1 = new Menu();
		_m1.setIcon(src.getIcon());
		_m1.setMenuid(src.getMenuid());
		_m1.setMenuname(src.getMenuname());
		_m1.setUrl(src.getUrl());
		_m1.setMenus(new ArrayList<Menu>());
		return _m1;
	}
	
}
