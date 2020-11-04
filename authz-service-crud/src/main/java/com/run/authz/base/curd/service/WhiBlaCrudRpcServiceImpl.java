/*
 * File name: WhiBlaCrudRpcServiceImpl.java
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

package com.run.authz.base.curd.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.base.crud.WhiBlaCrudRpcService;
import com.run.authz.api.base.util.ExceptionChecked;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.api.constants.MongodbConstants;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.base.util.MongoTemplateUtil;

/**
 * @Description:
 * @author: zhabing
 * @version: 1.0, 2017年9月6日
 */

public class WhiBlaCrudRpcServiceImpl implements WhiBlaCrudRpcService {

	private static final Logger	logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private MongoTemplateUtil	mongoTemplateUtil;
	@Autowired
	private MongoTemplate		mongoTemplate;



	/**
	 * @see com.run.authz.api.base.crud.WhiBlaCrudRpcService#addWhiteBlack(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> addWhiteBlack(JSONObject whiteBlackInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "addWhiteBlack", whiteBlackInfo,
					AuthzConstants.NAME, AuthzConstants.TYPE, AuthzConstants.ADDRESS);
			if (rs != null) {
				return rs;
			}

			// 黑白名单名称
			String name = whiteBlackInfo.getString(AuthzConstants.NAME);
			// 重名校验
			if (nameCheck(name, null)) {
				logger.debug(String.format("[addWhiteBlack()->fail:%s]", AuthzConstants.ROLE_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.ROLE_SAVE_FAIL_NAME_EXITES);
			}

			// 插入黑白名单表
			RpcResponse<JSONObject> res = mongoTemplateUtil.insert(logger, "addWhiteBlack", whiteBlackInfo,
					MongodbConstants.MONGODB_IP_WHITE_AND_BLACK_COLL);
			return res;

		} catch (Exception e) {
			logger.error("[addWhiteBlack()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:黑白名单重名验证
	 * 
	 * @param whiteBlackName
	 *            黑白名单名称
	 * @param silfId
	 *            自身id
	 * @return
	 */
	private boolean nameCheck(String roleName, String silfId) {
		// 角色重名校验
		Query queryRole = new Query();
		queryRole.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
		queryRole.addCriteria(Criteria.where(AuthzConstants.ROLE_NAME).is(roleName));

		if (!StringUtils.isEmpty(silfId)) {
			queryRole.addCriteria(Criteria.where(AuthzConstants.ID_).nin(silfId));
		}
		return mongoTemplate.exists(queryRole, MongodbConstants.MONGODB_IP_WHITE_AND_BLACK_COLL);
	}



	/**
	 * @see com.run.authz.api.base.crud.WhiBlaCrudRpcService#updateWhiteBlack(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> updateWhiteBlack(JSONObject whiteBlackInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "updateWhiteBlack", whiteBlackInfo,
					AuthzConstants.ID_, AuthzConstants.NAME, AuthzConstants.TYPE, AuthzConstants.ADDRESS);
			if (rs != null) {
				return rs;
			}

			// 黑白名单名称
			String name = whiteBlackInfo.getString(AuthzConstants.NAME);
			String id = whiteBlackInfo.getString(AuthzConstants.ID_);
			// 重名校验
			if (nameCheck(name, id)) {
				logger.debug(String.format("[updateWhiteBlack()->fail:%s]", AuthzConstants.ROLE_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.ROLE_SAVE_FAIL_NAME_EXITES);
			}

			// 修改黑白名单表
			RpcResponse<JSONObject> res = mongoTemplateUtil.update(logger, "updateWhiteBlack", whiteBlackInfo,
					MongodbConstants.MONGODB_IP_WHITE_AND_BLACK_COLL, id);
			return res;

		} catch (Exception e) {
			logger.error("[updateWhiteBlack()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.WhiBlaCrudRpcService#delWhiteBlack(com.alibaba.fastjson.JSONArray)
	 */
	@Override
	public RpcResponse<JSONArray> delWhiteBlack(JSONArray ids) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(ids) || ids.isEmpty()) {
				logger.error(String.format("[delWhiteBlack()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.IDS));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			RpcResponse<JSONArray> res = mongoTemplateUtil.delete(logger, "delWhiteBlack",
					MongodbConstants.MONGODB_IP_WHITE_AND_BLACK_COLL, ids);
			return res;
		} catch (Exception e) {
			logger.error("[delWhiteBlack()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
