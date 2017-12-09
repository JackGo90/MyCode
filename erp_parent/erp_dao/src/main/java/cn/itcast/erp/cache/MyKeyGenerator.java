package cn.itcast.erp.cache;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

public class MyKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		//调用的类名，去包名 
		String targetClassName = target.getClass().getSimpleName();
		// 拼接key值
		String key = targetClassName + "_" + params[0];
		//System.out.println(key);
		return key;
	}

}
