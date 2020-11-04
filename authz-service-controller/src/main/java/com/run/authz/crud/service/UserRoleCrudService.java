/*
 * File name: UserRoleCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月19日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.crud.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.base.crud.UserRoleBaseCrudService;
import com.run.authz.api.constants.AuthzConstants;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;

/**
 * @Description: 用户角色crud service类
 * @author: zhabing
 * @version: 1.0, 2017年7月19日
 */
@Service
public class UserRoleCrudService {
	private static final Logger		logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private UserRoleBaseCrudService	accUserRoleCrud;



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result<JSONObject> saveUserRole(String roleInfo) {
		logger.info(String.format("[saveUserRole()->request param:%s]", roleInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(roleInfo);
			if (null != checResult) {
				logger.error(String.format("[saveUserRole()->fail:%s]", roleInfo));
				return checResult;
			}
			RpcResponse<JSONObject> res = accUserRoleCrud.saveUserRoleInfo(JSON.parseObject(roleInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[saveUserRole()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[saveUserRole()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[saveUserRole()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result<JSONObject> saveUserRoleRs(String roleInfo) {
		logger.info(String.format("[saveUserRoleRs()->request param:%s]", roleInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(roleInfo);
			if (null != checResult) {
				logger.error(String.format("[saveUserRoleRs()->fail:%s]", roleInfo));
				return checResult;
			}
			RpcResponse<JSONObject> res = accUserRoleCrud.saveUserRoleRsInfo(JSON.parseObject(roleInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[saveUserRoleRs()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[saveUserRoleRs()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[saveUserRoleRs()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result<JSONObject> updateUserRoleRs(String roleInfo) {
		logger.info(String.format("[updateUserRoleRs()->request param:%s]", roleInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(roleInfo);
			if (null != checResult) {
				logger.error(String.format("[updateUserRoleRs()->fail:%s]", roleInfo));
				return checResult;
			}
			RpcResponse<JSONObject> res = accUserRoleCrud.updateUserRoleRsInfo(JSON.parseObject(roleInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[updateUserRoleRs()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updateUserRoleRs()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updateUserRoleRs()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result<String> swateUserRoleState(String roleInfo) {
		logger.info(String.format("[swateUserRoleState()->request param:%s]", roleInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(roleInfo);
			if (null != checResult) {
				logger.error(String.format("[swateUserRoleState()->fail:%s]", roleInfo));
				return checResult;
			}

			JSONObject swateInfo = JSON.parseObject(roleInfo);
			String roleId = swateInfo.getString(AuthzConstants.ROLE_ID);
			String state = swateInfo.getString(AuthzConstants.STATE);

			RpcResponse<String> res = accUserRoleCrud.swateUserRoleState(roleId, state);
			if (res.isSuccess()) {
				logger.info(String.format("[swateUserRoleState()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[swateUserRoleState()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[swateUserRoleState()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Result<JSONObject> updateUserRole(String roleInfo) {
		logger.info(String.format("[updateUserRole()->request param:%s]", roleInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(roleInfo);
			if (null != checResult) {
				logger.error(String.format("[updateUserRole()->fail:%s]", checResult.getException()));
				return checResult;
			}
			RpcResponse<JSONObject> res = accUserRoleCrud.updateUserRoleInfo(JSON.parseObject(roleInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[updateUserRole()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updateUserRole()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updateUserRole()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Result<JSONArray> delUserRole(String ids) {
		logger.info(String.format("[delUserRole()->request param:%s]", ids));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(ids);
			if (null != checResult) {
				logger.error(String.format("[delUserRole()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject deInfo = JSON.parseObject(ids);
			// 获取删除id数组
			JSONArray delIds = deInfo.getJSONArray(AuthzConstants.IDS);
			RpcResponse<JSONArray> res = accUserRoleCrud.deleteUserRoleInfo(delIds);
			if (res.isSuccess()) {
				logger.info(String.format("[delUserRole()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[delUserRole()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[delUserRole()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:添加角色与用户的关联
	 * @param accUserNameInfo
	 * @return
	 */
	public Result<String> addRoleRsUser(String roleUserInfo) {
		logger.info(String.format("[addRoleRsUser()->request param:%s]", roleUserInfo));
		try {
			// 参数基础校验
			Result<String> checResult = ExceptionChecked.checkRequestParam(roleUserInfo);
			if (null != checResult) {
				logger.error(String.format("[addRoleRsUser()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject roleInfo = JSON.parseObject(roleUserInfo);

			// 获取用户id数组
			JSONArray userIds = roleInfo.getJSONArray(AuthzConstants.USER_ID);
			JSONArray roleIds = roleInfo.getJSONArray(AuthzConstants.ROLE_ID);
			String accessId = roleInfo.getString(AuthzConstants.TENEMENT_ACCESS_ID);
			RpcResponse<String> res = accUserRoleCrud.addRoleRsUser(roleIds, userIds, accessId);
			if (res.isSuccess()) {
				logger.info(String.format("[addRoleRsUser()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[addRoleRsUser()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[addRoleRsUser()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:批量删除角色与用户的关联
	 * @param accUserNameInfo
	 * @return
	 */
	public Result<String> delRoleRsUser(@RequestBody String roleUserInfo) {
		logger.info(String.format("[delRoleRsUser()->request param:%s]", roleUserInfo));
		try {
			// 参数基础校验
			Result<String> checResult = ExceptionChecked.checkRequestParam(roleUserInfo);
			if (null != checResult) {
				logger.error(String.format("[delRoleRsUser()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject roleInfo = JSON.parseObject(roleUserInfo);

			// 获取用户id数组
			JSONArray userIds = roleInfo.getJSONArray(AuthzConstants.USER_ID);
			JSONArray roleIds = roleInfo.getJSONArray(AuthzConstants.ROLE_ID);
			RpcResponse<String> res = accUserRoleCrud.delRoleRsUser(roleIds, userIds);
			if (res.isSuccess()) {
				logger.info(String.format("[delRoleRsUser()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[delRoleRsUser()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[delRoleRsUser()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
