package com.run.authz.base.curd.service;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.base.crud.FunctionItemBaseCurdService;
import com.run.authz.api.base.util.ExceptionChecked;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.api.constants.MongodbConstants;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.base.util.MongoTemplateUtil;

public class FunctionItemCurdRpcServiceImpl implements FunctionItemBaseCurdService {
	private static final Logger	logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private MongoTemplateUtil	tenementTemplateUtil;
	@Autowired
	private MongoTemplate		tenementTemplate;



	@Override
	public RpcResponse<JSONObject> saveFunctionItemInfo(JSONObject functionItemInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "saveFunctionItemInfo",
					functionItemInfo, AuthzConstants.ACCESS_TYPE, AuthzConstants.ITEM_NAME,
					AuthzConstants.APPLICATION_TYPE);
			if (rs != null) {
				return rs;
			}
			// 重名校验
			Query queryItem = new Query(Criteria.where(AuthzConstants.ACCESS_TYPE)
					.is(functionItemInfo.getString(AuthzConstants.ACCESS_TYPE)));
			queryItem.addCriteria(
					Criteria.where(AuthzConstants.ITEM_NAME).is(functionItemInfo.getString(AuthzConstants.ITEM_NAME)));
			queryItem.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));

			// 增加条件应用类型
			// 获取应用类型
			String applictionType = functionItemInfo.getString(AuthzConstants.APPLICATION_TYPE);

			if (AuthzConstants.APP.equals(applictionType)) {
				queryItem.addCriteria(Criteria.where(AuthzConstants.APPLICATION_TYPE).is(applictionType));
			} else if (AuthzConstants.PC.equals(applictionType)) {
				queryItem.addCriteria(Criteria.where(AuthzConstants.APPLICATION_TYPE).in(applictionType, null));
			}

			Boolean result = tenementTemplate.exists(queryItem, MongodbConstants.FUNCTION_ITEM);
			if (result) {
				logger.debug(
						String.format("[saveFunctionItemInfo()->fail:%s]", AuthzConstants.ITEM_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.ITEM_SAVE_FAIL_NAME_EXITES);
			}

			// 插入功能项表操作
			RpcResponse<JSONObject> res = tenementTemplateUtil.insert(logger, "saveFunctionItemInfo", functionItemInfo,
					MongodbConstants.FUNCTION_ITEM);
			return res;

		} catch (Exception e) {
			logger.error("[saveFunctionItemInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<JSONObject> updateFunctionItemInfo(JSONObject functionItemInfo) throws Exception {
		try {
			// 参数有效性校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "updateUserRoleInfo",
					functionItemInfo, AuthzConstants.ITEM_NAME, AuthzConstants.ID_, AuthzConstants.APPLICATION_TYPE);
			if (rs != null) {
				return rs;
			}

			String id = functionItemInfo.getString(AuthzConstants.ID_);
			// 重名校验
			Query queryItem = new Query(Criteria.where(AuthzConstants.ACCESS_TYPE)
					.is(functionItemInfo.getString(AuthzConstants.ACCESS_TYPE)));
			queryItem.addCriteria(Criteria.where(AuthzConstants.ID_).nin(id));
			queryItem.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
			queryItem.addCriteria(
					Criteria.where(AuthzConstants.ITEM_NAME).is(functionItemInfo.getString(AuthzConstants.ITEM_NAME)));

			// 增加条件应用类型
			// 获取应用类型
			String applictionType = functionItemInfo.getString(AuthzConstants.APPLICATION_TYPE);

			if (AuthzConstants.APP.equals(applictionType)) {
				queryItem.addCriteria(Criteria.where(AuthzConstants.APPLICATION_TYPE).is(applictionType));
			} else if (AuthzConstants.PC.equals(applictionType)) {
				queryItem.addCriteria(Criteria.where(AuthzConstants.APPLICATION_TYPE).in(applictionType, null));
			}

			Boolean result = tenementTemplate.exists(queryItem, MongodbConstants.FUNCTION_ITEM);
			if (result) {
				logger.debug(String.format("[updateFunctionItemInfo()->fail:%s]",
						AuthzConstants.ITEM_UPDATE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.ITEM_UPDATE_FAIL_NAME_EXITES);
			}

			return tenementTemplateUtil.update(logger, "updateFunctionItemInfo", functionItemInfo,
					MongodbConstants.FUNCTION_ITEM, id);

		} catch (Exception e) {
			logger.error("[updateUserRoleInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<JSONArray> deleteFunctionItemInfo(JSONArray ids) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(ids) || ids.isEmpty()) {
				logger.error(String.format("[deleteFunctionItemInfo()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.IDS));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			RpcResponse<JSONArray> res = tenementTemplateUtil.delete(logger, "deleteFunctionItemInfo",
					MongodbConstants.FUNCTION_ITEM, ids);
			return res;
		} catch (Exception e) {
			logger.error("[deleteFunctionItemInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> addItemRsMenu(String itemId, JSONArray menuArray) {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(itemId)) {
				logger.error(String.format("[addItemRsMenu()->error:%s]", AuthzConstants.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 清空功能项菜单RS表
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.ITEM_ID).is(itemId));
			tenementTemplate.remove(query, MongodbConstants.ITEM_RS_MENU);

			// 保存功能项与菜单关系
			JSONObject rsObj = new JSONObject();

			rsObj.put(AuthzConstants.ITEM_ID, itemId);

			for (int i = 0; i < menuArray.size(); i++) {
				rsObj.put(AuthzConstants.ID_, UUID.randomUUID().toString());
				String menuId = menuArray.getString(i);
				rsObj.put(AuthzConstants.MENU_ID, menuId);
				tenementTemplate.insert(rsObj, MongodbConstants.ITEM_RS_MENU);
			}

			logger.info(String.format("[addItemRsMenu()->success:%s]", AuthzConstants.SAVE_SUCC));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.SAVE_SUCC, null);

		} catch (Exception e) {
			logger.error("[addItemRsMenu()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> addItemRsButton(String itemId, JSONArray buttonArray) {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(itemId)) {
				logger.error(String.format("[addItemRsButton()->error:%s]", AuthzConstants.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 清空功能项菜单RS表
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.ITEM_ID).is(itemId));
			tenementTemplate.remove(query, MongodbConstants.ITEM_RS_BUTTON);

			// 保存功能项与按钮关系
			JSONObject rsObj = new JSONObject();

			rsObj.put(AuthzConstants.ITEM_ID, itemId);

			for (int i = 0; i < buttonArray.size(); i++) {
				rsObj.put(AuthzConstants.ID_, UUID.randomUUID().toString());
				String buttonId = buttonArray.getString(i);
				rsObj.put(AuthzConstants.BUTTON_ID, buttonId);
				tenementTemplate.insert(rsObj, MongodbConstants.ITEM_RS_BUTTON);
			}

			logger.info(String.format("[addItemRsButton()->success:%s]", AuthzConstants.SAVE_SUCC));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.SAVE_SUCC, null);

		} catch (Exception e) {
			logger.error("[addItemRsButton()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> addItemRsInterUrl(String itemId, JSONArray urlArray) {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(itemId)) {
				logger.error(String.format("[addItemRsInterUrl()->error:%s]", AuthzConstants.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 清空功能项接口地址RS表
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.ITEM_ID).is(itemId));
			tenementTemplate.remove(query, MongodbConstants.ITEM_RS_INTER_URL);

			// 保存功能项与接口地址关系
			JSONObject rsObj = new JSONObject();

			rsObj.put(AuthzConstants.ITEM_ID, itemId);

			for (int i = 0; i < urlArray.size(); i++) {
				rsObj.put(AuthzConstants.ID_, UUID.randomUUID().toString());
				String interUrlId = urlArray.getString(i);
				rsObj.put(AuthzConstants.URLID, interUrlId);
				tenementTemplate.insert(rsObj, MongodbConstants.ITEM_RS_INTER_URL);
			}

			logger.info(String.format("[addItemRsInterUrl()->success:%s]", AuthzConstants.SAVE_SUCC));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.SAVE_SUCC, null);

		} catch (Exception e) {
			logger.error("[addItemRsInterUrl()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
