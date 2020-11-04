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

package com.run.usc.base.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.WriteResult;
import com.run.authz.api.constants.AuthzConstants;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;

/**
 * @Description:MongoTemplateUtil CRUD工具类
 * @author: zhabing
 * @version: 1.0, 2017年7月4日
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
		if (null != map && !map.isEmpty()) {
			logger.debug(String.format("[%s()->success:%s]", methodName, map));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, map);
		} else {
			logger.debug(String.format("[%s()->fail:%s]", methodName, AuthzConstants.GET_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.GET_FAIL);
		}
	}



	/**
	 * 
	 * @Description:插入json参数都用进行的步骤，所以做统一封装
	 * @param objectToSave
	 * @param collectionName
	 */
	public RpcResponse<JSONObject> insert(Logger logger, String methodName, JSONObject objectToSave, String collectionName) {
		// 接入方编号
		String id = UUID.randomUUID().toString().replace("-", "");
		objectToSave.put(AuthzConstants.ID_, id);
		objectToSave.put(AuthzConstants.CREATE_TIME, DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
		objectToSave.put(AuthzConstants.UPDATE_TIME, DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));

		// 去除权限验证信息
		objectToSave.remove(AuthzConstants.AUTHZ_INFO);

		objectToSave.put(AuthzConstants.IS_DELETE, AuthzConstants.STATE_NORMAL_ONE);
		objectToSave.put(AuthzConstants.STATE, AuthzConstants.STATE_NORMAL_ONE);// 0停用1正常

		mongoTemplate.insert(objectToSave, collectionName);

		Query query = new Query();
		Criteria criteria = Criteria.where(AuthzConstants.ID_).is(id);
		query.addCriteria(criteria);

		JSONObject map = mongoTemplate.findOne(query, JSONObject.class, collectionName);

		if (null != map && !map.isEmpty()) {
			logger.debug(String.format("[%s()->success:%s]", methodName, map.toJSONString()));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.SAVE_SUCC, map);
		} else {
			logger.debug(String.format("[%s()->fail:%s]", methodName, AuthzConstants.SAVE_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.SAVE_FAIL);
		}
	}



	/**
	 * 
	 * @Description:插入json参数都用进行的步骤，所以做统一封装
	 * @param objectToSave
	 * @param collectionName
	 */
	public RpcResponse<JSONObject> insertId(Logger logger, String methodName, JSONObject objectToSave,
			String collectionName, String id) {
		objectToSave.put(AuthzConstants.ID_, id);
		objectToSave.put(AuthzConstants.CREATE_TIME, DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
		objectToSave.put(AuthzConstants.UPDATE_TIME, DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));

		// 去除权限验证信息
		objectToSave.remove(AuthzConstants.AUTHZ_INFO);

		objectToSave.put(AuthzConstants.IS_DELETE, AuthzConstants.STATE_NORMAL_ONE);
		objectToSave.put(AuthzConstants.STATE, AuthzConstants.STATE_NORMAL_ONE);

		mongoTemplate.insert(objectToSave, collectionName);

		Query query = new Query();
		Criteria criteria = Criteria.where(AuthzConstants.ID_).is(id);
		query.addCriteria(criteria);

		JSONObject map = mongoTemplate.findOne(query, JSONObject.class, collectionName);

		if (null != map && !map.isEmpty()) {
			logger.debug(String.format("[%s()->success:%s]", methodName, map));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.SAVE_SUCC, map);
		} else {
			logger.debug(String.format("[%s()->fail:%s]", methodName, AuthzConstants.SAVE_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.SAVE_FAIL);
		}

	}



	/**
	 * 
	 * @Description:修改
	 * @param objectToSave
	 * @param collectionName
	 * @param id
	 * @return
	 */
	public RpcResponse<JSONObject> update(Logger logger, String methodName, JSONObject objectToSave, String collectionName,
			String id) {
		// 修改接入方信息
		Criteria criteria = Criteria.where(AuthzConstants.ID_).is(id);
		String tenementInfoStr = JSON.toJSONString(objectToSave);
		Update update = MongoUtils.jsonStringToUpdate(tenementInfoStr);
		update.set(AuthzConstants.UPDATE_TIME, DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
		WriteResult updateMulti = mongoTemplate.updateMulti(new Query(criteria), update, collectionName);
		if (updateMulti.getN() > 0) {
			logger.debug(String.format("[%s()->success:%s]", methodName, objectToSave));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.UPDATE_SUCC, objectToSave);
		} else {
			logger.debug(String.format("[updateAccessInfo()->fail:%s", AuthzConstants.UPDATE_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.UPDATE_FAIL);
		}
	}



	/**
	 * 
	 * @Description:批量删除
	 * @param objectToSave
	 * @param collectionName
	 * @param id
	 * @return
	 */
	public RpcResponse<JSONArray> delete(Logger logger, String methodName, String collectionName, JSONArray ids) {
		// 修改接入方信息
		Criteria criteria = Criteria.where(AuthzConstants.ID_).in(ids);
		Update update = new Update();
		update.set(AuthzConstants.IS_DELETE, AuthzConstants.STATE_STOP_ZERO);
		update.set(AuthzConstants.UPDATE_TIME, DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
		WriteResult res = mongoTemplate.updateMulti(new Query(criteria), update, collectionName);
		if (res.getN() > 0) {
			logger.debug(String.format("[%s()->deleteId:%s]", methodName, ids));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.DEL_SUCC, ids);
		} else {
			logger.debug(String.format("[%s()->fail:%s]", methodName, AuthzConstants.DEL_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.DEL_FAIL);
		}
	}



	/**
	 * 
	 * @Description:校验是否存在子类集合
	 * @param logger
	 * @param methodName
	 * @param collectionName
	 * @param parentKey
	 * @param parentValue
	 * @return
	 */
	public Boolean checkIsDelete(String collectionName, String parentKey, List<String> parentValue) {
		Query query = new Query();
		Criteria criteria = Criteria.where(parentKey).in(parentValue);
		Criteria criteriaDel = Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE);
		query.addCriteria(criteria);
		query.addCriteria(criteriaDel);
		return mongoTemplate.exists(query, collectionName);
	}



	/**
	 * 
	 * @Description:批量更改状态信息
	 * @param logger
	 * @param methodName
	 * @param collectionName
	 * @param parentKey
	 * @param parentValue
	 * @return
	 */
	public int switchState(String collectionName, String parentKey, List<String> parentValue, String state) {
		Query query = new Query();
		Criteria criteria = Criteria.where(parentKey).in(parentValue);
		query.addCriteria(criteria);
		Update update = new Update();
		update.set(AuthzConstants.STATE, state);
		update.set(AuthzConstants.UPDATE_TIME, DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));

		return mongoTemplate.updateMulti(query, update, collectionName).getN();
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



	/**
	 * 
	 * @Description:重名校验
	 * @param name
	 * @param parentId
	 *            父类id
	 * @param selfId
	 *            需要排除的id，一般用户更新
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private boolean nameCheck(String parentId, String parentKey, String name, String nameKey, String selfId,
			String selfKey, String collectionName) {
		Query queryT = new Query();
		Criteria criteriaT = Criteria.where(nameKey).is(name).and(AuthzConstants.IS_DELETE)
				.is(AuthzConstants.STATE_NORMAL_ONE).and(parentKey).is(parentId);

		criteriaT.and(selfKey).nin(selfId);
		queryT.addCriteria(criteriaT);
		List<? extends Map<String, Object>> tenementInfoListT = (List<? extends Map<String, Object>>) mongoTemplate
				.find(queryT, new HashMap<String, Object>().getClass(), collectionName);
		if (tenementInfoListT != null && !tenementInfoListT.isEmpty()) {
			return true;
		}
		return false;
	}

}
