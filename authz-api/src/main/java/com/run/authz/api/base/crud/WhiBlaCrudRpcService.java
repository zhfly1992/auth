/*
* File name: WhiBlaCrudRpcService.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			zhabing		2017年9月6日
* ...			...			...
*
***************************************************/

package com.run.authz.api.base.crud;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
* @Description:	黑白名单接口crud类
* @author: zhabing
* @version: 1.0, 2017年9月6日
*/

public interface WhiBlaCrudRpcService {
	/**
	 * 
	 * @Description:保存黑白名单
	 * @param whiteBlackInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> addWhiteBlack(JSONObject whiteBlackInfo) throws Exception;
	
	/**
	 * 
	 * @Description:修改黑白名单
	 * @param whiteBlackInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> updateWhiteBlack(JSONObject whiteBlackInfo) throws Exception;
	
	/**
	 * 
	 * @Description:删除黑白名单
	 * @param whiteBlackInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONArray> delWhiteBlack(JSONArray ids) throws Exception;
	

}
