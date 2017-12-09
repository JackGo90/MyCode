package cn.itcast.erp.action;
import cn.itcast.erp.action.util.WebUtil;
import cn.itcast.erp.biz.IStoreBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Store;

/**
 * 仓库Action 
 * @author Administrator
 *
 */
public class StoreAction extends BaseAction<Store> {

	private IStoreBiz storeBiz;

	public void setStoreBiz(IStoreBiz storeBiz) {
		this.storeBiz = storeBiz;
		super.setBaseBiz(this.storeBiz);
	}
	
	/**
	 * 我的仓库
	 */
	public void myList(){
		//获取当前登陆用户
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "亲，您还没有登陆!");
			return;
		}
		
		//获取查询条件
		Store store = getT1();
		if(null == store){
			//构建查询条件
			store = new Store();
			//当前登陆用户的仓库
			store.setEmpuuid(loginUser.getUuid());
			//条件传t1
			setT1(store);
		}
		super.list();
	}

}
