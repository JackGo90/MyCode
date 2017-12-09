package cn.itcast.erp.biz.impl;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.crypto.hash.Md5Hash;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.Const;
import redis.clients.jedis.Jedis;
/**
 * 员工业务逻辑类
 * @author Administrator
 *
 */
public class EmpBiz extends BaseBiz<Emp> implements IEmpBiz {

	private IEmpDao empDao;
	private IRoleDao roleDao;
	private Jedis jedis;
	
	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
	}

	private int hashIteration = 2;
	
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
		super.setBaseDao(this.empDao);
	}

	@Override
	public Emp findByUsernameAndPwd(String username, String pwd) {
		// TODO Auto-generated method stub
		// source: 要加密码的内容
		// salt: 盐, 扰乱码
		// hashIteration: 散列次数  2
		/*Md5Hash md5 = new Md5Hash(pwd,username,2);
		String newPwd = md5.toString();
		System.out.println("newPwd:" + newPwd);*/
		return this.empDao.findByUsernameAndPwd(username, encrypt(pwd, username));
	}
	
	@Override
	public void add(Emp emp) {
		//新增员工时，加密密码
		//初始密码为登陆名
		String encryptedPwd = encrypt(emp.getUsername(),emp.getUsername());
		emp.setPwd(encryptedPwd);
		super.add(emp);
	}
	
	/**
	 * 加密
	 * @param src
	 * @param salt
	 * @return
	 */
	private String encrypt(String src, String salt){
		Md5Hash md5 = new Md5Hash(src,salt,hashIteration);
		return md5.toString();
	}

	@Override
	public void updatePwd(String oldPwd, String newPwd, Long uuid) {
		//获取员工
		Emp emp = empDao.get(uuid);
		//加密原密码
		oldPwd = encrypt(oldPwd, emp.getUsername());
		//看原密码是否与数据库中的密码一致
		if(!oldPwd.equals(emp.getPwd())){
			throw new ErpException("原密码不正确!");
		}
		//加密新密码
		newPwd = encrypt(newPwd, emp.getUsername());
		//更新 密码
		empDao.updatePwd(newPwd,uuid);
	}

	@Override
	public void updatePwd_reset(String newPwd, Long uuid) {
		//获取员工
		Emp emp = empDao.get(uuid);
		//新密码要加密
		empDao.updatePwd(encrypt(newPwd, emp.getUsername()), uuid);
	}

	@Override
	public List<Tree> readEmpRoles(Long uuid) {
		//获取员工信息
		Emp emp = empDao.get(uuid);
		// 获取用户下的角色列表
		List<Role> empRoles = emp.getRoles();
		
		//所有的角色信息
		List<Role> roles = roleDao.getList(null, null, null);
		List<Tree> result = new ArrayList<Tree>();
		//把角色信息转树的结构
		for(Role role : roles){
			Tree t1 = createTree(role);
			//判断用户的拥有的角色中，是否包含这个角色
			if(empRoles.contains(role)){
				//有这个角色时，让它选中
				t1.setChecked(true);
			}
			result.add(t1);
		}
		return result;
	}

	@Override
	public void updateEmpRoles(Long uuid, String ids) {
		// 获取员工信息，进行入持久化
		Emp emp = empDao.get(uuid);
		// 删除旧关系
		emp.setRoles(new ArrayList<Role>());
		
		String[] roleUuids = ids.split(",");
		for(String roleUuid : roleUuids){
			//添加新的关系
			emp.getRoles().add(roleDao.get(Long.valueOf(roleUuid)));
		}
		// 清除缓存中用户权限
		jedis.del(Const.MENU_KEY + uuid);
	}
	
	/**
	 * 把角色转成树的结构
	 * @param menu
	 * @return
	 */
	private Tree createTree(Role role){
		Tree t1 = new Tree();
		t1.setText(role.getName());
		t1.setId(role.getUuid() + "");
		t1.setChildren(new ArrayList<Tree>());
		return t1;
	}
	
}
