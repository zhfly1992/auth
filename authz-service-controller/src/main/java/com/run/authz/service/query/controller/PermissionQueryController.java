/*
 * File name: PermissionQueryController.java
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

package com.run.authz.service.query.controller;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.api.constants.AuthzUrlConstants;
import com.run.authz.base.query.PermiBaseQueryService;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Pagination;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;

/**
 * @Description: 权限querycontroller类
 * @author: zhabing
 * @version: 1.0, 2017年8月28日
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = AuthzUrlConstants.PERMISSIOM)
public class PermissionQueryController {

	private static final Logger		logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private PermiBaseQueryService	permiQuery;



	/**
	 * 
	 * @Description:分页查询权限信息
	 * @param userRolePageInfo
	 *            权限分页信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_PREMISSION_BYPAGE, method = RequestMethod.POST)
	public Result getPermissionByPage(@RequestBody String permiPageInfo) {
		logger.info(String.format("[getPermissionByPage()->request param:%s]", permiPageInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(permiPageInfo);
			if (null != checResult) {
				logger.error(String.format("[getPermissionByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject permiJson = JSON.parseObject(permiPageInfo);

			RpcResponse<Pagination<Map<String, Object>>> res = permiQuery.getPermiInfoByPage(permiJson);
			if (res.isSuccess()) {
				logger.info(String.format("[getPermissionByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getPermissionByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getPermissionByPage()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:检查角色名称是否重复
	 * @param accUserNameInfo
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.CHECK_PERMI_NAME, method = RequestMethod.POST)
	public Result<Boolean> checkPermiName(@RequestBody String permiNameInfo) {
		logger.info(String.format("[checkPermiName()->request param:%s]", permiNameInfo));
		try {
			// 参数基础校验
			Result<Boolean> checResult = ExceptionChecked.checkRequestParam(permiNameInfo);
			if (null != checResult) {
				logger.error(String.format("[checkPermiName()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject permiInfo = JSON.parseObject(permiNameInfo);
			RpcResponse<Boolean> res = permiQuery.checkPermiName(permiInfo.getString(AuthzConstants.ACCESS_TYPE),
					permiInfo.getString(AuthzConstants.PERMI_NAME));
			if (res.isSuccess()) {
				logger.info(String.format("[checkPermiName()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkPermiName()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[checkPermiName()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:分页查询該角色已經擁有或者未擁有的权限信息
	 * @param userRolePageInfo
	 *            权限分页信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_ROLE_PREMISSION_BYPAGE, method = RequestMethod.POST)
	public Result getRolePermissionByPage(@RequestBody String permiPageInfo) {
		logger.info(String.format("[getPermissionByPage()->request param:%s]", permiPageInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(permiPageInfo);
			if (null != checResult) {
				logger.error(String.format("[getPermissionByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject permiJson = JSON.parseObject(permiPageInfo);

			RpcResponse<Pagination<Map<String, Object>>> res = permiQuery.getRolePermissionByPage(permiJson);
			if (res.isSuccess()) {
				logger.info(String.format("[getPermissionByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getPermissionByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getPermissionByPage()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:分页查询接入方已拥有和未拥有的权限信息
	 * @param userRolePageInfo
	 *            权限分页信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_ACCESS_PREMISSION_BYPAGE, method = RequestMethod.POST)
	public Result getAccessPermissionByPage(@RequestBody String permiPageInfo) {
		logger.info(String.format("[getPermissionByPage()->request param:%s]", permiPageInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(permiPageInfo);
			if (null != checResult) {
				logger.error(String.format("[getPermissionByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject permiJson = JSON.parseObject(permiPageInfo);

			RpcResponse<Pagination<Map<String, Object>>> res = permiQuery.getAccessPermissionByPage(permiJson);
			if (res.isSuccess()) {
				logger.info(String.format("[getPermissionByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getPermissionByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getPermissionByPage()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:分页查询該岗位已經擁有或者未擁有的权限信息
	 * @param userRolePageInfo
	 *            权限分页信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_ROLE_PREMI_BYPAGE, method = RequestMethod.POST)
	public Result getRolePermiByPage(@RequestBody String permiPageInfo) {
		logger.info(String.format("[getRolePermiByPage()->request param:%s]", permiPageInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(permiPageInfo);
			if (null != checResult) {
				logger.error(String.format("[getRolePermiByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject permiJson = JSON.parseObject(permiPageInfo);

			RpcResponse<Pagination<Map<String, Object>>> res = permiQuery.getRolePermiByPage(permiJson);
			if (res.isSuccess()) {
				logger.info(String.format("[getRolePermiByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getRolePermiByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getRolePermiByPage()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:分页查询該权限已經擁有或者未擁有的功能项信息
	 * @param userRolePageInfo
	 *            权限分页信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_FUNC_PREMISSION_BYPAGE, method = RequestMethod.POST)
	public Result getFuncPermissionByPage(@RequestBody String permiPageInfo) {
		logger.info(String.format("[getFuncPermissionByPage()->request param:%s]", permiPageInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(permiPageInfo);
			if (null != checResult) {
				logger.error(String.format("[getFuncPermissionByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject permiJson = JSON.parseObject(permiPageInfo);

			RpcResponse<Pagination<Map<String, Object>>> res = permiQuery.getFuncPermissionByPage(permiJson);
			if (res.isSuccess()) {
				logger.info(String.format("[getFuncPermissionByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getFuncPermissionByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getFuncPermissionByPage()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_ITEM_BY_ROLE_ID, method = RequestMethod.GET)
	public Result<List<Map>> getItemInfoByPermisId(@PathVariable String permisId) {
		try {
			RpcResponse<List<Map>> res = permiQuery.getItemInfoByPermisId(permisId);
			if (res.isSuccess()) {
				logger.info(String.format("[getItemInfoByPermisId()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getItemInfoByPermisId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getItemInfoByPermisId()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
