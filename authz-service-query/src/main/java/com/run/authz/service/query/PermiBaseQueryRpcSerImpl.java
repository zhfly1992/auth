/*
 * File name: PermiBaseQueryRpcSerImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年8月29日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.service.query;

import java.util.ArrayList;
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
import com.run.authz.base.query.PermiBaseQueryService;
import com.run.authz.service.util.MongoPageUtil;
import com.run.authz.service.util.MongoTemplateUtil;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.governance.service.query.GovernanceServices;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.base.query.TenAccBaseQueryService;

/**
 * @Description: 权限query Rpc查询类
 * @author: zhabing
 * @version: 1.0, 2017年8月29日
 */

public class PermiBaseQueryRpcSerImpl implements PermiBaseQueryService {

	private static final Logger		logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private MongoTemplate			mongoTemplate;

	@Autowired
	private MongoTemplateUtil		tenementTemplateUtil;

	@Autowired
	private TenAccBaseQueryService	tenAccQuery;

	@Autowired
	private GovernanceServices		governance;



	/**
	 * @see com.run.authz.base.query.PermiBaseQueryService#getPermiInfoByPage(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getPermiInfoByPage(JSONObject pageInfo) throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getUserRoleInfoByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页页数
			String pageNumber = pageInfo.getString(AuthzConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getUserRoleInfoByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页大小
			String pageSize = pageInfo.getString(AuthzConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = AuthzConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getUserRoleInfoByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 组装查询条件
			String permiName = (String) pageInfo.get(AuthzConstants.PERMI_NAME);
			Object accessType = pageInfo.get(AuthzConstants.ACCESS_TYPE);

			Pattern patternPermiName = Pattern
					.compile(AuthzConstants.REGX_LEFT + permiName + AuthzConstants.REGX_RIGHT);
			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, AuthzConstants.UPDATE_TIME)));
			Criteria criteria = Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE);
			if (!StringUtils.isEmpty(permiName)) {
				criteria.and(AuthzConstants.PERMI_NAME).regex(patternPermiName);
			}
			if (!StringUtils.isEmpty(accessType)) {
				criteria.and(AuthzConstants.ACCESS_TYPE).is(accessType);
			}
			query.addCriteria(criteria);
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					mongoTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.PERMISSION_COLL);

			logger.debug(String.format("[getUserRoleInfoByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getUserRoleInfoByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.PermiBaseQueryService#getRolePermissionByPage(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getRolePermissionByPage(JSONObject pageInfo) throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getRolePermissionByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页页数
			String pageNumber = pageInfo.getString(AuthzConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getRolePermissionByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页大小
			String pageSize = pageInfo.getString(AuthzConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = AuthzConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getRolePermissionByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 已经拥有或者未拥有的标志
			String havaOwn = pageInfo.getString(AuthzConstants.HAVE_OWN);
			if (StringUtils.isEmpty(havaOwn)) {
				logger.error(String.format("[getRolePermissionByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.HAVE_OWN));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 权限id
			String roleId = pageInfo.getString(AuthzConstants.ROLE_ID);
			if (StringUtils.isEmpty(roleId)) {
				logger.error(String.format("[getRolePermissionByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.ROLE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 接入方id
			String accessId = pageInfo.getString(AuthzConstants.TENEMENT_ACCESS_ID);
			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[getRolePermissionByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.TENEMENT_ACCESS_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			Query queryPerMi = new Query(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));

			// 根据接入方id查询接入方所拥有的权限信息
			List<String> permiList = tenementTemplateUtil.getListByKey(MongodbConstants.PERMISSION_RS_ACCESS,
					queryPerMi, AuthzConstants.PERMI_ID);

			// 组装查询条件
			String permiName = (String) pageInfo.get(AuthzConstants.PERMI_NAME);

			Pattern patternPermiName = Pattern
					.compile(AuthzConstants.REGX_LEFT + permiName + AuthzConstants.REGX_RIGHT);
			Query query = new Query();

			query.with(new Sort(new Order(Direction.DESC, AuthzConstants.UPDATE_TIME)));
			Criteria criteria = Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE);
			if (!StringUtils.isEmpty(permiName)) {
				criteria.and(AuthzConstants.PERMI_NAME).regex(patternPermiName);
			}
			query.addCriteria(criteria);

			Query queryPer = new Query();
			queryPer.addCriteria(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));
			List<String> havaList = tenementTemplateUtil.getListByKey(MongodbConstants.PERMISSION_RS_ROLE, queryPer,
					AuthzConstants.PERMI_ID);

			// 筛选角色已经拥有和未拥有的权限列表
			List<String> havaListPer = new ArrayList<String>();
			List<String> ninListPer = new ArrayList<String>();

			for (String permi : permiList) {
				boolean check = false;
				for (String havaPermi : havaList) {
					if (permi.equals(havaPermi)) {
						check = true;
					}
				}
				if (check) {
					havaListPer.add(permi);
				} else {
					ninListPer.add(permi);
				}
			}

			// 查询该角色已经拥有的
			if (havaOwn.equals(AuthzConstants.STATE_NORMAL_ONE)) {
				query.addCriteria(Criteria.where(AuthzConstants.ID_).in(havaListPer));
			} else {
				query.addCriteria(Criteria.where(AuthzConstants.ID_).in(ninListPer));
			}
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					mongoTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.PERMISSION_COLL);

			List<Map<String, Object>> map = page.getDatas();
			if (null != map && !map.isEmpty()) {
				for (Map<String, Object> date : map) {
					// 根据接入方id查询接入方,租户信息
					RpcResponse<Map<String, Object>> res = tenAccQuery.getTenmentAccByAccId(accessId);
					if (res.isSuccess()) {
						Map<String, Object> maps = res.getSuccessValue();
						date.put(AuthzConstants.TENEMENT_ACCESS_NAME, maps.get(AuthzConstants.TENEMENT_ACCESS_NAME));
						date.put(AuthzConstants.TENEMENT_NAME,
								maps.get(TenementConstant.TENEMENT_ACCESS_TENEMENT_NAME));
						date.put(TenementConstant.ACCESS_TYPE, maps.get(TenementConstant.ACCESS_TYPE));
					}
				}
			}

			logger.debug(String.format("[getRolePermissionByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getRolePermissionByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.PermiBaseQueryService#getRolePermissionByPage(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getAccessPermissionByPage(JSONObject pageInfo)
			throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getAccessPermissionByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页页数
			String pageNumber = pageInfo.getString(AuthzConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getAccessPermissionByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页大小
			String pageSize = pageInfo.getString(AuthzConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = AuthzConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getAccessPermissionByPage()->error:%s-->%s]",
						AuthzConstants.CHECK_BUSINESS, AuthzConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 已经拥有或者未拥有的标志
			String havaOwn = pageInfo.getString(AuthzConstants.HAVE_OWN);
			if (StringUtils.isEmpty(havaOwn)) {
				logger.error(String.format("[getRolePermissionByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.HAVE_OWN));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 接入方id
			String accessId = pageInfo.getString(AuthzConstants.TENEMENT_ACCESS_ID);
			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[getAccessPermissionByPage()->error:%s-->%s]",
						AuthzConstants.CHECK_BUSINESS, AuthzConstants.TENEMENT_ACCESS_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 接入方类型
			String accessType = pageInfo.getString(TenementConstant.ACCESS_TYPE);
			if (StringUtils.isEmpty(accessType)) {
				logger.error(String.format("[getAccessPermissionByPage()->error:%s-->%s]",
						AuthzConstants.CHECK_BUSINESS, TenementConstant.ACCESS_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 查询这个接入方上已经授权的权限的关系表
			Query queryPer = new Query();
			queryPer.addCriteria(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));
			List<String> havaList = tenementTemplateUtil.getListByKey(MongodbConstants.PERMISSION_RS_ACCESS, queryPer,
					AuthzConstants.PERMI_ID);

			// 组装查询条件
			String permiName = (String) pageInfo.get(AuthzConstants.PERMI_NAME);

			Pattern patternPermiName = Pattern
					.compile(AuthzConstants.REGX_LEFT + permiName + AuthzConstants.REGX_RIGHT);
			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, AuthzConstants.UPDATE_TIME)));
			Criteria criteria = Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE);
			query.addCriteria(Criteria.where(TenementConstant.ACCESS_TYPE).is(accessType));
			if (!StringUtils.isEmpty(permiName)) {
				criteria.and(AuthzConstants.PERMI_NAME).regex(patternPermiName);
			}
			query.addCriteria(criteria);

			// 查询该角色已经拥有的
			if (havaOwn.equals(AuthzConstants.STATE_NORMAL_ONE)) {
				query.addCriteria(Criteria.where(AuthzConstants.ID_).in(havaList));
			} else {
				query.addCriteria(Criteria.where(AuthzConstants.ID_).nin(havaList));
			}
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					mongoTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.PERMISSION_COLL);

			logger.debug(String.format("[getAccessPermissionByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getAccessPermissionByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.PermiBaseQueryService#getRolePermissionByPage(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getRolePermiByPage(JSONObject pageInfo) throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getRolePermiByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页页数
			String pageNumber = pageInfo.getString(AuthzConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getRolePermiByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页大小
			String pageSize = pageInfo.getString(AuthzConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = AuthzConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getRolePermiByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 角色id
			String roleId = pageInfo.getString(AuthzConstants.ROLE_ID);

			// 接入方秘钥
			String accessSecret = pageInfo.getString(AuthzConstants.ACCESS_SECRET);

			if (StringUtils.isEmpty(accessSecret)) {
				logger.error(String.format("[getRolePermiByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			RpcResponse<String> resAcc = tenAccQuery.getAccessIdBySecret(accessSecret);
			String accessId = resAcc.getSuccessValue();

			// 查询这个接入方所拥有的权限信息
			Query queryAccPer = new Query();
			queryAccPer.addCriteria(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));
			List<String> accPerList = tenementTemplateUtil.getListByKey(MongodbConstants.PERMISSION_RS_ACCESS,
					queryAccPer, AuthzConstants.PERMI_ID);

			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, AuthzConstants.UPDATE_TIME)));
			query.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(AuthzConstants.STATE).is(AuthzConstants.STATE_NORMAL_ONE));
			if (StringUtils.isEmpty(roleId)) {
				query.addCriteria(Criteria.where(AuthzConstants.ID_).in(accPerList));
			} else {
				// 查询这个角色所拥有的权限id
				Query queryPer = new Query();
				queryPer.addCriteria(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));
				List<String> havaList = tenementTemplateUtil.getListByKey(MongodbConstants.PERMISSION_RS_ROLE, queryPer,
						AuthzConstants.PERMI_ID);
				query.addCriteria(Criteria.where(AuthzConstants.ID_).in(havaList));
			}

			// 判断查询app还是pc的权限
			String applicationType = pageInfo.getString(AuthzConstants.APPLICATION_TYPE);
			if (StringUtils.isEmpty(applicationType)) {
				logger.error(String.format("[getRolePermiByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.APPLICATION_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			if (AuthzConstants.APP.equals(applicationType)) {
				query.addCriteria(Criteria.where(AuthzConstants.APPLICATION_TYPE).is(applicationType));
			} else if (AuthzConstants.PC.equals(applicationType)) {
				query.addCriteria(Criteria.where(AuthzConstants.APPLICATION_TYPE).in(applicationType, null));
			}

			// 组装查询条件
			String permiName = (String) pageInfo.get(AuthzConstants.PERMI_NAME);

			Pattern patternPermiName = Pattern
					.compile(AuthzConstants.REGX_LEFT + permiName + AuthzConstants.REGX_RIGHT);

			if (!StringUtils.isEmpty(permiName)) {
				query.addCriteria(Criteria.where(AuthzConstants.PERMI_NAME).regex(patternPermiName));
			}

			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					mongoTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.PERMISSION_COLL);

			List<Map<String, Object>> map = page.getDatas();
			if (null != map && !map.isEmpty()) {
				for (Map<String, Object> date : map) {
					// 根据接入方id查询接入方,租户信息
					RpcResponse<Map<String, Object>> res = tenAccQuery.getTenmentAccByAccId(accessId);
					if (res.isSuccess()) {
						Map<String, Object> maps = res.getSuccessValue();
						date.put(AuthzConstants.TENEMENT_ACCESS_NAME, maps.get(AuthzConstants.TENEMENT_ACCESS_NAME));
						date.put(AuthzConstants.TENEMENT_NAME,
								maps.get(TenementConstant.TENEMENT_ACCESS_TENEMENT_NAME));
					}
				}
			}

			logger.debug(String.format("[getRolePermiByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getRolePermiByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.PermiBaseQueryService#getRolePermissionByPage(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getFuncPermissionByPage(JSONObject pageInfo) throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getFuncPermissionByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页页数
			String pageNumber = pageInfo.getString(AuthzConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getFuncPermissionByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页大小
			String pageSize = pageInfo.getString(AuthzConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = AuthzConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getFuncPermissionByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 已经拥有或者未拥有的标志
			String havaOwn = pageInfo.getString(AuthzConstants.HAVE_OWN);
			if (StringUtils.isEmpty(havaOwn)) {
				logger.error(String.format("[getFuncPermissionByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.HAVE_OWN));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 权限id
			String permiId = pageInfo.getString(AuthzConstants.PERMI_ID);
			if (StringUtils.isEmpty(permiId)) {
				logger.error(String.format("[getFuncPermissionByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.PERMI_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 接入方id
			String accessId = pageInfo.getString(AuthzConstants.TENEMENT_ACCESS_ID);
			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[getFuncPermissionByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.TENEMENT_ACCESS_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 组装查询条件
			String itemName = (String) pageInfo.get(AuthzConstants.ITEM_NAME);

			Pattern patternPermiName = Pattern.compile(AuthzConstants.REGX_LEFT + itemName + AuthzConstants.REGX_RIGHT);
			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, AuthzConstants.UPDATE_TIME)));
			Criteria criteria = Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE);
			query.addCriteria(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));
			if (!StringUtils.isEmpty(itemName)) {
				criteria.and(AuthzConstants.ITEM_NAME).regex(patternPermiName);
			}
			query.addCriteria(criteria);

			Query queryPer = new Query();
			queryPer.addCriteria(Criteria.where(AuthzConstants.PERMI_ID).is(permiId));
			List<String> havaList = tenementTemplateUtil.getListByKey(MongodbConstants.PERMISSION_RS_FUNC, queryPer,
					AuthzConstants.FUNC_ID);
			// 查询该角色已经拥有的
			if (havaOwn.equals(AuthzConstants.STATE_NORMAL_ONE)) {
				query.addCriteria(Criteria.where(AuthzConstants.ID_).in(havaList));
			} else {
				query.addCriteria(Criteria.where(AuthzConstants.ID_).nin(havaList));
			}
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					mongoTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.FUNCTION_ITEM);

			List<Map<String, Object>> map = page.getDatas();
			if (null != map && !map.isEmpty()) {
				for (Map<String, Object> date : map) {
					// 根据接入方id查询接入方,租户信息
					RpcResponse<Map<String, Object>> res = tenAccQuery.getTenmentAccByAccId(accessId);
					if (res.isSuccess()) {
						Map<String, Object> maps = res.getSuccessValue();
						date.put(AuthzConstants.TENEMENT_ACCESS_NAME, maps.get(AuthzConstants.TENEMENT_ACCESS_NAME));
						date.put(AuthzConstants.TENEMENT_NAME,
								maps.get(TenementConstant.TENEMENT_ACCESS_TENEMENT_NAME));
					}
				}
			}

			logger.debug(String.format("[getFuncPermissionByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getFuncPermissionByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#checkPermiName(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> checkPermiName(String accessType, String permiName) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(accessType)) {
				logger.error(String.format("[checkPermiName()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ACCESS_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(permiName)) {
				logger.error(String.format("[checkPermiName()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PERMI_NAME));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 判断该接入方下面的权限是否存在该名称
			Query queryPre = new Query();

			queryPre.addCriteria(Criteria.where(AuthzConstants.PERMI_NAME).is(permiName));
			queryPre.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
			queryPre.addCriteria(Criteria.where(AuthzConstants.ACCESS_TYPE).is(accessType));

			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC,
					mongoTemplate.exists(queryPre, MongodbConstants.PERMISSION_COLL));
		} catch (Exception e) {
			logger.error("[checkPermiName()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.PermiBaseQueryService#getInterListByUserId(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<List<Map>> getInterListByUserId(String userId) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(userId)) {
				logger.error(String.format("[getInterListByUserId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 根据用户id查询所拥有的角色id
			Query queryRoleId = new Query(Criteria.where(AuthzConstants.USER_ID).is(userId));
			List<String> roleIds = tenementTemplateUtil.getListByKey(MongodbConstants.ROLE_RS_USER, queryRoleId,
					AuthzConstants.ROLE_ID);

			// 根据角色id集合查询权限id集合
			Query queryPermi = new Query(Criteria.where(AuthzConstants.ROLE_ID).in(roleIds));
			List<String> permiIds = tenementTemplateUtil.getListByKey(MongodbConstants.PERMISSION_RS_ROLE, queryPermi,
					AuthzConstants.PERMI_ID);

			// 根据权限id结合查询功能项集合
			Query queryFunc = new Query(Criteria.where(AuthzConstants.PERMI_ID).in(permiIds));
			List<String> funcIds = tenementTemplateUtil.getListByKey(MongodbConstants.PERMISSION_RS_FUNC, queryFunc,
					AuthzConstants.FUNC_ID);

			// 根据功能项id查询功能项所关联的rest接口
			Query queryUrl = new Query(Criteria.where(AuthzConstants.ITEM_ID).in(funcIds));
			List<String> urlIds = tenementTemplateUtil.getListByKey(MongodbConstants.ITEM_RS_INTER_URL, queryUrl,
					AuthzConstants.URLID);

			// 根据接口集合查询接口信息
			RpcResponse<List<Map>> res = governance.getInterfaceByUrlIds(urlIds);
			return res;
		} catch (Exception e) {
			logger.error("[checkPermiName()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getItemInfoByPermisId(String permisId) throws Exception {
		try {
			if (org.apache.commons.lang3.StringUtils.isBlank(permisId)) {
				logger.error(String.format("[getItemInfoByPermisId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ROLE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.PERMI_ID).is(permisId));
			List<Map> find = mongoTemplate.find(query, Map.class, MongodbConstants.PERMISSION_RS_FUNC);
			if (find == null || find.isEmpty()) {
				logger.debug(String.format("[getItemInfoByPermisId()->error:%s]", AuthzConstants.GET_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.GET_FAIL);
			} else {
				logger.debug(String.format("[getItemInfoByPermisId()->error:%s]", AuthzConstants.GET_SUCC));
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, find);
			}
		} catch (Exception e) {
			logger.error("[getItemInfoByPermisId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<String>> getPermiIdIdByRoleId(String roleId) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(roleId)) {
				logger.error(String.format("[getPermiIdIdByRoleId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ROLE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 根据角色id查询权限id
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));

			List<Map> permiIdlist = mongoTemplate.find(query, Map.class, MongodbConstants.PERMISSION_RS_ROLE);
			ArrayList<String> resultList = new ArrayList<>();
			if (null != permiIdlist && permiIdlist.size() > 0) {
				for (int i = 0; i < permiIdlist.size(); i++) {
					resultList.add(permiIdlist.get(i).get(AuthzConstants.PERMI_ID).toString());
				}
			}

			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, resultList);
		} catch (Exception e) {
			logger.error("[getPermiIdIdByRoleId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.PermiBaseQueryService#checkRoleNormal(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean checkRoleNormal(String roleId) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where(AuthzConstants.STATE).is(AuthzConstants.STATE_NORMAL_ONE));
		query.addCriteria(Criteria.where(AuthzConstants.ID_).is(roleId));
		Map<String, Object> map = mongoTemplate.findOne(query, Map.class, MongodbConstants.MONGODB_USERROLE_COLL);
		if (map != null && map.size() != 0) {
			return true;
		}
		return false;
	}

}
