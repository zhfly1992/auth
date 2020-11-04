/*
 * File name: UserBaseQueryRpcServiceImpl.java
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

package com.run.filter.service.query;

import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.filter.FilterBaseQueryService;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 权限中心token查询服务
 * @author: zhabing
 * @version: 1.0, 2017年6月22日
 */

public class FilterBaseQueryRpcServiceImpl implements FilterBaseQueryService {

	/**
	 * @see com.run.authz.api.filter.FilterBaseQueryService#authenticate(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<?> authenticate(JSONObject authInfo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	/**
	 * @see com.run.authz.api.filter.FilterBaseQueryService#getUserId(java.lang.String)
	 */
	@Override
	public RpcResponse<?> getUserId(String tokenId) {
		// TODO Auto-generated method stub
		return null;
	}

}
