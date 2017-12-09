package cn.itcast.erp.biz.impl;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.erp.biz.IRoleBiz;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
import cn.itcast.erp.util.Const;
import redis.clients.jedis.Jedis;
/**
 * 角色业务逻辑类
 * @author Administrator
 *
 */
public class RoleBiz extends BaseBiz<Role> implements IRoleBiz {

	private IRoleDao roleDao;
	private IMenuDao menuDao;
	private Jedis jedis;
	
	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
	}

	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
		super.setBaseDao(this.roleDao);
	}

	@Override
	public List<Tree> readRoleMenus(Long uuid) {
		//获取角色信息
		Role role = roleDao.get(uuid);
		// 角色所拥有的权限， 此时，角色下所有的menu都进入持久化状态
		List<Menu> roleMenus = role.getMenus();
		
		// 获取所有的菜单
		Menu root = menuDao.get("0");
		//返回的结果集
		List<Tree> result = new ArrayList<Tree>();
		// 循环一级菜单， 此时也进入持久化状态
		for(Menu lv1 : root.getMenus()){
			Tree t1 = createTree(lv1);
			//循环二级菜单， 此时也进入持久化状态
			for(Menu lv2 : lv1.getMenus()){
				Tree t2 = createTree(lv2);
				//hibernate中，进入持久化状态的对象，同一类型同一ID，只有一个对象
				if(roleMenus.contains(lv2)){
					//角色的权限集合中，包含有这个菜单，让它选中
					t2.setChecked(true);
				}
				t1.getChildren().add(t2);
			}
			result.add(t1);
		}
		return result;
	}
	
	/**
	 * 把菜单转成树的结构
	 * @param menu
	 * @return
	 */
	private Tree createTree(Menu menu){
		Tree t1 = new Tree();
		t1.setText(menu.getMenuname());
		t1.setId(menu.getMenuid());
		t1.setChildren(new ArrayList<Tree>());
		return t1;
	}

	@Override
	public void updateRoleMenus(Long uuid, String ids) {
		//获取角色，进入持久化状态
		Role role = roleDao.get(uuid);
		//删除角色下的所有菜单权限 delete from role_menu where roleuuid=uuid
		// 删除旧的关系 
		role.setMenus(new ArrayList<Menu>());
		
		//加上新的关系
		String[] idArr = ids.split(",");
		for(String id : idArr){
			//先取出菜单，再设置
			role.getMenus().add(menuDao.get(id));
		}
		
		// 清除缓存
		List<Emp> emps = role.getEmps();
		for (Emp emp : emps) {
			// 删除用户的权限缓存
			jedis.del(Const.MENU_KEY + emp.getUuid());
		}
		
	}
	
}
