/*
 * File name: PermiBaseCrudRpcImpl.java
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

package com.run.authz.base.curd.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.run.authz.api.base.crud.PermissionBaseCrudService;
import com.run.authz.api.base.util.ExceptionChecked;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.api.constants.MongodbConstants;
import com.run.common.util.UUIDUtil;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.util.MongoTemplateUtil;

/**
 * @Description:权限crud实现类
 * @author: zhabing
 * @version: 1.0, 2017年8月28日
 */

public class PermiBaseCrudRpcImpl implements PermissionBaseCrudService {

	private static final Logger	logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private MongoTemplateUtil	mongoTemplateUtil;
	@Autowired
	private MongoTemplate		mongoTemplate;



	/**
	 * @see com.run.authz.api.base.crud.PermissionBaseCrudService#savePermissionInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> savePermissionInfo(JSONObject perInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "savePermissionInfo", perInfo,
					AuthzConstants.ACCESS_TYPE, AuthzConstants.PERMI_NAME, AuthzConstants.APPLICATION_TYPE);
			if (rs != null) {
				return rs;
			}

			// 接入方id
			String accessType = perInfo.getString(AuthzConstants.ACCESS_TYPE);

			// 应用类型
			String applicationType = perInfo.getString(AuthzConstants.APPLICATION_TYPE);

			// 重名校验
			if (nameCheck(accessType, applicationType, perInfo.getString(AuthzConstants.PERMI_NAME), null)) {
				logger.debug(
						String.format("[savePermissionInfo()->fail:%s]", AuthzConstants.PER_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.PER_SAVE_FAIL_NAME_EXITES);
			}

			// 插入权限表操作
			RpcResponse<JSONObject> res = mongoTemplateUtil.insert(logger, "savePermissionInfo", perInfo,
					MongodbConstants.PERMISSION_COLL);
			return res;

		} catch (Exception e) {
			logger.error("[savePermissionInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:接入方权限重名验证
	 * @param accessId
	 *            接入方id
	 * @param permiName
	 *            权限名称
	 * @param silfId
	 *            自身id
	 * @return
	 */
	private boolean nameCheck(String accessType, String applicationType, String permiName, String silfId) {
		// 角色重名校验
		Query queryRole = new Query();
		queryRole.addCriteria(Criteria.where(AuthzConstants.ACCESS_TYPE).is(accessType));
		queryRole.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
		queryRole.addCriteria(Criteria.where(AuthzConstants.PERMI_NAME).is(permiName));

		// 区分应用类型
		if (AuthzConstants.APP.equals(applicationType)) {
			queryRole.addCriteria(Criteria.where(AuthzConstants.APPLICATION_TYPE).is(applicationType));
		} else if (AuthzConstants.PC.equals(applicationType)) {
			queryRole.addCriteria(Criteria.where(AuthzConstants.APPLICATION_TYPE).in(applicationType, null));
		}

		if (!StringUtils.isEmpty(silfId)) {
			queryRole.addCriteria(Criteria.where(AuthzConstants.ID_).nin(silfId));
		}
		return mongoTemplate.exists(queryRole, MongodbConstants.PERMISSION_COLL);
	}



	/**
	 * @see com.run.authz.api.base.crud.PermissionBaseCrudService#updatePermissionInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> updatePermissionInfo(JSONObject perInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "updatePermissionInfo", perInfo,
					AuthzConstants.ACCESS_TYPE, AuthzConstants.PERMI_NAME, AuthzConstants.ID_,
					AuthzConstants.APPLICATION_TYPE);
			if (rs != null) {
				return rs;
			}

			String id = perInfo.getString(AuthzConstants.ID_);
			// 接入方类型
			String accessType = perInfo.getString(AuthzConstants.ACCESS_TYPE);

			String permiName = perInfo.getString(AuthzConstants.PERMI_NAME);

			// 应用类型
			String applicationType = perInfo.getString(AuthzConstants.APPLICATION_TYPE);
			// 重名校验
			if (nameCheck(accessType, applicationType, permiName, id)) {
				logger.debug(
						String.format("[updatePermissionInfo()->fail:%s]", AuthzConstants.ROLE_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.ROLE_SAVE_FAIL_NAME_EXITES);
			}

			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.ID_).is(id));

			Update update = new Update();
			update.set(AuthzConstants.PERMI_NAME, permiName);

			// 修改权限表操作
			RpcResponse<JSONObject> res = mongoTemplateUtil.update(logger, "updatePermissionInfo", perInfo,
					MongodbConstants.PERMISSION_COLL, id);
			return res;

		} catch (Exception e) {
			logger.error("[updatePermissionInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#deleteUserRoleInfo(java.util.List)
	 */
	@Override
	public RpcResponse<JSONArray> deletePermiInfo(JSONArray ids) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(ids) || ids.isEmpty()) {
				logger.error(String.format("[deletePermiInfo()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.IDS));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			RpcResponse<JSONArray> res = mongoTemplateUtil.delete(logger, "deletePermiInfo",
					MongodbConstants.PERMISSION_COLL, ids);
			return res;
		} catch (Exception e) {
			logger.error("[deletePermiInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.PermissionBaseCrudService#addPermiRsRole(com.alibaba.fastjson.JSONArray,
	 *      com.alibaba.fastjson.JSONArray)
	 */
	@Override
	public RpcResponse<List<JSONObject>> addPermiRsRole(JSONArray permiArray, JSONArray roleArray) throws Exception {
		try {
			// 参数必填字段校验
			if (permiArray == null || permiArray.isEmpty()) {
				logger.error(String.format("[addPermiRsRole()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PERMI_ARRAY));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			if (roleArray == null || roleArray.isEmpty()) {
				logger.error(String.format("[addPermiRsRole()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ROLE_ARRAY));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			List<JSONObject> listObj = new ArrayList<>();

			for (Object permiId : permiArray) {
				for (Object roleId : roleArray) {
					// 判断数据库中是否存在这条数据
					Query query = new Query();
					query.addCriteria(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));
					query.addCriteria(Criteria.where(AuthzConstants.PERMI_ID).is(permiId));
					Boolean check = mongoTemplate.exists(query, MongodbConstants.PERMISSION_RS_ROLE);
					if (!check) {
						JSONObject newObj = new JSONObject();
						newObj.put(AuthzConstants.ID_, UUID.randomUUID().toString().replace("-", ""));
						newObj.put(AuthzConstants.ROLE_ID, roleId);
						newObj.put(AuthzConstants.PERMI_ID, permiId);
						listObj.add(newObj);
					}
				}
			}
			mongoTemplate.insert(listObj, MongodbConstants.PERMISSION_RS_ROLE);
			logger.info(String.format("[addPermiRsRole()->succ:%s]", AuthzConstants.SAVE_SUCC));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.SAVE_SUCC, listObj);

		} catch (Exception e) {
			logger.error("[savePermissionInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.PermissionBaseCrudService#delPermiRsRole(com.alibaba.fastjson.JSONArray,
	 *      com.alibaba.fastjson.JSONArray)
	 */
	@Override
	public RpcResponse<List<JSONObject>> delPermiRsRole(JSONArray permiArray, JSONArray roleArray) throws Exception {
		try {
			// 参数必填字段校验
			if (permiArray == null || permiArray.isEmpty()) {
				logger.error(String.format("[delPermiRsRole()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PERMI_ARRAY));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			if (roleArray == null || roleArray.isEmpty()) {
				logger.error(String.format("[delPermiRsRole()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ROLE_ARRAY));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			int i = 0;
			for (Object permiId : permiArray) {
				for (Object roleId : roleArray) {
					// 删除数据
					Query query = new Query();
					query.addCriteria(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));
					query.addCriteria(Criteria.where(AuthzConstants.PERMI_ID).is(permiId));
					WriteResult res = mongoTemplate.remove(query, MongodbConstants.PERMISSION_RS_ROLE);
					i = i + res.getN();
				}
			}

			if (i > 0) {
				logger.info(String.format("[delPermiRsRole()->succ:%s]", AuthzConstants.DEL_SUCC));
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.DEL_SUCC, null);
			} else {
				logger.error(String.format("[delPermiRsRole()->error:%s]", AuthzConstants.DEL_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.DEL_FAIL);
			}
		} catch (Exception e) {
			logger.error("[delPermiRsRole()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.PermissionBaseCrudService#addPermiRsRole(com.alibaba.fastjson.JSONArray,
	 *      com.alibaba.fastjson.JSONArray)
	 */
	@Override
	public RpcResponse<List<JSONObject>> addPermiRsFunc(JSONArray permiArray, JSONArray funcArray) throws Exception {
		try {
			// 参数必填字段校验
			if (permiArray == null || permiArray.isEmpty()) {
				logger.error(String.format("[addPermiRsFunc()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PERMI_ARRAY));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			if (funcArray == null || funcArray.isEmpty()) {
				logger.error(String.format("[addPermiRsFunc()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.FUNC_ARRAY));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			List<JSONObject> listObj = new ArrayList<>();

			for (Object permiId : permiArray) {
				for (Object funcId : funcArray) {
					// 判断数据库中是否存在这条数据
					Query query = new Query();
					query.addCriteria(Criteria.where(AuthzConstants.FUNC_ID).is(funcId));
					query.addCriteria(Criteria.where(AuthzConstants.PERMI_ID).is(permiId));
					Boolean check = mongoTemplate.exists(query, MongodbConstants.PERMISSION_RS_FUNC);
					if (!check) {
						JSONObject newObj = new JSONObject();
						newObj.put(AuthzConstants.ID_, UUID.randomUUID().toString().replace("-", ""));
						newObj.put(AuthzConstants.FUNC_ID, funcId);
						newObj.put(AuthzConstants.PERMI_ID, permiId);
						listObj.add(newObj);
					}
				}
			}
			mongoTemplate.insert(listObj, MongodbConstants.PERMISSION_RS_FUNC);
			logger.info(String.format("[addPermiRsFunc()->succ:%s]", AuthzConstants.SAVE_SUCC));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.SAVE_SUCC, listObj);

		} catch (Exception e) {
			logger.error("[addPermiRsFunc()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.PermissionBaseCrudService#delPermiRsRole(com.alibaba.fastjson.JSONArray,
	 *      com.alibaba.fastjson.JSONArray)
	 */
	@Override
	public RpcResponse<List<JSONObject>> delPermiRsFunc(JSONArray permiArray, JSONArray funcArray) throws Exception {
		try {
			// 参数必填字段校验
			if (permiArray == null || permiArray.isEmpty()) {
				logger.error(String.format("[delPermiRsFunc()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PERMI_ARRAY));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			if (funcArray == null || funcArray.isEmpty()) {
				logger.error(String.format("[delPermiRsFunc()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.FUNC_ARRAY));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			int i = 0;
			for (Object permiId : permiArray) {
				for (Object funcId : funcArray) {
					// 删除数据
					Query query = new Query();
					query.addCriteria(Criteria.where(AuthzConstants.FUNC_ID).is(funcId));
					query.addCriteria(Criteria.where(AuthzConstants.PERMI_ID).is(permiId));
					WriteResult res = mongoTemplate.remove(query, MongodbConstants.PERMISSION_RS_FUNC);
					i = i + res.getN();
				}
			}

			if (i > 0) {
				logger.info(String.format("[delPermiRsFunc()->succ:%s]", AuthzConstants.DEL_SUCC));
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.DEL_SUCC, null);
			} else {
				logger.error(String.format("[delPermiRsFunc()->error:%s]", AuthzConstants.DEL_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.DEL_FAIL);
			}
		} catch (Exception e) {
			logger.error("[delPermiRsFunc()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<List<JSONObject>> permisBindFunc(String powerId, List<String> itemId) throws Exception {
		try {
			if (org.apache.commons.lang3.StringUtils.isBlank(powerId) || itemId == null) {
				logger.error(String.format("[permisBindFunc()->error:%s]", AuthzConstants.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 删除powerId的关系表
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.PERMI_ID).is(powerId));
			mongoTemplate.remove(query, MongodbConstants.PERMISSION_RS_FUNC);
			// 添加关系
			List<JSONObject> itemRs = new ArrayList<>();
			for (String str : itemId) {
				JSONObject json = new JSONObject();
				json.put(AuthzConstants.ID_, UUIDUtil.getUUID());
				json.put(AuthzConstants.FUNC_ID, str);
				json.put(AuthzConstants.PERMI_ID, powerId);
				itemRs.add(json);
			}
			mongoTemplate.insert(itemRs, MongodbConstants.PERMISSION_RS_FUNC);
			logger.info(String.format("[permisBindFunc()->succ:%s]", AuthzConstants.SAVE_SUCC));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.SAVE_SUCC, itemRs);
		} catch (Exception e) {
			logger.error("[permisBindFunc()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.PermissionBaseCrudService#addAccessRsPermi(java.lang.String,
	 *      java.util.List)
	 */
	@Override
	public RpcResponse<List<JSONObject>> addAccessRsPermi(String accessId, JSONArray permiIds) throws Exception {
		try {
			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[addAccessRsPermi()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						TenementConstant.ACCESS_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			if (StringUtils.isEmpty(permiIds) || permiIds.size() == 0) {
				logger.error(String.format("[addAccessRsPermi()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						TenementConstant.ACCESS_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 添加关系
			List<JSONObject> accessRsPermi = new ArrayList<>();
			for (int i = 0; i < permiIds.size(); i++) {
				JSONObject json = new JSONObject();
				json.put(AuthzConstants.ID_, UUIDUtil.getUUID());
				json.put(AuthzConstants.TENEMENT_ACCESS_ID, accessId);
				json.put(AuthzConstants.PERMI_ID, permiIds.getString(i));
				accessRsPermi.add(json);
			}
			mongoTemplate.insert(accessRsPermi, MongodbConstants.PERMISSION_RS_ACCESS);
			logger.info(String.format("[addAccessRsPermi()->succ:%s]", AuthzConstants.SAVE_SUCC));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.SAVE_SUCC, accessRsPermi);
		} catch (Exception e) {
			logger.error("[addAccessRsPermi()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.PermissionBaseCrudService#addAccessRsPermi(java.lang.String,
	 *      java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<List<JSONObject>> addAccessRsPermi(String accessId, String accessType) throws Exception {
		try {
			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[addAccessRsPermi()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						TenementConstant.ACCESS_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			if (StringUtils.isEmpty(accessType)) {
				logger.error(String.format("[addAccessRsPermi()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						TenementConstant.ACCESS_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 根据接入方类型查询接入方类型所拥有的权限信息
			Query query = new Query(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(AuthzConstants.STATE).is(AuthzConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(AuthzConstants.ACCESS_TYPE).is(accessType));

			List<String> permiIds = mongoTemplateUtil.getListByKey(MongodbConstants.PERMISSION_COLL, query,
					AuthzConstants.ID_);

			List<JSONObject> accessRsPermi = new ArrayList<>();
			for (int i = 0; i < permiIds.size(); i++) {
				JSONObject json = new JSONObject();
				json.put(AuthzConstants.ID_, UUIDUtil.getUUID());
				json.put(AuthzConstants.TENEMENT_ACCESS_ID, accessId);
				json.put(AuthzConstants.PERMI_ID, permiIds.get(i));
				accessRsPermi.add(json);
			}
			mongoTemplate.insert(accessRsPermi, MongodbConstants.PERMISSION_RS_ACCESS);
			logger.info(String.format("[addAccessRsPermi()->succ:%s]", AuthzConstants.SAVE_SUCC));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.SAVE_SUCC, accessRsPermi);
		} catch (Exception e) {
			logger.error("[addAccessRsPermi()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.PermissionBaseCrudService#addAccessRsPermi(java.lang.String,
	 *      java.util.List)
	 */
	@Override
	public RpcResponse<Integer> delAccessRsPermi(String accessId, JSONArray permiIds) throws Exception {
		
		try {
			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[delAccessRsPermi()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						TenementConstant.ACCESS_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			if (StringUtils.isEmpty(permiIds) || permiIds.size() == 0) {
				logger.error(String.format("[delAccessRsPermi()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						"permiIds"));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 删除权限与接入方id的关系表
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));
			query.addCriteria(Criteria.where(AuthzConstants.PERMI_ID).in(permiIds));
			WriteResult res = mongoTemplate.remove(query, MongodbConstants.PERMISSION_RS_ACCESS);
			if (res.getN() > 0) {
				BasicDBObject fieldsObject = new BasicDBObject();
				fieldsObject.put(UscConstants.ID_, 1);
				DBObject dbCondition = new BasicDBObject();
				Query getRole = new BasicQuery(dbCondition, fieldsObject);
				getRole.addCriteria(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));
				List<JSONObject> roles = mongoTemplate.find(getRole, JSONObject.class, MongodbConstants.MONGODB_USERROLE_COLL);
				JSONArray roleIdArray = new JSONArray();
				for (JSONObject role : roles) {
					String roleId = role.getString(UscConstants.ID_);
					if (!org.apache.commons.lang3.StringUtils.isBlank(roleId)) {
						roleIdArray.add(roleId);
					}
				}
				if (roleIdArray.size() > 0) {
					RpcResponse<List<JSONObject>> delPermiRsRole = delPermiRsRole(permiIds, roleIdArray);
					if (delPermiRsRole == null || !delPermiRsRole.isSuccess()) {
						logger.error(String.format("[delPermiRsFunc()->fail:解除角色与权限的绑定关系失败:权限id:%s --> 角色id: %s", permiIds,roleIdArray));
					}
				}
				
				logger.info(String.format("[delPermiRsFunc()->succ:%s]", AuthzConstants.DEL_SUCC));
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.DEL_SUCC, res.getN());
			} else {
				logger.error(String.format("[delPermiRsFunc()->error:%s]", AuthzConstants.DEL_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.DEL_FAIL);
			}

		} catch (Exception e) {
			logger.error("[delAccessRsPermi()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		
	}
}
