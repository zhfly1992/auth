/*
 * File name: UserRoleBaseCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月18日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.api.base.crud;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 角色crud接口类
 * @author: zhabing
 * @version: 1.0, 2017年7月18日
 */

public interface UserRoleBaseCrudService {

	/**
	 * 
	 * @Description:保存角色信息
	 * @param roleInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> saveUserRoleInfo(JSONObject roleInfo) throws Exception;



	/**
	 * 
	 * @Description:保存角色信息以及他的关联信息
	 * @param roleInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> saveUserRoleRsInfo(JSONObject roleInfo) throws Exception;



	/**
	 * 
	 * @Description:保存角色信息以及他的关联信息
	 * @param roleInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> updateUserRoleRsInfo(JSONObject roleInfo) throws Exception;



	/**
	 * 
	 * @Description:修改角色信息
	 * @param roleInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> updateUserRoleInfo(JSONObject roleInfo) throws Exception;



	/**
	 * 
	 * @Description:批量删除角色信息
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONArray> deleteUserRoleInfo(JSONArray ids) throws Exception;



	/**
	 * 
	 * @Description:保存接入方角色与接入方用户的关联信息
	 * @param roleMenuInfo
	 * @return
	 */
	RpcResponse<String> addRoleRsUser(JSONArray roleIds, JSONArray userIds, String accessId);



	/**
	 * 
	 * @Description:保存接入方组织与接入方用户的关联信息
	 * @param roleMenuInfo
	 * @return
	 */
	RpcResponse<String> addOrgRsUser(JSONArray roleIds, JSONArray userIds, String accessId);



	/**
	 * 
	 * @Description:批量删除接入方角色与接入方用户的关联信息
	 * @param roleMenuInfo
	 * @return
	 */
	RpcResponse<String> delRoleRsUser(JSONArray roleIds, JSONArray userIds);



	/**
	 * 
	 * @Description:添加用户与角色的关联信息
	 * @param roleInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> saveUserRoleRs(JSONObject roleInfo) throws Exception;



	/**
	 * 
	 * @Description:删除用户与角色的关联信息
	 * @param roleInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> delUserRoleRs(String userId) throws Exception;



	/**
	 * 
	 * @Description:禁用或者启用角色状态
	 * @param roleId
	 * @return
	 */
	RpcResponse<String> swateUserRoleState(String roleId, String state);



	/**
	 * 
	 * @Description:删除用户与角色的关联信息
	 * @param roleInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> delUserRoleRs(String userId, String accessSecret) throws Exception;



	/**
	 * 
	 * @Description:根据接入方密钥以及组织Id查询RoleUserAs表中数据并且修改sourceName名称
	 * @param accessSecret
	 * @param sourceName
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> updateUserAsOrgInfo(String accessSecret, String sourceName, String orgId) throws Exception;

}
