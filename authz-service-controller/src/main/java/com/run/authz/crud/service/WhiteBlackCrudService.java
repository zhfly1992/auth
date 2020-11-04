/*
 * File name: WhiteBlackCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年9月6日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.crud.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.base.crud.WhiBlaCrudRpcService;
import com.run.authz.api.constants.AuthzConstants;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;

/**
 * @Description: 黑白名单service类
 * @author: zhabing
 * @version: 1.0, 2017年9月6日
 */
@Service
public class WhiteBlackCrudService {

	private static final Logger		logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private WhiBlaCrudRpcService	whiBlacCrudService;



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result<JSONObject> addWhiteBlack(String roleInfo) {
		logger.info(String.format("[addWhiteBlack()->request param:%s]", roleInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(roleInfo);
			if (null != checResult) {
				logger.error(String.format("[addWhiteBlack()->fail:%s]", roleInfo));
				return checResult;
			}
			RpcResponse<JSONObject> res = whiBlacCrudService.addWhiteBlack(JSON.parseObject(roleInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[addWhiteBlack()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[addWhiteBlack()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[addWhiteBlack()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result<JSONObject> updateWhiteBlack(String roleInfo) {
		logger.info(String.format("[updateWhiteBlack()->request param:%s]", roleInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(roleInfo);
			if (null != checResult) {
				logger.error(String.format("[updateWhiteBlack()->fail:%s]", roleInfo));
				return checResult;
			}
			RpcResponse<JSONObject> res = whiBlacCrudService.updateWhiteBlack(JSON.parseObject(roleInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[updateWhiteBlack()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updateWhiteBlack()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updateWhiteBlack()->exception:%s]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Result<JSONArray> delWhiteBlack(String ids) {
		logger.info(String.format("[delWhiteBlack()->request param:%s]", ids));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(ids);
			if (null != checResult) {
				logger.error(String.format("[delWhiteBlack()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject deInfo = JSON.parseObject(ids);
			// 获取删除id数组
			JSONArray delIds = deInfo.getJSONArray(AuthzConstants.IDS);
			RpcResponse<JSONArray> res = whiBlacCrudService.delWhiteBlack(delIds);
			if (res.isSuccess()) {
				logger.info(String.format("[delWhiteBlack()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[delWhiteBlack()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[delWhiteBlack()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
