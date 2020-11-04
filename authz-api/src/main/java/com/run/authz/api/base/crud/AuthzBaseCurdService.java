/*
 * File name: UserBaseCurdService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月21日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.api.base.crud;

import java.util.Map;

import com.run.entity.common.RpcResponse;

/**
 * @Description: tokencrud
 * @author: zhabing
 * @version: 1.0, 2017年6月21日
 */

public interface AuthzBaseCurdService {
	/**
	 * 
	 * @Description:创建redisCache
	 * @param userInfo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<Map> createRedisCache(String key, String value, Long timeOut) throws Exception;



	/**
	 * 
	 * @Description:删除token
	 * @param userInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Boolean> removeToken(String tokenId) throws Exception;



	/**
	 * 
	 * @Description:刷新redis失效时间
	 * @param token
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Boolean> refreshKey(String key,Long timeOut) throws Exception;
	
	/**
	 * 
	* @Description:获取value；
	* @param key
	* @return
	* @throws Exception
	 */
	RpcResponse<String> getValueByKey(String key) throws Exception;
	
	
	/**
	 * 
	 * @Description:将非对称加密生成的私钥放入redis
	 * @param key
	 * @param value
	 * @return
	 * @author :zh
	 * @version 2020年10月28日
	 */
	RpcResponse<Boolean> createRedisCacheForKey(String key,String value);

}
