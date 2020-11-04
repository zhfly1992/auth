/*
 * File name: UserRoleCrudRpcserImpl.java
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

package com.run.authz.base.curd.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import com.run.authz.api.base.crud.UserRoleBaseCrudService;
import com.run.authz.api.base.util.ExceptionChecked;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.api.constants.MongodbConstants;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.base.query.TenAccBaseQueryService;
import com.run.usc.base.util.MongoTemplateUtil;

/**
 * @Description: 角色rpc crud
 * @author: zhabing
 * @version: 1.0, 2017年7月18日
 */

public class UserRoleCrudRpcSerImpl implements UserRoleBaseCrudService {

	private static final Logger		logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private MongoTemplateUtil		tenementTemplateUtil;
	@Autowired
	private MongoTemplate			tenementTemplate;
	@Autowired
	private TenAccBaseQueryService	tenAccQuery;



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#saveUserRoleInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> saveUserRoleInfo(JSONObject roleInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "saveUserRoleInfo", roleInfo,
					AuthzConstants.TENEMENT_ACCESS_ID, AuthzConstants.ROLE_NAME);
			if (rs != null) {
				return rs;
			}

			// 接入方id
			String accessId = roleInfo.getString(AuthzConstants.TENEMENT_ACCESS_ID);
			// 重名校验
			if (nameCheck(accessId, roleInfo.getString(AuthzConstants.ROLE_NAME), null)) {
				logger.debug(String.format("[saveUserRoleInfo()->fail:%s]", AuthzConstants.ROLE_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.ROLE_SAVE_FAIL_NAME_EXITES);
			}

			// 插入用户表操作
			RpcResponse<JSONObject> res = tenementTemplateUtil.insert(logger, "saveUserRoleInfo", roleInfo,
					MongodbConstants.MONGODB_USERROLE_COLL);
			return res;

		} catch (Exception e) {
			logger.error("[saveUserRoleInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#saveUserRoleRsInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> saveUserRoleRsInfo(JSONObject roleInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "saveUserRoleRsInfo", roleInfo,
					AuthzConstants.ACCESS_SECRET, AuthzConstants.ROLE_NAME, AuthzConstants.ORG_MESS,
					AuthzConstants.PERMI_ARRAY);
			if (rs != null) {
				return rs;
			}

			// 根据接入方secret查询接入方id
			String accessSecret = roleInfo.getString(AuthzConstants.ACCESS_SECRET);
			RpcResponse<String> res = tenAccQuery.getAccessIdBySecret(accessSecret);
			String accessId = null;
			if (res.isSuccess()) {
				accessId = res.getSuccessValue();
			} else {
				logger.error(String.format("[saveUserRoleRsInfo()->error:%s]", res.getMessage()));
				return RpcResponseBuilder
						.buildErrorRpcResp(String.format("[saveUserRoleRsInfo()->error:%s]", res.getMessage()));
			}

			// 组织id数组
			JSONArray orgArray = roleInfo.getJSONArray(AuthzConstants.ORG_MESS);
			// 权限id数据
			JSONArray permiArray = roleInfo.getJSONArray(AuthzConstants.PERMI_ARRAY);

			if (orgArray == null || orgArray.isEmpty()) {
				logger.debug(String.format("[saveUserRoleRsInfo()->fail:%s--->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ORG_MESS));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[saveUserRoleRsInfo()->fail:%s--->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.ORG_MESS));
			}

			if (permiArray == null || permiArray.isEmpty()) {
				logger.debug(String.format("[saveUserRoleRsInfo()->fail:%s--->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.FUNC_ARRAY));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[saveUserRoleRsInfo()->fail:%s--->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.FUNC_ARRAY));
			}

			// 岗位id
			String roleId = UUID.randomUUID().toString().replace("-", "");

			// 保存岗位与组织的关系表
			List<JSONObject> listOrgs = new ArrayList<JSONObject>();
			for (Object org : orgArray) {
				JSONObject roleOrgjson = new JSONObject();
				String id = UUID.randomUUID().toString().replace("-", "");
				roleOrgjson.put(AuthzConstants.ID_, id);
				roleOrgjson.put(AuthzConstants.ROLE_ID, roleId);
				roleOrgjson.put(AuthzConstants.ORGANIZED_ID, org);
				listOrgs.add(roleOrgjson);
			}
			tenementTemplate.insert(listOrgs, MongodbConstants.ROLE_RS_ORGAN);

			// 保存岗位与权限的关系表
			List<JSONObject> listPermi = new ArrayList<JSONObject>();
			for (Object permi : permiArray) {
				JSONObject roleFuncjson = new JSONObject();
				String id = UUID.randomUUID().toString().replace("-", "");
				roleFuncjson.put(AuthzConstants.ID_, id);
				roleFuncjson.put(AuthzConstants.ROLE_ID, roleId);
				roleFuncjson.put(AuthzConstants.PERMI_ID, permi);
				listPermi.add(roleFuncjson);
			}
			tenementTemplate.insert(listPermi, MongodbConstants.PERMISSION_RS_ROLE);

			// 角色信息对象
			JSONObject roleJson = new JSONObject();
			roleJson.put(AuthzConstants.ROLE_NAME, roleInfo.get(AuthzConstants.ROLE_NAME));
			roleJson.put(AuthzConstants.TENEMENT_ACCESS_ID, accessId);
			roleJson.put(AuthzConstants.REMARK, roleInfo.get(AuthzConstants.REMARK));
			// 插入用户表操作
			RpcResponse<JSONObject> result = tenementTemplateUtil.insertId(logger, "saveUserRoleRsInfo", roleJson,
					MongodbConstants.MONGODB_USERROLE_COLL, roleId);

			return result;

		} catch (Exception e) {
			logger.error("[saveUserRoleRsInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#saveUserRoleRsInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> updateUserRoleRsInfo(JSONObject roleInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "updateUserRoleRsInfo", roleInfo,
					AuthzConstants.ROLE_ID, AuthzConstants.ACCESS_SECRET, AuthzConstants.ROLE_NAME,
					AuthzConstants.OLD_ORG_MESS, AuthzConstants.NEW_ORG_MESS, AuthzConstants.PERMI_ARRAY);
			if (rs != null) {
				return rs;
			}

			String roleId = roleInfo.getString(AuthzConstants.ROLE_ID);

			// 根据接入方secret查询接入方id
			String accessSecret = roleInfo.getString(AuthzConstants.ACCESS_SECRET);
			RpcResponse<String> res = tenAccQuery.getAccessIdBySecret(accessSecret);
			String accessId = null;
			if (res.isSuccess()) {
				accessId = res.getSuccessValue();
			} else {
				logger.error(String.format("[updateUserRoleRsInfo()->error:%s]", res.getMessage()));
				return RpcResponseBuilder
						.buildErrorRpcResp(String.format("[updateUserRoleRsInfo()->error:%s]", res.getMessage()));
			}

			// 重名校验
			if (nameCheck(accessId, roleInfo.getString(AuthzConstants.ROLE_NAME), roleId)) {
				logger.debug(
						String.format("[updateUserRoleRsInfo()->fail:%s]", AuthzConstants.ROLE_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.ROLE_SAVE_FAIL_NAME_EXITES);
			}

			/// 组织id数组
			JSONArray orgArray = roleInfo.getJSONArray(AuthzConstants.ORG_MESS);
			// 权限id数据
			JSONArray permiArray = roleInfo.getJSONArray(AuthzConstants.PERMI_ARRAY);

			if (orgArray == null || orgArray.isEmpty()) {
				logger.debug(String.format("[saveUserRoleRsInfo()->fail:%s--->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ORG_MESS));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[saveUserRoleRsInfo()->fail:%s--->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.ORG_MESS));
			}

			if (permiArray == null || permiArray.isEmpty()) {
				logger.debug(String.format("[saveUserRoleRsInfo()->fail:%s--->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.FUNC_ARRAY));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[saveUserRoleRsInfo()->fail:%s--->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.FUNC_ARRAY));
			}
			// 删除岗位与组织的关系
			Query reOrgQuery = new Query();
			reOrgQuery.addCriteria(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));
			tenementTemplate.remove(reOrgQuery, MongodbConstants.ROLE_RS_ORGAN);

			// 保存岗位与组织的关系表
			for (Object org : orgArray) {
				JSONObject roleOrgjson = new JSONObject();
				String roleOrgId = UUID.randomUUID().toString().replace("-", "");
				roleOrgjson.put(AuthzConstants.ID_, roleOrgId);
				roleOrgjson.put(AuthzConstants.ROLE_ID, roleId);
				roleOrgjson.put(AuthzConstants.ORGANIZED_ID, org);
				tenementTemplate.insert(roleOrgjson, MongodbConstants.ROLE_RS_ORGAN);
			}

			// 删除岗位与权限的关系
			Query reFuncQuery = new Query();
			reFuncQuery.addCriteria(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));
			tenementTemplate.remove(reFuncQuery, MongodbConstants.PERMISSION_RS_ROLE);

			// 保存岗位与权限的关系表
			List<JSONObject> listpermis = new ArrayList<JSONObject>();
			for (Object permi : permiArray) {
				JSONObject roleFuncjson = new JSONObject();
				String id = UUID.randomUUID().toString().replace("-", "");
				roleFuncjson.put(AuthzConstants.ID_, id);
				roleFuncjson.put(AuthzConstants.ROLE_ID, roleId);
				roleFuncjson.put(AuthzConstants.PERMI_ID, permi);
				listpermis.add(roleFuncjson);
			}
			tenementTemplate.insert(listpermis, MongodbConstants.PERMISSION_RS_ROLE);

			// 获取新的组织id数组和旧的组织id数组
			List<String> newOrgList = roleInfo.getJSONArray(AuthzConstants.NEW_ORG_MESS).toJavaList(String.class);
			List<String> oldOrgList = roleInfo.getJSONArray(AuthzConstants.OLD_ORG_MESS).toJavaList(String.class);
			List<String> delOrgIds = Lists.newArrayList();

			// 判断新的数组中是否包含旧的，并且将不包含的放入新的集合中
			for (String oldOrgId : oldOrgList) {
				if (!newOrgList.contains(oldOrgId)) {
					delOrgIds.add(oldOrgId);
				}
			}

			if (delOrgIds.size() != 0) {
				// 删除RoleUserRs表中的数据
				Query delRoleUserRsQuery = new Query();
				delRoleUserRsQuery.addCriteria(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));
				delRoleUserRsQuery.addCriteria(Criteria.where(AuthzConstants.ORGANIZED_ID).in(delOrgIds));
				tenementTemplate.remove(delRoleUserRsQuery, MongodbConstants.ROLE_RS_USER);
			}

			// 角色信息对象
			JSONObject roleJson = new JSONObject();
			roleJson.put(AuthzConstants.ROLE_NAME, roleInfo.get(AuthzConstants.ROLE_NAME));
			roleJson.put(AuthzConstants.TENEMENT_ACCESS_ID, accessId);
			roleJson.put(AuthzConstants.REMARK, roleInfo.get(AuthzConstants.REMARK));
			// 修改用户表操作
			RpcResponse<JSONObject> result = tenementTemplateUtil.update(logger, "updateUserRoleRsInfo", roleJson,
					MongodbConstants.MONGODB_USERROLE_COLL, roleId);

			return result;

		} catch (Exception e) {
			logger.error("[updateUserRoleRsInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#updateUserRoleInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> updateUserRoleInfo(JSONObject roleInfo) throws Exception {
		try {
			// 参数有效性校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "updateUserRoleInfo", roleInfo,
					AuthzConstants.TENEMENT_ACCESS_ID, AuthzConstants.ROLE_NAME, AuthzConstants.ID_);
			if (rs != null) {
				return rs;
			}

			// 修改操作
			String id = roleInfo.getString(AuthzConstants.ID_);

			// 接入方id
			String accessId = roleInfo.getString(AuthzConstants.TENEMENT_ACCESS_ID);
			// 重名校验
			if (nameCheck(accessId, roleInfo.getString(AuthzConstants.ROLE_NAME), id)) {
				logger.debug(
						String.format("[updateUserRoleInfo()->fail:%s]", AuthzConstants.ROLE_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.ROLE_SAVE_FAIL_NAME_EXITES);
			}
			return tenementTemplateUtil.update(logger, "updateUserRoleInfo", roleInfo,
					MongodbConstants.MONGODB_USERROLE_COLL, id);

		} catch (Exception e) {
			logger.error("[updateUserRoleInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#deleteUserRoleInfo(java.util.List)
	 */
	@Override
	public RpcResponse<JSONArray> deleteUserRoleInfo(JSONArray ids) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(ids) || ids.isEmpty()) {
				logger.error(String.format("[deleteUserRoleInfo()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.IDS));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			RpcResponse<JSONArray> res = tenementTemplateUtil.delete(logger, "deleteAccUserInfo",
					MongodbConstants.MONGODB_USERROLE_COLL, ids);
			if (res.isSuccess()) {
				// 删除角色下与人员的关系表
				Query query = new Query();
				query.addCriteria(Criteria.where(AuthzConstants.ROLE_ID).in(ids));
				tenementTemplate.remove(query, MongodbConstants.ROLE_RS_USER);
			}

			return res;
		} catch (Exception e) {
			logger.error("[updateAccessInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:用户角色重名验证
	 * @param accessId
	 *            接入方id
	 * @param roleName
	 *            角色名称
	 * @param silfId
	 *            自身id
	 * @return
	 */
	private boolean nameCheck(String accessId, String roleName, String silfId) {
		// 角色重名校验
		Query queryRole = new Query();
		queryRole.addCriteria(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));
		queryRole.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
		queryRole.addCriteria(Criteria.where(AuthzConstants.ROLE_NAME).is(roleName));

		if (!StringUtils.isEmpty(silfId)) {
			queryRole.addCriteria(Criteria.where(AuthzConstants.ID_).nin(silfId));
		}
		return tenementTemplate.exists(queryRole, MongodbConstants.MONGODB_USERROLE_COLL);
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#addRoleRsMenu(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> addRoleRsUser(JSONArray roleIds, JSONArray userIds, String accessId) {
		try {
			// 参数有效性校验
			if (roleIds.isEmpty() || userIds.isEmpty()) {
				logger.error(String.format("[addRoleRsUser()->error:%s]", AuthzConstants.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 添加用户与角色的关系
			for (int i = 0; i < userIds.size(); i++) {
				// 用户id
				String userId = userIds.getString(i);
				for (int j = 0; j < roleIds.size(); j++) {
					// 角色id
					String roleId = roleIds.getString(j);
					// 首先查询数据库中是否已经存在了（应对多窗口同时操作的情况）
					Query query = new Query(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));
					query.addCriteria(Criteria.where(AuthzConstants.USER_ID).is(userId));
					Boolean checkExist = tenementTemplate.exists(query, MongodbConstants.ROLE_RS_USER);
					// 如果没有则插入用户与角色的关系表
					if (!checkExist) {
						JSONObject rsObj = new JSONObject();
						rsObj.put(AuthzConstants.ID_, UUID.randomUUID().toString());
						rsObj.put(AuthzConstants.USER_ID, userId);
						rsObj.put(AuthzConstants.ROLE_ID, roleId);
						if (StringUtils.isEmpty(accessId)) {
							RpcResponse<JSONObject> res = tenementTemplateUtil.getModelById(logger, "addRoleRsUser",
									MongodbConstants.MONGODB_USERROLE_COLL, roleId);
							if (res.isSuccess()) {
								accessId = res.getSuccessValue().getString(AuthzConstants.TENEMENT_ACCESS_ID);
							}
						}
						rsObj.put(AuthzConstants.TENEMENT_ACCESS_ID, accessId);
						tenementTemplate.insert(rsObj, MongodbConstants.ROLE_RS_USER);
					}
				}
			}

			logger.info(String.format("[addRoleRsUser()->success:%s]", AuthzConstants.SAVE_SUCC));
			return RpcResponseBuilder.buildSuccessRpcResp(
					String.format("[addRoleRsUser()->success:%s]", AuthzConstants.SAVE_SUCC), null);

		} catch (Exception e) {
			logger.error("[addRoleRsUser()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#addRoleRsMenu(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> addOrgRsUser(JSONArray orgIds, JSONArray userIds, String accessId) {
		try {
			// 参数有效性校验
			if (orgIds.isEmpty() || userIds.isEmpty() || StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[addOrgRsUser()->error:%s]", AuthzConstants.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 添加用户与角色的关系
			for (int i = 0; i < userIds.size(); i++) {
				// 用户id
				String userId = userIds.getString(i);
				for (int j = 0; j < orgIds.size(); j++) {
					// 角色id
					String roleId = orgIds.getString(j);
					// 首先查询数据库中是否已经存在了（应对多窗口同时操作的情况）
					Query query = new Query(Criteria.where(AuthzConstants.ORGANIZED_ID).is(roleId));
					query.addCriteria(Criteria.where(AuthzConstants.USER_ID).is(userId));
					Boolean checkExist = tenementTemplate.exists(query, MongodbConstants.ROLE_RS_ORGAN);
					// 如果没有则插入用户与角色的关系表
					if (!checkExist) {
						JSONObject rsObj = new JSONObject();
						rsObj.put(AuthzConstants.ID_, UUID.randomUUID().toString());
						rsObj.put(AuthzConstants.USER_ID, userId);
						rsObj.put(AuthzConstants.ROLE_ID, roleId);
						tenementTemplate.insert(rsObj, MongodbConstants.ROLE_RS_USER);
					}
				}
			}

			logger.info(String.format("[addOrgRsUser()->success:%s]", AuthzConstants.SAVE_SUCC));
			return RpcResponseBuilder
					.buildSuccessRpcResp(String.format("[addOrgRsUser()->success:%s]", AuthzConstants.SAVE_SUCC), null);

		} catch (Exception e) {
			logger.error("[addOrgRsUser()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#addRoleRsMenu(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> delRoleRsUser(JSONArray roleIds, JSONArray userIds) {
		try {
			// 参数有效性校验
			if (roleIds.isEmpty() || userIds.isEmpty()) {
				logger.error(String.format("[delRoleRsUser()->error:%s]", AuthzConstants.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 删除用户与角色的关系
			Query query = new Query(Criteria.where(AuthzConstants.ROLE_ID).in(roleIds));
			query.addCriteria(Criteria.where(AuthzConstants.USER_ID).in(userIds));
			WriteResult res = tenementTemplate.remove(query, MongodbConstants.ROLE_RS_USER);
			if (res.getN() > 0) {
				logger.info(String.format("[delRoleRsUser()->success:%s]", AuthzConstants.DEL_SUCC));
				return RpcResponseBuilder.buildSuccessRpcResp(
						String.format("[delRoleRsUser()->success:%s]", AuthzConstants.DEL_SUCC), null);
			} else {
				logger.info(String.format("[delRoleRsUser()->fail:%s", AuthzConstants.DEL_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.DEL_FAIL);
			}
		} catch (Exception e) {
			logger.error("[delRoleRsUser()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#saveUserRoleRs(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> saveUserRoleRs(JSONObject roleInfo) throws Exception {
		try {
			// 参数有效性校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "updateUserRoleInfo", roleInfo,
					AuthzConstants.TENEMENT_ACCESS_ID, AuthzConstants.ROLE_ID, AuthzConstants.USER_ID);
			if (rs != null) {
				return rs;
			}
			roleInfo.put(AuthzConstants.ID_, UUID.randomUUID().toString().replace("-", ""));
			tenementTemplate.insert(roleInfo, MongodbConstants.ROLE_RS_USER);
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.SAVE_SUCC, roleInfo);
		} catch (Exception e) {
			logger.error("[saveUserRoleRs()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#saveUserRoleRs(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> delUserRoleRs(String userId) throws Exception {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(userId)) {
				logger.error(String.format("[delUserRoleRs()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[delUserRoleRs()->error:%s-->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.USER_ID));
			}

			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.USER_ID).is(userId));
			tenementTemplate.remove(query, MongodbConstants.ROLE_RS_USER);
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.DEL_SUCC, userId);
		} catch (Exception e) {
			logger.error("[saveUserRoleRs()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#swateUserRoleState(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<String> swateUserRoleState(String roleId, String state) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(roleId)) {
				logger.error(String.format("[swateUserRoleState()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ROLE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			if (StringUtils.isEmpty(state)) {
				logger.error(String.format("[deleteUserRoleInfo()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.STATE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 修改岗位状态
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.ID_).is(roleId));
			Update update = new Update();
			update.set(AuthzConstants.STATE, state);
			WriteResult res = tenementTemplate.updateMulti(query, update, MongodbConstants.MONGODB_USERROLE_COLL);
			if (res.getN() > 0) {
				logger.info(String.format("[swateUserRoleState()->success:%s]", AuthzConstants.UPDATE_SUCC));
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.UPDATE_SUCC, roleId);
			} else {
				logger.error(String.format("[swateUserRoleState()->error:%s]", AuthzConstants.UPDATE_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.UPDATE_FAIL);
			}

		} catch (Exception e) {
			logger.error("[swateUserRoleState()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#delUserRoleRs(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<String> delUserRoleRs(String userId, String accessSecret) throws Exception {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(userId)) {
				logger.error(String.format("[delUserRoleRs()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[delUserRoleRs()->error:%s-->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.USER_ID));
			}

			if (StringUtils.isEmpty(accessSecret)) {
				logger.error(String.format("[delUserRoleRs()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[delUserRoleRs()->error:%s-->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.ACCESS_SECRET));
			}

			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.USER_ID).is(userId).and(AuthzConstants.TENEMENT_ACCESS_ID)
					.is(accessSecret));
			tenementTemplate.remove(query, MongodbConstants.ROLE_RS_USER);
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.DEL_SUCC, userId);
		} catch (Exception e) {
			logger.error("[saveUserRoleRs()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#updateUserAsOrgInfo(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<String> updateUserAsOrgInfo(String accessSecret, String sourceName, String orgId)
			throws Exception {
		logger.info(
				String.format("[updateUserAsOrgInfo()-> rpc param : accessSecret = %s , sourceName = %s , orgId = %s]",
						accessSecret, sourceName, orgId));
		try {

			if (StringUtils.isEmpty(accessSecret) || StringUtils.isEmpty(sourceName) || StringUtils.isEmpty(orgId)) {
				logger.error(String.format("[updateUserAsOrgInfo()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						"接入方密钥或者组织名称,组织Id为null!"));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[updateUserAsOrgInfo()->error:%s-->%s]",
						AuthzConstants.NO_BUSINESS, "接入方密钥或者组织名称,组织Id为null!"));
			}

			BasicDBObject basicDBObject = new BasicDBObject();
			basicDBObject.put("$set", new BasicDBObject(AuthzConstants.SOURCE_NAME, sourceName));
			Update update = new BasicUpdate(basicDBObject);
			tenementTemplate
					.updateMulti(
							new Query(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessSecret)
									.and(AuthzConstants.ORGANIZED_ID).is(orgId)),
							update, MongodbConstants.ROLE_RS_USER);
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.DEL_SUCC, "true");
		} catch (Exception e) {
			logger.error("[updateUserAsOrgInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}
}
