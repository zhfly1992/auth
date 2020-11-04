/*
 * File name: UserRoleBaseQueryService.java
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

package com.run.authz.base.query;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 角色查询接口类
 * @author: zhabing
 * @version: 1.0, 2017年7月18日
 */

public interface UserRoleBaseQueryService {

	/**
	 * 
	 * @Description:分页查询角色信息
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getUserRoleInfoByPage(Map<String, String> pageInfo) throws Exception;



	/**
	 * 
	 * @Description:分页查询角色信息
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getAccUserRoleInfoByPage(JSONObject pageInfo) throws Exception;



	/**
	 * 
	 * @Description:分页查询接入方接口信息
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getAccInterfaceByPage(JSONObject pageInfo) throws Exception;



	/**
	 * 
	 * @Description:检查角色名称
	 * @param accessId
	 *            接入方id
	 * @param roleName
	 *            角色名称
	 * @return
	 */
	RpcResponse<Boolean> checkUserRoleName(String accessId, String roleName) throws Exception;



	/**
	 * 
	 * @Description:检查岗位名称
	 * @param accesstSecret
	 *            接入方秘钥
	 * @param roleName
	 *            角色名称
	 * @return
	 */
	RpcResponse<Boolean> checkOrgRoleName(String orgId, String roleName) throws Exception;



	/***
	 * 
	 * @Description:根据角色id查询已经被关联的用户id集合
	 * @param roleId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getUserIdByRoleId(String roleId) throws Exception;



	/**
	 * 
	 * @Description:根据角色id查询角色基本信息
	 * @param roleId
	 * @return
	 */
	RpcResponse<Map<String, Object>> getRoleMessById(String roleId) throws Exception;



	/**
	 * 
	 * @Description:分页查询当前用户的角色信息
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getUserRoleInfoByUserId(JSONObject pageInfo) throws Exception;



	/**
	 * 
	 * @Description:根据用户id查询用户所拥有的菜单权限
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getUserMenuByToken(String userId, String accessId) throws Exception;



	/**
	 * 
	 * @Description:根据组织id集合查询用户id集合
	 * @param orgIds
	 * @return
	 * @throws Exception
	 */
	List<String> getUserIdByOrg(List<String> orgIds) throws Exception;



	/**
	 * 
	 * @Description:根据人员id和接入方id查询相应的组织和角色信息
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List<Map> getRoleMessByUserId(String userId, String accessId);



	/**
	 * 
	 * @Description:根据人员id和接入方秘钥查询相应的组织和角色信息
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getRoleMessBySecret(String userId, String accessSecret);



	/**
	 * 
	 * @Description:校验角色组织用户是否存在
	 * @param roleInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Boolean> checkRoleUserExist(JSONObject roleInfo) throws Exception;



	/**
	 * 
	 * @Description:根据角色id查询角色所用有的菜单列表
	 * @param roleId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getRoleMenuById(String roleId, String applicationType);



	/**
	 * 
	 * @Description:检查接入方组织下面是否存在人员信息
	 * @param sourceInfo
	 * @return
	 */
	RpcResponse<Boolean> checkOrgUserExist(JSONObject orgInfo);



	/**
	 * 
	 * @Description:根据组织id查询角色集合
	 * @param roleId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getRoleListByOrgId(String orgId);



	/**
	 * @Description:根据角色id查询组织集合
	 * @param roleId
	 * @return RpcResponse<List<Map>>
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<Map> getOrgByRoleId(String roleId);



	/**
	 * 
	 * @Description:根据组织id查询该组织下能或不能接收短信的用户
	 * @param paramInfo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getUserListByOrgId(JSONObject paramInfo) throws Exception;



	/**
	 * @Description:根据组织id查询该组织以及父组织下能或不能接收短信的用户
	 * @param paramInfo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getAllUserListByOrgId(JSONObject paramInfo) throws Exception;



	/**
	 * 
	 * @Description: 根据角色名称模糊查询用户id集合
	 * @param roleName
	 *            角色名称
	 * @return
	 */
	List<String> getUserListByRoleName(String roleName, String accessId);

}
