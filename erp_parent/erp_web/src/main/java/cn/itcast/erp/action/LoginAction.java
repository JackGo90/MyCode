package cn.itcast.erp.action;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import cn.itcast.erp.action.util.WebUtil;
import cn.itcast.erp.entity.Emp;

/**
 * 登陆出操作action
 *
 */
public class LoginAction {
	private String username;
	private String pwd;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	/**
	 * 登陆验证
	 */
	public void checkUser(){
		//当事人 登陆用户 逻辑概念
		Subject subject = SecurityUtils.getSubject();
		//令牌 凭证
		UsernamePasswordToken upt = new UsernamePasswordToken(username, pwd);
		//要登陆了，调login方法
		try {
			subject.login(upt);
			WebUtil.ajaxReturn(true,"登陆成功");
		} catch (AuthenticationException e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false,"登陆失败");
		}
		
	}
	/*public void checkUser(){
		Emp emp = empBiz.findByUsernameAndPwd(username, pwd);
		if(null != emp){
			ServletActionContext.getRequest().getSession().setAttribute("loginUser", emp);
			WebUtil.ajaxReturn(true,"登陆成功");
		}else{
			WebUtil.ajaxReturn(false,"登陆失败");
		}
	}*/

	/**
	 * 显示登陆用户名
	 */
	public void showName(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null != loginUser){
			WebUtil.ajaxReturn(true,loginUser.getName());
		}else{
			WebUtil.ajaxReturn(false, "亲，您还没有登陆！");
		}
	}

	/**
	 * 退出登陆
	 */
	public void loginOut(){
		SecurityUtils.getSubject().logout();
		//ServletActionContext.getRequest().getSession().removeAttribute("loginUser");
	}
}
