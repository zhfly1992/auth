package com.run.tool.base.rpc.filter;


import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;

/**
 * 
 * 接口调用权限统一拦截认证
 * 
 * @author: zhabing
 * @version: 1.0, 2016年12月9日
 */
public class AuthzFilter implements Filter {

	
	
	/**
	 * @see com.alibaba.dubbo.rpc.Filter#invoke(com.alibaba.dubbo.rpc.Invoker, com.alibaba.dubbo.rpc.Invocation)
	 */
	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) {
		try {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("11111111111111111111111111111111--------------------!!!!!!!!!!!!!!!!!");
		// TODO Auto-generated method stub
		return invoker.invoke(invocation);
		/*return null;*/
	}
	

}
