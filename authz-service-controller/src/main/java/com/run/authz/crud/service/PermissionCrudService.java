/*
 * File name: PermissionCrudService.java
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

package com.run.authz.crud.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.base.crud.PermissionBaseCrudService;
import com.run.authz.api.constants.AuthzConstants;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;

/**
 * @Description:
 * @author: zhabing
 * @version: 1.0, 2017年8月28日
 */
@Service
public class PermissionCrudService {
	private static final Logger			logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private PermissionBaseCrudService	permiCrud;



	public Result<JSONObject> savePermission(String permiInfo) {
		logger.info(String.format("[savePermission()->request param:%s]", permiInfo));
		try {
			// 参数基础校验
			Result<JSONObject> checResult = ExceptionChecked.checkRequestParam(permiInfo);
			if (null != checResult) {
				logger.error(String.format("[savePermission()->fail:%s]", permiInfo));
				return checResult;
			}
			RpcResponse<JSONObject> res = permiCrud.savePermissionInfo(JSON.parseObject(permiInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[savePermission()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[savePermission()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[savePermission()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<JSONObject> updatePermissionInfo(String permiInfo) {
		logger.info(String.format("[updatePermissionInfo()->request param:%s]", permiInfo));
		try {
			// 参数基础校验
			Result<JSONObject> checResult = ExceptionChecked.checkRequestParam(permiInfo);
			if (null != checResult) {
				logger.error(String.format("[updatePermissionInfo()->fail:%s]", permiInfo));
				return checResult;
			}
			RpcResponse<JSONObject> res = permiCrud.updatePermissionInfo(JSON.parseObject(permiInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[updatePermissionInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updatePermissionInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[updatePermissionInfo()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Result<JSONArray> delPermission(String ids) {
		logger.info(String.format("[delPermission()->request param:%s]", ids));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(ids);
			if (null != checResult) {
				logger.error(String.format("[delPermission()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject deInfo = JSON.parseObject(ids);
			// 获取删除id数组
			JSONArray delIds = deInfo.getJSONArray(AuthzConstants.IDS);
			RpcResponse<JSONArray> res = permiCrud.deletePermiInfo(delIds);
			if (res.isSuccess()) {
				logger.info(String.format("[delPermission()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[delPermission()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[delUserRole()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<List<JSONObject>> addPermiRsRole(String permiInfo) {
		logger.info(String.format("[addPermiRsRole()->request param:%s]", permiInfo));
		try {
			// 参数基础校验
			Result<List<JSONObject>> checResult = ExceptionChecked.checkRequestParam(permiInfo);
			if (null != checResult) {
				logger.error(String.format("[addPermiRsRole()->fail:%s]", permiInfo));
				return checResult;
			}

			JSONObject permiJson = JSON.parseObject(permiInfo);

			// 获取角色数组
			JSONArray roleArray = permiJson.getJSONArray(AuthzConstants.ROLE_ARRAY);
			// 获取权限数组
			JSONArray permiArray = permiJson.getJSONArray(AuthzConstants.PERMI_ARRAY);

			RpcResponse<List<JSONObject>> res = permiCrud.addPermiRsRole(permiArray, roleArray);
			if (res.isSuccess()) {
				logger.info(String.format("[addPermiRsRole()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[addPermiRsRole()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[addPermiRsRole()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}


//TODO 角色与权限解绑
	public Result<List<JSONObject>> delPermiRsRole(String permiInfo) {
		logger.info(String.format("[delPermiRsRole()->request param:%s]", permiInfo));
		try {
			// 参数基础校验
			Result<List<JSONObject>> checResult = ExceptionChecked.checkRequestParam(permiInfo);
			if (null != checResult) {
				logger.error(String.format("[delPermiRsRole()->fail:%s]", permiInfo));
				return checResult;
			}

			JSONObject permiJson = JSON.parseObject(permiInfo);

			// 获取角色数组
			JSONArray roleArray = permiJson.getJSONArray(AuthzConstants.ROLE_ARRAY);
			// 获取权限数组
			JSONArray permiArray = permiJson.getJSONArray(AuthzConstants.PERMI_ARRAY);

			RpcResponse<List<JSONObject>> res = permiCrud.delPermiRsRole(permiArray, roleArray);
			if (res.isSuccess()) {
				logger.info(String.format("[delPermiRsRole()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[delPermiRsRole()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[delPermiRsRole()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<List<JSONObject>> addPermiRsFunc(String permiInfo) {
		logger.info(String.format("[addPermiRsFunc()->request param:%s]", permiInfo));
		try {
			// 参数基础校验
			Result<List<JSONObject>> checResult = ExceptionChecked.checkRequestParam(permiInfo);
			if (null != checResult) {
				logger.error(String.format("[addPermiRsFunc()->fail:%s]", permiInfo));
				return checResult;
			}

			JSONObject permiJson = JSON.parseObject(permiInfo);

			// 获取功能项数组
			JSONArray funcArray = permiJson.getJSONArray(AuthzConstants.FUNC_ARRAY);
			// 获取权限数组
			JSONArray permiArray = permiJson.getJSONArray(AuthzConstants.PERMI_ARRAY);

			RpcResponse<List<JSONObject>> res = permiCrud.addPermiRsFunc(permiArray, funcArray);
			if (res.isSuccess()) {
				logger.info(String.format("[addPermiRsFunc()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[addPermiRsFunc()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[addPermiRsFunc()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<List<JSONObject>> delPermiRsFunc(String permiInfo) {
		logger.info(String.format("[delPermiRsFunc()->request param:%s]", permiInfo));
		try {
			// 参数基础校验
			Result<List<JSONObject>> checResult = ExceptionChecked.checkRequestParam(permiInfo);
			if (null != checResult) {
				logger.error(String.format("[delPermiRsFunc()->fail:%s]", permiInfo));
				return checResult;
			}

			JSONObject permiJson = JSON.parseObject(permiInfo);

			// 获取功能项数组
			JSONArray funcArray = permiJson.getJSONArray(AuthzConstants.FUNC_ARRAY);
			// 获取权限数组
			JSONArray permiArray = permiJson.getJSONArray(AuthzConstants.PERMI_ARRAY);

			RpcResponse<List<JSONObject>> res = permiCrud.delPermiRsFunc(permiArray, funcArray);
			if (res.isSuccess()) {
				logger.info(String.format("[delPermiRsFunc()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[delPermiRsFunc()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[delPermiRsFunc()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings("unchecked")
	public Result<List<JSONObject>> permisBindFunc(String permiId, String itemInfo) {
		logger.info(String.format("[permisBindFunc()->request param:%s]", itemInfo));
		try {
			// 参数基础校验
			Result<List<JSONObject>> checResult = ExceptionChecked.checkRequestParam(itemInfo);
			if (null != checResult) {
				logger.error(String.format("[permisBindFunc()->fail:%s]", itemInfo));
				return checResult;
			}
			JSONObject permiJson = JSON.parseObject(itemInfo);
			List<String> permiArray = (List<String>) permiJson.get(AuthzConstants.ITEM_ID);
			RpcResponse<List<JSONObject>> res = permiCrud.permisBindFunc(permiId, permiArray);
			if (res.isSuccess()) {
				logger.info(String.format("[permisBindFunc()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[permisBindFunc()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[permisBindFunc()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<List<JSONObject>> addAccessRsPermi(String accessRsPermiInfo) {
		logger.info(String.format("[addAccessRsPermi()->request param:%s]", accessRsPermiInfo));
		try {
			// 参数基础校验
			Result<List<JSONObject>> checResult = ExceptionChecked.checkRequestParam(accessRsPermiInfo);
			if (null != checResult) {
				logger.error(String.format("[addAccessRsPermi()->fail:%s]", accessRsPermiInfo));
				return checResult;
			}
			JSONObject permiJson = JSON.parseObject(accessRsPermiInfo);
			String accessId = permiJson.getString(AuthzConstants.TENEMENT_ACCESS_ID);
			JSONArray listPermiIds = permiJson.getJSONArray(AuthzConstants.PERMI_ID);
			RpcResponse<List<JSONObject>> res = permiCrud.addAccessRsPermi(accessId, listPermiIds);
			if (res.isSuccess()) {
				logger.info(String.format("[addAccessRsPermi()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[addAccessRsPermi()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[addAccessRsPermi()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Integer> delAccessRsPermi(String accessRsPermiInfo) {
		logger.info(String.format("[delAccessRsPermi()->request param:%s]", accessRsPermiInfo));
		try {
			// 参数基础校验
			Result<Integer> checResult = ExceptionChecked.checkRequestParam(accessRsPermiInfo);
			if (null != checResult) {
				logger.error(String.format("[delAccessRsPermi()->fail:%s]", accessRsPermiInfo));
				return checResult;
			}
			JSONObject permiJson = JSON.parseObject(accessRsPermiInfo);
			String accessId = permiJson.getString(AuthzConstants.TENEMENT_ACCESS_ID);
			JSONArray listPermiIds = permiJson.getJSONArray(AuthzConstants.PERMI_ID);
			RpcResponse<Integer> res = permiCrud.delAccessRsPermi(accessId, listPermiIds);
			if (res.isSuccess()) {
				logger.info(String.format("[delAccessRsPermi()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[delAccessRsPermi()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[delAccessRsPermi()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}

}
