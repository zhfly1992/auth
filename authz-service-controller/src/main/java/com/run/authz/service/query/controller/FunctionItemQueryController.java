package com.run.authz.service.query.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.run.authz.base.query.FunctionItemBaseQueryService;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Pagination;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = AuthzUrlConstants.ITEM)
public class FunctionItemQueryController {
	private static final Logger				logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private FunctionItemBaseQueryService	functionItemBaseQueryService;



	/**
	 * @Description:分页查询功能项
	 * @param userRolePageInfo
	 * @return Result<Pagination<Map<String, Object>>>
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_FUNCTION_ITEM_BY_PAGE, method = RequestMethod.POST)
	public Result<Pagination<Map<String, Object>>> getFunctionItemByPage(@RequestBody String itemPageInfo) {
		logger.info(String.format("[getFunctionItemByPage()->request param:%s]", itemPageInfo));
		try {
			// 参数基础校验
			Result<Pagination<Map<String, Object>>> checResult = ExceptionChecked.checkRequestParam(itemPageInfo);
			if (null != checResult) {
				logger.error(String.format("[getFunctionItemByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(itemPageInfo);

			// 分页大小
			String pageSize = pageInfo.getString(AuthzConstants.PAGESIZE);
			// 分页页数
			String pageNumber = pageInfo.getString(AuthzConstants.PAGENUMBER);
			// 查询条件
			String itemName = pageInfo.getString(AuthzConstants.ITEM_NAME);
			String accessType = pageInfo.getString(AuthzConstants.ACCESS_TYPE);

			Map<String, String> map = new HashMap<String, String>();
			map.put(AuthzConstants.PAGESIZE, pageSize);
			map.put(AuthzConstants.PAGENUMBER, pageNumber);
			map.put(AuthzConstants.ITEM_NAME, itemName);
			map.put(AuthzConstants.ACCESS_TYPE, accessType);

			RpcResponse<Pagination<Map<String, Object>>> res = functionItemBaseQueryService.getFunctionItemByPage(map);
			if (res.isSuccess()) {
				logger.info(String.format("[getFunctionItemByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getFunctionItemByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getFunctionItemByPage()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:根据id查询功能项
	 * @param itemInfo
	 * @return Result<Map<String, Object>>
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_FUNCTION_ITEM_BY_ID, method = RequestMethod.POST)
	public Result<Map<String, Object>> getFunctionItemById(@RequestBody String itemInfo) {
		logger.info(String.format("[getFunctionItemById()->request param:%s]", itemInfo));
		try {
			// 参数基础校验
			Result<Map<String, Object>> checResult = ExceptionChecked.checkRequestParam(itemInfo);
			if (null != checResult) {
				logger.error(String.format("[getFunctionItemById()->fail:%s]", itemInfo));
				return checResult;
			}

			// 获取功能项id
			JSONObject itemJson = JSON.parseObject(itemInfo);
			String itemId = itemJson.getString(AuthzConstants.ID_);

			RpcResponse<Map<String, Object>> res = functionItemBaseQueryService.getFunctionItemById(itemId);
			if (res.isSuccess()) {
				logger.info(String.format("[getFunctionItemById()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getFunctionItemById()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getFunctionItemById()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_FUNCTION_ITEM_BY_ACCESSSECRET, method = RequestMethod.POST)
	public Result<Pagination<Map<String, Object>>> getFunctionItemByAccessSecret(@RequestBody String itemPageInfo) {
		logger.info(String.format("[getFunctionItemByAccessSecret()->request param:%s]", itemPageInfo));
		try {
			// 参数基础校验
			Result<Pagination<Map<String, Object>>> checResult = ExceptionChecked.checkRequestParam(itemPageInfo);
			if (null != checResult) {
				logger.error(String.format("[getFunctionItemByAccessSecret()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(itemPageInfo);

			String secret = pageInfo.getString(AuthzConstants.ACCESS_SECRET);

			if (StringUtils.isEmpty(secret)) {
				logger.error(String.format("[getFunctionItemByAccessSecret()->fail:%s]", AuthzConstants.NO_BUSINESS));
				return ResultBuilder.failResult(AuthzConstants.NO_BUSINESS);
			}

			// 获取接入方信息
			RpcResponse<String> accessInfo = functionItemBaseQueryService.getAccessInfoBySecret(secret);
			if (!accessInfo.isSuccess()) {
				logger.error(String.format("[getFunctionItemByAccessSecret()->fail:%s]", AuthzConstants.GET_FAIL));
				return ResultBuilder.failResult(AuthzConstants.GET_FAIL);
			}

			// 分页大小
			String pageSize = pageInfo.getString(AuthzConstants.PAGESIZE);
			// 分页页数
			String pageNumber = pageInfo.getString(AuthzConstants.PAGENUMBER);

			Map<String, String> map = new HashMap<String, String>();
			map.put(AuthzConstants.PAGESIZE, pageSize);
			map.put(AuthzConstants.PAGENUMBER, pageNumber);

			RpcResponse<Pagination<Map<String, Object>>> res = functionItemBaseQueryService
					.getFunctionItemByAccessId(map, accessInfo.getSuccessValue());
			if (res.isSuccess()) {
				logger.info(String.format("[getFunctionItemByAccessSecret()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getFunctionItemByAccessSecret()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getFunctionItemByAccessSecret()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:根据功能项id查询关联菜单id
	 * @param itemInfo
	 * @return Result<List<Map>>
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_MENUID_BY_ITEMID, method = RequestMethod.POST)
	public Result<List<Map>> getMenuIdByitemId(@RequestBody String itemInfo) {
		logger.info(String.format("[getMenuIdByitemId()->request param:%s]", itemInfo));
		try {
			// 参数基础校验
			Result<List<Map>> checResult = ExceptionChecked.checkRequestParam(itemInfo);
			if (null != checResult) {
				logger.error(String.format("[getMenuIdByitemId()->fail:%s]", itemInfo));
				return checResult;
			}

			// 获取功能项id
			JSONObject itemJson = JSON.parseObject(itemInfo);
			String itemId = itemJson.getString(AuthzConstants.ID_);

			RpcResponse<List<Map>> res = functionItemBaseQueryService.getMenuIdByItemId(itemId);
			if (res.isSuccess()) {
				logger.info(String.format("[getMenuIdByitemId()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getMenuIdByitemId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getMenuIdByitemId()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_BUTTONID_BY_ITEMID, method = RequestMethod.POST)
	public Result<List<Map>> getButtonIdByitemId(@RequestBody String itemInfo) {
		logger.info(String.format("[getMenuIdByitemId()->request param:%s]", itemInfo));
		try {
			// 参数基础校验
			Result<List<Map>> checResult = ExceptionChecked.checkRequestParam(itemInfo);
			if (null != checResult) {
				logger.error(String.format("[getMenuIdByitemId()->fail:%s]", itemInfo));
				return checResult;
			}

			// 获取功能项id
			JSONObject itemJson = JSON.parseObject(itemInfo);
			String itemId = itemJson.getString(AuthzConstants.ITEM_ID);

			RpcResponse<List<Map>> res = functionItemBaseQueryService.getButtonIdByItemId(itemId);
			if (res.isSuccess()) {
				logger.info(String.format("[getMenuIdByitemId()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getMenuIdByitemId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getMenuIdByitemId()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:根据功能项id获取与之关联的urlId
	 * @param itemUrlInfo
	 * @return Result<List<Map>>
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_URLID_BY_ITEMID, method = RequestMethod.POST)
	public Result<List<Map>> getUrlIdByitemId(@RequestBody String itemUrlInfo) {
		logger.info(String.format("[getMenuIdByitemId()->request param:%s]", itemUrlInfo));
		try {
			// 参数基础校验
			Result<List<Map>> checResult = ExceptionChecked.checkRequestParam(itemUrlInfo);
			if (null != checResult) {
				logger.error(String.format("[getMenuIdByitemId()->fail:%s]", itemUrlInfo));
				return checResult;
			}

			// 获取功能项id
			JSONObject itemUrlJson = JSON.parseObject(itemUrlInfo);
			String itemId = itemUrlJson.getString(AuthzConstants.ID_);

			RpcResponse<List<Map>> res = functionItemBaseQueryService.getUrlIdByItemId(itemId);
			if (res.isSuccess()) {
				logger.info(String.format("[getMenuIdByitemId()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getMenuIdByitemId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getMenuIdByitemId()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:校验功能项名称是否重复
	 * @param itemInfo
	 * @return Result<Boolean>
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.CHECK_ITEM_NAME, method = RequestMethod.POST)
	public Result<Boolean> checkItemName(@RequestBody String itemInfo) {
		logger.info(String.format("[checkItemName()->request param:%s]", itemInfo));
		try {
			// 参数基础校验
			Result<Boolean> checResult = ExceptionChecked.checkRequestParam(itemInfo);
			if (null != checResult) {
				logger.error(String.format("[checkItemName()->fail:%s]", itemInfo));
				return checResult;
			}

			// 获取功能项id
			JSONObject itemJson = JSON.parseObject(itemInfo);
			String itemName = itemJson.getString(AuthzConstants.ITEM_NAME);
			String accessType = itemJson.getString(AuthzConstants.ACCESS_TYPE);

			RpcResponse<Boolean> res = functionItemBaseQueryService.checkItemName(itemName, accessType);
			if (res.isSuccess()) {
				logger.info(String.format("[checkItemName()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkItemName()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[checkItemName()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据菜单id查询功能项信息
	 * @param itemInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_ITEM_BY_MENU_ID, method = RequestMethod.GET)
	public Result<List<Map>> getItemInfoByMenuId(@PathVariable String menuId) {
		try {
			RpcResponse<List<Map>> itemInfo = functionItemBaseQueryService.getItemBymenuId(menuId);
			if (itemInfo.isSuccess()) {
				logger.info(String.format("[getItemInfoByMenuId()->success:%s]", itemInfo.getMessage()));
				return ResultBuilder.successResult(itemInfo.getSuccessValue(), itemInfo.getMessage());
			} else {
				logger.error(String.format("[getItemInfoByMenuId()->fail:%s]", itemInfo.getMessage()));
				return ResultBuilder.failResult(itemInfo.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getItemInfoByMenuId()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
