package cn.itcast.erp.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

/**
 * 自定义授权过滤
 */
public class MyAuthorizationFilter extends PermissionsAuthorizationFilter {

	@Override
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {

        Subject subject = getSubject(request, response);
        String[] perms = (String[]) mappedValue;

        if(null == perms || perms.length == 0){
        	return true;
        }
        // 只要包含其中一标定的权限就可以通过
        for (String perm : perms) {
			if(subject.isPermitted(perm)){
				return true;
			}
		}

        return false;
    }
}
