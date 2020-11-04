/*
 * File name: WhiteBlackQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年9月4日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.service.query.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.api.constants.AuthzUrlConstants;
import com.run.authz.base.query.WhiteBlackBaseQueryService;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Pagination;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;

/**
 * @Description: 黑白名单控制类
 * @author: zhabing
 * @version: 1.0, 2017年9月4日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = AuthzUrlConstants.WHITE_BLACK)
public class WhiteBlackQueryController {

	private static final Logger			logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private WhiteBlackBaseQueryService	whiteBlackQuery;



	/**
	 * 
	 * @Description:分页查询黑白名单信息
	 * @param userRolePageInfo
	 *            角色分页信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_WHITE_BLACK_BYPAGE, method = RequestMethod.POST)
	public Result getWhiteBlackByPage(@RequestBody String whiteBlackPageInfo) {
		logger.info(String.format("[getUserRoleByPage()->request param:%s]", whiteBlackPageInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(whiteBlackPageInfo);
			if (null != checResult) {
				logger.error(String.format("[getUserRoleByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}

			JSONObject pageInfo = JSON.parseObject(whiteBlackPageInfo);

			RpcResponse<Pagination<Map<String, Object>>> res = whiteBlackQuery.getWhiteBlackByPage(pageInfo);
			if (res.isSuccess()) {
				logger.info(String.format("[getUserRoleByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getUserRoleByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getUserRoleByPage()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:分页查询黑白名单信息
	 * @param userRolePageInfo
	 *            角色分页信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.CHECK_WHITE_BLACK_NAME, method = RequestMethod.POST)
	public Result checkWhileBlackName(@RequestBody String nameInfo) {
		logger.info(String.format("[checkWhileBlackName()->request param:%s]", nameInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(nameInfo);
			if (null != checResult) {
				logger.error(String.format("[checkWhileBlackName()->fail:%s]", checResult.getException()));
				return checResult;
			}

			JSONObject nameJson = JSON.parseObject(nameInfo);
			String name = nameJson.getString(AuthzConstants.NAME);

			RpcResponse<Boolean> res = whiteBlackQuery.checkWhileBlackName(name);
			if (res.isSuccess()) {
				logger.info(String.format("[checkWhileBlackName()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkWhileBlackName()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[checkWhileBlackName()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
