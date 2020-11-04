/*
 * File name: WhileBlackQueryRpcSerImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年8月16日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.service.query;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.api.constants.MongodbConstants;
import com.run.authz.base.query.WhiteBlackBaseQueryService;
import com.run.authz.service.util.MongoPageUtil;
import com.run.authz.service.util.MongoTemplateUtil;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;

/**
 * @Description: 黑白名单查询Rpc
 * @author: zhabing
 * @version: 1.0, 2017年8月16日
 */

public class WhiteBlackQueryRpcSerImpl implements WhiteBlackBaseQueryService {

	private static final Logger	logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private MongoTemplateUtil	mongoTemplateUtil;
	@Autowired
	private MongoTemplate		mongoTemplate;



	/**
	 * @see com.run.gateway.base.query.WhiteBlackBaseQueryService#getWhileBlackListByType(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<List<String>> getWhileBlackListByType(String type) {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(type)) {
				logger.error(String.format("[getWhileBlackListByType()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(AuthzConstants.STATE).is(AuthzConstants.STATE_NORMAL_ONE));

			// 查询黑白名单列表
			List<String> addressList = mongoTemplateUtil.getListByKey(MongodbConstants.MONGODB_IP_WHITE_AND_BLACK_COLL,
					query, AuthzConstants.ADDRESS);

			if (null != addressList && addressList.size() != 0) {
				logger.info(String.format("[getWhileBlackListByType()->error:%s-->%s]", AuthzConstants.GET_SUCC,
						addressList));
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, addressList);
			} else {
				logger.error(String.format("[getWhileBlackListByType()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.GET_SUCC);
			}

		} catch (Exception e) {
			logger.error("[getWhileBlackListByType()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.WhiteBlackBaseQueryService#getWhiteBlackByPage(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getWhiteBlackByPage(JSONObject pageInfo) throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getWhiteBlackByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页页数
			String pageNumber = pageInfo.getString(AuthzConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getWhiteBlackByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页大小
			String pageSize = pageInfo.getString(AuthzConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = AuthzConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getWhiteBlackByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 组装查询条件
			String whiteBlackName = (String) pageInfo.get(AuthzConstants.NAME);

			// 黑名单或者白名单
			String type = (String) pageInfo.get(AuthzConstants.TYPE);

			Pattern patternPermiName = Pattern
					.compile(AuthzConstants.REGX_LEFT + whiteBlackName + AuthzConstants.REGX_RIGHT);
			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, AuthzConstants.UPDATE_TIME)));
			Criteria criteria = Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE);
			if (!StringUtils.isEmpty(whiteBlackName)) {
				criteria.and(AuthzConstants.NAME).regex(patternPermiName);
			}
			if (!StringUtils.isEmpty(type)) {
				query.addCriteria(Criteria.where(AuthzConstants.TYPE).is(type));
			}
			query.addCriteria(criteria);
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					mongoTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_IP_WHITE_AND_BLACK_COLL);

			logger.debug(String.format("[getWhiteBlackByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getWhiteBlackByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.WhiteBlackBaseQueryService#checkWhileBlackName(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> checkWhileBlackName(String name) {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(name)) {
				logger.error(String.format("[checkWhileBlackName()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.NAME));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			Query query = new Query(Criteria.where(AuthzConstants.NAME).is(name));
			query.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(AuthzConstants.STATE).is(AuthzConstants.STATE_NORMAL_ONE));

			// 查询黑白名单列表
			Boolean check = mongoTemplate.exists(query, MongodbConstants.MONGODB_IP_WHITE_AND_BLACK_COLL);
			logger.debug(String.format("[checkWhileBlackName()->success:%s]", check));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, check);

		} catch (Exception e) {
			logger.error("[checkWhileBlackName()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
