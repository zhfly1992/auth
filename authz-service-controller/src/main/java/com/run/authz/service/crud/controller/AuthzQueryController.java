/*
 * File name: UscQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月22日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.service.crud.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.api.constants.AuthzUrlConstants;
import com.run.authz.base.query.AuthzBaseQueryService;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;

/**
 * @Description: token查询类
 * @author: zhabing
 * @version: 1.0, 2017年6月22日
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = AuthzUrlConstants.API_USER_TOKEN)
public class AuthzQueryController {

	private static final Logger		logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private AuthzBaseQueryService	authzqueryRpcService;



	/**
	 * 
	 * @Description:验证token
	 * @param registerInfo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.API_CODE_AUTHENTICATE, method = RequestMethod.POST)
	public Result authenticate(@RequestBody String authInfo) {
		logger.info("[authenticate()->request param:" + authInfo + "]");
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(authInfo);
			if (null != checResult) {
				logger.error("[authenticate()->fail:" + checResult.getException() + "]");
				return checResult;
			}
			RpcResponse res = authzqueryRpcService.authenticate(JSON.parseObject(authInfo));
			if (res.isSuccess()) {
				logger.info("[authenticate()->success:" + res.getMessage() + "]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[authenticate()->fail:" + res.getMessage() + "]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[authenticate()->exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@RequestMapping(value = AuthzUrlConstants.API_CODE_GET_USERID + "/{tokenId}", method = RequestMethod.POST)
	public Result getUserId(@PathVariable String tokenId) {
		logger.info("[getUserId()->request param:" + tokenId + "]");
		try {
			RpcResponse res = authzqueryRpcService.getCacheValueById(tokenId);
			if (res.isSuccess()) {
				logger.info("[getUserId()->success:" + res.getMessage() + "]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[getUserId()->fail:" + res.getMessage() + "]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getUserId()->exception:" + e.getMessage() + "]");
			return ResultBuilder.exceptionResult(e);
		}
	}

}
