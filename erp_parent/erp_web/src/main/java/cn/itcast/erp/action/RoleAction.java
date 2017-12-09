package cn.itcast.erp.action;
import java.util.List;

import cn.itcast.erp.action.util.WebUtil;
import cn.itcast.erp.biz.IRoleBiz;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;

/**
 * 角色Action 
 * @author Administrator
 *
 */
public class RoleAction extends BaseAction<Role> {

	private IRoleBiz roleBiz;

	public void setRoleBiz(IRoleBiz roleBiz) {
		this.roleBiz = roleBiz;
		super.setBaseBiz(this.roleBiz);
	}
	
	/**
	 * 获取角色下的权限菜单
	 */
	public void readRoleMenus(){
		List<Tree> list = roleBiz.readRoleMenus(getId());
		WebUtil.write(list);
	}
	
	private String ids;//菜单的编号，多个以逗号分割
	
	public void setIds(String ids) {
		this.ids = ids;
	}

	/**
	 * 更新角色的权限菜单
	 */
	public void updateRoleMenus(){
		try {
			roleBiz.updateRoleMenus(getId(), ids);
			WebUtil.ajaxReturn(true, "更新成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "更新失败");
		}
	}

}
