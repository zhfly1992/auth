/*
 * File name: MongoTemplateUtil.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月4日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.service.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.constants.AuthzConstants;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;

/**
 * @Description:MongoTemplateUtil QUERY工具类
 * @author: zhabing
 * @version: 1.0, 2017年7月5日
 */
@Component
public class MongoTemplateUtil {
	@Autowired
	private MongoTemplate mongoTemplate;



	/**
	 * 
	 * @Description:根据表id查询表信息
	 * @param logger
	 * @param methodName
	 * @param collectionName
	 * @param id
	 * @return
	 */
	public RpcResponse<JSONObject> getModelById(Logger logger, String methodName, String collectionName, String id) {
		Query query = new Query();
		Criteria criteria = Criteria.where(AuthzConstants.ID_).is(id);
		query.addCriteria(criteria);

		JSONObject map = mongoTemplate.findOne(query, JSONObject.class, collectionName);

		if (!StringUtils.isEmpty(map)) {
			logger.debug(String.format("[%s()->success:%s]", methodName, map));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, map);
		} else {
			logger.debug(String.format("[%s()->fail:%s]", methodName, AuthzConstants.GET_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.GET_FAIL);
		}
	}



	/**
	 * 
	 * @Description:根据表父类id查询list集合
	 * @param logger
	 * @param methodName
	 * @param collectionName
	 * @param parentKey
	 * @param parentValue
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public RpcResponse<List<Map>> getModelByParentId(Logger logger, String methodName, String collectionName,
			String parentKey, String parentValue) {
		Query query = new Query();
		Criteria criteria = Criteria.where(parentKey).is(parentValue);
		query.addCriteria(criteria);

		List<Map> list = mongoTemplate.find(query, Map.class, collectionName);

		if (list != null && list.size() != 0) {
			logger.debug(String.format("[%s()->success:%s]", methodName, list));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, list);
		} else {
			logger.debug(String.format("[%s()->fail:%s]", methodName, AuthzConstants.GET_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.GET_FAIL);
		}
	}



	/**
	 * 
	 * @Description:查询单列集合
	 * @param collectionName
	 * @param queryKey
	 *            查询关键字
	 * @param query
	 *            查询条件
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getListByKey(String collectionName, Query query, String queryKey) {
		List<Map> map = mongoTemplate.find(query, Map.class, collectionName);
		List keys = new ArrayList<>();
		if (map != null && !map.isEmpty()) {
			for (Map accMap : map) {
				keys.add(accMap.get(queryKey));
			}
		}
		return keys;
	}
}
