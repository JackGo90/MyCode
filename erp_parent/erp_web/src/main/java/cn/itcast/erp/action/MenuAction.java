package cn.itcast.erp.action;
import cn.itcast.erp.action.util.WebUtil;
import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;

/**
 * 菜单Action 
 * @author Administrator
 *
 */
public class MenuAction extends BaseAction<Menu> {

	private IMenuBiz menuBiz;

	public void setMenuBiz(IMenuBiz menuBiz) {
		this.menuBiz = menuBiz;
		super.setBaseBiz(this.menuBiz);
	}
	
	/**
	 * 读取菜单
	 */
	public void getMenuTree(){
		//得到主菜单，此时进入持久化状态
		/*Menu root = menuBiz.get("0");
		WebUtil.write(root);*/
		Emp emp = WebUtil.getLoginUser();
		if(null != emp){
			Menu menu = menuBiz.readMenusByEmpuuid(emp.getUuid());
			WebUtil.write(menu);
		}
	}

}
