/*
 * File name: UserBaseCurdServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月21日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.base.curd.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.common.collect.Maps;
import com.run.authz.api.base.crud.AuthzBaseCurdService;
import com.run.authz.api.base.util.ParamChecker;
import com.run.authz.api.constants.AuthzConstants;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;

/**
 * @Description: tokencrud
 * @author: zhabing
 * @version: 1.0, 2017年6月21日
 */
public class AuthzBaseCurdRpcServiceImpl implements AuthzBaseCurdService {

	private static final Logger				logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	@Qualifier("functionDomainRedisTemplate")
	private RedisTemplate<String, String>	redisTemplate;

	/** 1000*60*30 */
	@Value("${default.timeOut:1800000}")
	private String							defaultTimeOut;



	/**
	 * @see com.run.authz.api.base.crud.AuthzBaseCurdService#createRedisCache(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<Map> createRedisCache(String key, String value, Long timeOut) throws Exception {
		try {
			// 没有业务数据
			if (StringUtils.isBlank(key)) {
				logger.error(String.format("[createRedisCache()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.KEY));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[createRedisCache()->error:%s-->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.VALUE));
			}
			
			redisTemplate.opsForValue().set(key, value);
			
			//区分是PC端登录还是APP端登录
			String substring = key.substring(0, 3);
			if("app".equals(substring)) {
				redisTemplate.opsForValue().set("app"+value, key);
			}else if("tok".equals(substring)){
				redisTemplate.opsForValue().set("pc"+value, key);
			}
			
			if (null == timeOut || timeOut == 0) {
				redisTemplate.expire(key, Long.parseLong(defaultTimeOut), TimeUnit.MILLISECONDS);
			} else {
				redisTemplate.expire(key, timeOut, TimeUnit.MILLISECONDS);
			}
			// 返回参数值
			Map<String, String> map = Maps.newHashMap();
			map.put(AuthzConstants.KEY, key);
			map.put(AuthzConstants.VALUE, value);
			logger.info(String.format("[createRedisCache()->success:%s]", key));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.TOKEN_CREATE_SUCCESS, map);
		} catch (Exception e) {
			logger.error("[createRedisCache()->error]" + e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.authz.api.base.crud.AuthzBaseCurdService#removeToken(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> removeToken(String tokenId) throws Exception {
		try {
			// 没有用户数据
			if (StringUtils.isBlank(tokenId)) {
				logger.error(String.format("[removeToken()->error:%s--->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.TOKEN_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[removeToken()->error:%s--->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.TOKEN_ID));
			}
			// 移除库中的key-value
			redisTemplate.delete(tokenId);
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.TOKEN_REMOVE_SUCCESS, null);

		} catch (Exception e) {
			logger.error("[removeToken()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.AuthzBaseCurdService#refreshToken(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> refreshKey(String key, Long timeOut) throws Exception {

		try {
			// 校验业务参数

			if (ParamChecker.isBlank(key)) {
				logger.error(String.format("[refreshKey()->error:%s--->%s]", AuthzConstants.NO_BUSINESS, "key"));
				return RpcResponseBuilder.buildErrorRpcResp(
						String.format("[refreshKey()->error:%s--->%s]", AuthzConstants.NO_BUSINESS, "key"));
			}
			// 设置过期时间
			if (null == timeOut || 0 == timeOut) {
				redisTemplate.expire(key, Long.parseLong(defaultTimeOut), TimeUnit.MILLISECONDS);
			} else {
				redisTemplate.expire(key, timeOut, TimeUnit.MILLISECONDS);
			}
		} catch (Exception e) {
			logger.error("[refreshKey()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

		return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.KEY_REFRESH_SUCCESS, true);
	}



	/**
	 * @see com.run.authz.api.base.crud.AuthzBaseCurdService#getValueByKey(java.lang.String)
	 */
	@Override
	public RpcResponse<String> getValueByKey(String key) throws Exception {
		try {
			if (ParamChecker.isBlank(key)) {
				logger.error(String.format("[getValueByKey()->error:%s--->%s]", AuthzConstants.NO_BUSINESS, "key"));
				return RpcResponseBuilder.buildErrorRpcResp(
						String.format("[getValueByKey()->error:%s--->%s]", AuthzConstants.NO_BUSINESS, "key"));
			}
			String newToken = redisTemplate.opsForValue().get(key);
			if(null !=newToken && newToken.length()>0) {
					return RpcResponseBuilder.buildSuccessRpcResp("最新的Token查询成功！", newToken);
			}
			return RpcResponseBuilder.buildErrorRpcResp("最新的token查询失败或者没有生成最新的token!");
			
		}catch(Exception e) {
			logger.error("[getValueByKey()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		
	}
	
	
	@Override
	public RpcResponse<Boolean> createRedisCacheForKey(String key, String value){
		try {
			// 没有业务数据
			if (StringUtils.isBlank(key)) {
				logger.error(String.format("[createRedisCacheForKey()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.KEY));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[createRedisCacheForKey()->error:%s-->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.VALUE));
			}
				redisTemplate.opsForValue().set(key, value);
			// 返回参数值
			logger.info(String.format("[createRedisCacheForKey()->success:%s-%s]", key,value));
			return RpcResponseBuilder.buildSuccessRpcResp("createRedisCacheForKey success", true);
		} catch (Exception e) {
			logger.error("[createRedisCacheForKey()->error]" + e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
	
	
	

}
