/*
 * File name: PermiBaseQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年8月29日 ... ...
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
 * @Description: 权限查询接口
 * @author: zhabing
 * @version: 1.0, 2017年8月29日
 */

public interface PermiBaseQueryService {

	/**
	 * 
	 * @Description:分页查询权限信息
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getPermiInfoByPage(JSONObject pageInfo) throws Exception;



	/**
	 * 
	 * @Description:分页查询角色已經擁有或者未擁有权限信息
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getRolePermissionByPage(JSONObject pageInfo) throws Exception;

	/**
	 * 
	 * @Description:分页查询接入方已經擁有或者未擁有权限信息
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getAccessPermissionByPage(JSONObject pageInfo) throws Exception;

	/**
	 * 
	 * @Description:分页查询角色已經擁有或者未擁有权限信息
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getRolePermiByPage(JSONObject pageInfo) throws Exception;



	/**
	 * 
	 * @Description:分页查询角色已經擁有或者未擁有权限信息
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getFuncPermissionByPage(JSONObject pageInfo) throws Exception;



	/**
	 * 
	 * @Description:校验权限名称是否重名
	 * @param accessId
	 * @param premiName
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Boolean> checkPermiName(String accessId, String premiName) throws Exception;



	/***
	 * 
	 * @Description:根据用户id查询用户所拥有的接口权限
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getInterListByUserId(String userId) throws Exception;



	/**
	 * 
	 * @Description:通过角色id获取功能项信息
	 * @param roleId觉角色id
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getItemInfoByPermisId(String permisId) throws Exception;



	/**
	 * @Description:查询该角色下与之关联的权限id集合
	 * @param roleId
	 * @return
	 * @throws Exception
	 */
	RpcResponse<List<String>> getPermiIdIdByRoleId(String roleId) throws Exception;
	
	/**
	 * 
	* @Description:校验角色是否正常
	* @param roleId
	* @return
	* @throws Exception
	 */
	Boolean checkRoleNormal(String roleId) throws Exception;

}
