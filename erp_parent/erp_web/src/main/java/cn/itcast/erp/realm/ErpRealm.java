package cn.itcast.erp.realm;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.util.Const;
import redis.clients.jedis.Jedis;

public class ErpRealm extends AuthorizingRealm {
	
	private IEmpBiz empBiz;
	
	private IMenuBiz menuBiz;
	
	private Jedis jedis;

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	public void setMenuBiz(IMenuBiz menuBiz) {
		this.menuBiz = menuBiz;
	}

	public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.out.println("调用授权...");
		//2. 给用户 设置访问的权限
		// 当前登陆用户的权限集合信息
		SimpleAuthorizationInfo sai = new SimpleAuthorizationInfo();
		//sai.addStringPermission("采购订单查询");
		//sai.addStringPermission("采购订单申请");
		// 获取当事人
		Emp emp = (Emp)principals.getPrimaryPrincipal();
		
		// 转成json字符串
		String menuString = jedis.get(Const.MENU_KEY + emp.getUuid());
		List<Menu> menus = null;
		if(StringUtils.isEmpty(menuString)){
			// 获取登陆用户的菜单权限
			menus = menuBiz.getMenusByEmpuuid(emp.getUuid());
			// 转成json字符串
			menuString = JSON.toJSONString(menus);
			// 放入缓存中
			jedis.set(Const.MENU_KEY + emp.getUuid(), menuString);
		}else{
			// 转成java 的menus
			menus = JSON.parseArray(menuString,Menu.class);
		}
		
		// 授权
		for (Menu menu : menus) {
			sai.addStringPermission(menu.getMenuname());
		}
		//sai.addRole("admin");
		return sai;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("调用认证...");
		//转成令牌
		UsernamePasswordToken upt = (UsernamePasswordToken)token;
		//用户名
		String username = upt.getUsername();
		//密码
		String pwd = new String(upt.getPassword());
		
		//数据库查询
		Emp emp = empBiz.findByUsernameAndPwd(username, pwd);
		if(emp == null){
			throw new AuthenticationException("用户名或密码不正确");
		}
		//principal 主角, 相当于把登陆用户信息放入session中
		//credentials 凭证：与token的密码进行比较，如果相同，则成功，否则认证失败
		//realmName
		return new SimpleAuthenticationInfo(emp,pwd,getName());
	}

}
