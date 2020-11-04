/*
 * File name: UserBaseQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月22日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.api.filter;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 用户中心查询
 * @author: zhabing
 * @version: 1.0, 2017年6月22日
 */
@SuppressWarnings("rawtypes")
public interface FilterBaseQueryService {

	/**
	 * 
	 * @Description:验证用户token
	 * @param authInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse authenticate(JSONObject authInfo) throws Exception;



	/**
	 * 
	 * @Description:根据token得到用户id
	 * @param tokenInfo
	 * @return
	 */
	RpcResponse getUserId(String tokenId);

}
