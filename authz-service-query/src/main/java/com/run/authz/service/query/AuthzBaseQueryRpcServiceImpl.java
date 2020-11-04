/*
 * File name: UserBaseQueryRpcServiceImpl.java
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

package com.run.authz.service.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.base.query.AuthzBaseQueryService;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;

/**
 * @Description: 权限中心token查询服务
 * @author: zhabing
 * @version: 1.0, 2017年6月22日
 */

public class AuthzBaseQueryRpcServiceImpl implements AuthzBaseQueryService {

	private static final Logger				logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private RedisTemplate<String, String>	redisTemplate;



	/**
	 * @see com.run.authz.base.query.AuthzBaseQueryService#authenticate(java.lang.String)
	 */
	@Override
	public RpcResponse<?> authenticate(JSONObject authInfoJson) throws Exception {
		try {
			// 没有用户数据
			if (!authInfoJson.containsKey(AuthzConstants.QU_INFO)) {
				logger.error(
						"[authenticate()->error:" + AuthzConstants.NO_BUSINESS + ":" + AuthzConstants.QU_INFO + "]");
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS + ":" + AuthzConstants.QU_INFO);
			}
			// 获取传入的用户数据
			JSONObject quInfoJson = authInfoJson.getJSONObject(AuthzConstants.QU_INFO);
			// 获取参数token
			String token = quInfoJson.getString(AuthzConstants.TOKEN);
			// 获取参数userID
			String userId = quInfoJson.getString(AuthzConstants.USER_ID);
			// 根据token获取用户
			String redisUserId = redisTemplate.opsForValue().get(token);

			if (userId != null && redisUserId != null && userId.equals(redisUserId.split("-")[0])) {
				logger.debug("[authenticate()->success:" + AuthzConstants.TOKEN_AUTHZ_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.TOKEN_AUTHZ_SUCCESS, true);
			} else {
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.TOKEN_AUTHZ_FAIL, false);
			}
		} catch (Exception e) {
			logger.error("[authenticate()->error:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.AuthzBaseQueryService#getUserId(java.lang.String)
	 */
	@Override
	public RpcResponse<String> getCacheValueById(String key) {
		try {
			// 没有业务数据
			if (StringUtils.isBlank(key)) {
				logger.error(String.format("[getCacheValueById()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.QU_INFO));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			String value = redisTemplate.opsForValue().get(key);
			if (StringUtils.isBlank(value)) {
				logger.error(AuthzConstants.TOKEN_GET_USERID_FAIL);
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.TOKEN_GET_USERID_FAIL);
			}
			// 返回参数值
			logger.debug(String.format("[getCacheValueById()->success:%s-->%s]",
					AuthzConstants.TOKEN_GET_USERID_SUCCESS, value));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.TOKEN_GET_USERID_SUCCESS, value);
		} catch (Exception e) {
			logger.error("[getCacheValueById()->error:%s]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
