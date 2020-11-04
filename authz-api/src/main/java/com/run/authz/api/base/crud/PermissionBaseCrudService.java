/*
 * File name: PermissionBaseCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年8月28日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.api.base.crud;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 权限中心接口
 * @author: zhabing
 * @version: 1.0, 2017年8月28日
 */

public interface PermissionBaseCrudService {
	/**
	 * 
	 * @Description:保存权限信息
	 * @param roleInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> savePermissionInfo(JSONObject perInfo) throws Exception;



	/**
	 * 
	 * @Description:修改权限信息
	 * @param roleInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> updatePermissionInfo(JSONObject perInfo) throws Exception;



	/**
	 * 
	 * @Description:删除权限信息
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONArray> deletePermiInfo(JSONArray ids) throws Exception;



	/**
	 * 
	 * @Description:添加角色与权限的关联
	 * @param permiArray
	 * @param roleArray
	 * @return
	 * @throws Exception
	 */
	RpcResponse<List<JSONObject>> addPermiRsRole(JSONArray permiArray, JSONArray roleArray) throws Exception;



	/**
	 * 
	 * @Description:添加角色与权限的解除
	 * @param permiArray
	 * @param roleArray
	 * @return
	 * @throws Exception
	 */
	RpcResponse<List<JSONObject>> delPermiRsRole(JSONArray permiArray, JSONArray roleArray) throws Exception;



	/**
	 * 
	 * @Description:添加功能项与权限的关联
	 * @param permiArray
	 * @param funcArray
	 * @return
	 * @throws Exception
	 */
	RpcResponse<List<JSONObject>> addPermiRsFunc(JSONArray permiArray, JSONArray funcArray) throws Exception;



	/**
	 * 
	 * @Description:添加功能项与权限的解除
	 * @param permiArray
	 * @param funcArray
	 * @return
	 * @throws Exception
	 */
	RpcResponse<List<JSONObject>> delPermiRsFunc(JSONArray permiArray, JSONArray funcArray) throws Exception;



	/**
	 * 
	 * @Description:绑定功能项
	 * @param powerId
	 *            角色id
	 * @param itemId
	 *            功能项id
	 * @return
	 * @throws Exception
	 */
	RpcResponse<List<JSONObject>> permisBindFunc(String powerId, List<String> itemId) throws Exception;



	/**
	 * 
	 * @Description:接入方绑定
	 * @param accessId
	 *            接入方id
	 * @param permiIds
	 *            权限id集合
	 * @return
	 * @throws Exception
	 */
	RpcResponse<List<JSONObject>> addAccessRsPermi(String accessId, JSONArray permiIds) throws Exception;



	/**
	 * 
	 * @Description:默认给接入方授权权限信息
	 * @param accessId
	 * @return
	 * @throws Exception
	 */
	RpcResponse<List<JSONObject>> addAccessRsPermi(String accessId, String accessType) throws Exception;



	/**
	 * 
	 * @Description:接入方绑定
	 * @param accessId
	 *            接入方id
	 * @param permiIds
	 *            权限id集合
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Integer> delAccessRsPermi(String accessId, JSONArray permiIds) throws Exception;

}
