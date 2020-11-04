package com.run.authz.crud.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.base.crud.FunctionItemBaseCurdService;
import com.run.authz.api.constants.AuthzConstants;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;

@Service
public class FunctionItemCrudService {
	private static final Logger			logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private FunctionItemBaseCurdService	functionItemBaseCurdService;



	public Result<JSONObject> saveFunctionItem(String itemInfo) {
		logger.info(String.format("[saveFunctionItem()->request param:%s]", itemInfo));
		try {
			// 参数基础校验
			Result<JSONObject> checResult = ExceptionChecked.checkRequestParam(itemInfo);
			if (null != checResult) {
				logger.error(String.format("[saveFunctionItem()->fail:%s]", itemInfo));
				return checResult;
			}
			// 调用RPC服务保存功能项
			RpcResponse<JSONObject> res = functionItemBaseCurdService.saveFunctionItemInfo(JSON.parseObject(itemInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[saveFunctionItem()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[saveFunctionItem()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[saveUserRole()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<JSONObject> updateFunctionItem(String itemInfo) {
		logger.info(String.format("[updateFunctionItem()->request param:%s]", itemInfo));
		try {
			// 参数基础校验
			Result<JSONObject> checResult = ExceptionChecked.checkRequestParam(itemInfo);
			if (null != checResult) {
				logger.error(String.format("[updateFunctionItem()->fail:%s]", itemInfo));
				return checResult;
			}

			// 调用RPC修改功能项
			RpcResponse<JSONObject> res = functionItemBaseCurdService
					.updateFunctionItemInfo(JSON.parseObject(itemInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[updateFunctionItem()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updateFunctionItem()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updateFunctionItem()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<JSONArray> delFunctionItem(String ids) {
		logger.info(String.format("[delFunctionItem()->request param:%s]", ids));
		try {
			// 参数基础校验
			Result<JSONArray> checResult = ExceptionChecked.checkRequestParam(ids);
			if (null != checResult) {
				logger.error(String.format("[delUserRole()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject deInfo = JSON.parseObject(ids);
			// 获取删除id数组
			JSONArray delIds = deInfo.getJSONArray(AuthzConstants.IDS);
			RpcResponse<JSONArray> res = functionItemBaseCurdService.deleteFunctionItemInfo(delIds);
			if (res.isSuccess()) {
				logger.info(String.format("[delFunctionItem()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[delFunctionItem()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[delFunctionItem()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> addItemRsMenu(String itemMenuInfo) {
		logger.info(String.format("[addItemRsMenu()->request param:%s]", itemMenuInfo));
		try {
			// 参数基础校验
			Result<String> checResult = ExceptionChecked.checkRequestParam(itemMenuInfo);
			if (null != checResult) {
				logger.error(String.format("[addItemRsMenu()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject itemMenuJsonInfo = JSON.parseObject(itemMenuInfo);

			// 获取菜单资源id数组
			JSONArray menuIds = itemMenuJsonInfo.getJSONArray(AuthzConstants.MENU_ID);
			String itemId = itemMenuJsonInfo.getString(AuthzConstants.ITEM_ID);
			RpcResponse<String> res = functionItemBaseCurdService.addItemRsMenu(itemId, menuIds);
			if (res.isSuccess()) {
				logger.info(String.format("[addItemRsMenu()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[addItemRsMenu()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[addItemRsMenu()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> addItemRsButton(String itemButtonInfo) {
		logger.info(String.format("[addItemRsButton()->request param:%s]", itemButtonInfo));
		try {
			// 参数基础校验
			Result<String> checResult = ExceptionChecked.checkRequestParam(itemButtonInfo);
			if (null != checResult) {
				logger.error(String.format("[addItemRsButton()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject itemButtonJsonInfo = JSON.parseObject(itemButtonInfo);

			// 获取按钮资源id数组
			JSONArray buttonIds = itemButtonJsonInfo.getJSONArray(AuthzConstants.BUTTON_ID);
			String itemId = itemButtonJsonInfo.getString(AuthzConstants.ITEM_ID);
			RpcResponse<String> res = functionItemBaseCurdService.addItemRsButton(itemId, buttonIds);
			if (res.isSuccess()) {
				logger.info(String.format("[addItemRsButton()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[addItemRsButton()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[addItemRsButton()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> addItemRsInterUrl(String itemUrlInfo) {
		logger.info(String.format("[addItemRsInterUrl()->request param:%s]", itemUrlInfo));
		try {
			// 参数基础校验
			Result<String> checResult = ExceptionChecked.checkRequestParam(itemUrlInfo);
			if (null != checResult) {
				logger.error(String.format("[addItemRsInterUrl()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject itemUrlJsonInfo = JSON.parseObject(itemUrlInfo);

			// 获取url资源id数组
			JSONArray intterUrlIds = itemUrlJsonInfo.getJSONArray(AuthzConstants.URLID);
			String itemId = itemUrlJsonInfo.getString(AuthzConstants.ITEM_ID);
			RpcResponse<String> res = functionItemBaseCurdService.addItemRsInterUrl(itemId, intterUrlIds);
			if (res.isSuccess()) {
				logger.info(String.format("[addItemRsInterUrl()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[addItemRsInterUrl()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[addItemRsInterUrl()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
