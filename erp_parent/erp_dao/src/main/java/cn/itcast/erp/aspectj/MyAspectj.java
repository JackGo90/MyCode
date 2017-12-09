package cn.itcast.erp.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class MyAspectj {
	
		//定义切面拦截的规则
		@Around("execution(* cn.itcast.erp.dao.impl.*.get(Long))")
		public Object around(ProceedingJoinPoint point) throws Throwable{
			System.out.println(point);
			System.out.println(point.getClass());
			System.out.println(point.getKind());
			System.out.println(point.getSignature());
			System.out.println(point.getSourceLocation());
			System.out.println(point.getStaticPart());
			System.out.println(point.getThis());
			System.out.println(point.getTarget());
			return point.proceed();
		}
}
