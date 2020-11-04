/*
 * File name: WhileBlackBaseQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年8月16日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.base.query;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 黑白名单查询
 * @author: zhabing
 * @version: 1.0, 2017年8月16日
 */

public interface WhiteBlackBaseQueryService {
	/**
	 * 
	* @Description:根据黑白类型查询黑白名单列表
	* @param type while/black
	* @return
	 */
	RpcResponse<List<String>> getWhileBlackListByType(String type);
	
	/**
	 * 
	 * @Description:分页查询权限信息
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getWhiteBlackByPage(JSONObject pageInfo) throws Exception;
	
	/**
	 * 
	* @Description:检测黑白名单名称是否重复
	* @param name 名称是否重复
	* @return
	 */
	RpcResponse<Boolean> checkWhileBlackName(String name);
	
}
