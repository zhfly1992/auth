/*
 * File name: AuthzCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月20日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.crud.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.base.crud.AuthzBaseCurdService;
import com.run.authz.api.constants.AuthzConstants;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;

/**
 * @Description: 权限中心 crud
 * @author: zhabing
 * @version: 1.0, 2017年7月20日
 */
@Service
public class AuthzCrudService {
	private static final Logger		logger	= Logger.getLogger(AuthzConstants.LOGKEY);
	@Autowired
	private AuthzBaseCurdService	authzBaseCurdService;



	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Result<Boolean> removeToken(String authzInfo) {
		logger.info(String.format("[removeToken()->request param:%s]", authzInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(authzInfo);
			if (null != checResult) {
				logger.error(String.format("[loginout()->fail:%s]", checResult.getException()));
				return checResult;
			}

			JSONObject authzObject = JSON.parseObject(authzInfo);
			// 用户id，tokenId
			String tokenId = authzObject.getString(AuthzConstants.TOKEN_ID);
			RpcResponse<Boolean> res = authzBaseCurdService.removeToken(tokenId);
			if (res.isSuccess()) {
				logger.info(String.format("[loginout()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[loginout()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[loginout()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
