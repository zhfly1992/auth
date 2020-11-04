/*
 * File name: UserRoleQueryRpcSerImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月19日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.service.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.base.util.ExceptionChecked;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.api.constants.MongodbConstants;
import com.run.authz.base.query.UserRoleBaseQueryService;
import com.run.authz.service.util.MongoPageUtil;
import com.run.authz.service.util.MongoTemplateUtil;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.governance.contant.ParamGovernanceConstants;
import com.run.governance.service.query.GovernanceServices;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.query.AccSourceBaseQueryService;
import com.run.usc.base.query.AccUserBaseQueryService;
import com.run.usc.base.query.TenAccBaseQueryService;
import com.run.usc.base.query.TenementBaseQueryService;
import com.run.usc.base.query.UserBaseQueryService;

/**
 * @Description:用户角色查询rpc类
 * @author: zhabing
 * @version: 1.0, 2017年7月19日
 */

public class UserRoleQueryRpcSerImpl implements UserRoleBaseQueryService {

	private static final Logger			logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private MongoTemplate				tenementTemplate;

	@Autowired
	private MongoTemplateUtil			tenementTemplateUtil;

	@Autowired
	private AccSourceBaseQueryService	accSourceQuery;

	@Autowired
	private TenAccBaseQueryService		tenAccQuery;

	@Autowired
	private UserBaseQueryService		userQuery;

	@Autowired
	private AccUserBaseQueryService		accUser;

	@Autowired
	private GovernanceServices			goverService;

	@Autowired
	private UserRoleBaseQueryService	userRoleQuery;

	@Autowired
	private TenementBaseQueryService	tenement;



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getUserRoleInfoByPage(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getUserRoleInfoByPage(Map<String, String> pageInfo)
			throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getUserRoleInfoByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页页数
			String pageNumber = pageInfo.get(AuthzConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getUserRoleInfoByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页大小
			String pageSize = pageInfo.get(AuthzConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = AuthzConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getUserRoleInfoByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 组装查询条件
			String roleName = pageInfo.get(AuthzConstants.ROLE_NAME);
			String tenementName = pageInfo.get(AuthzConstants.TENEMENT_NAME);
			String accessId = pageInfo.get(AuthzConstants.TENEMENT_ACCESS_ID);

			Pattern patternTenement = Pattern.compile(AuthzConstants.REGX_LEFT + roleName + AuthzConstants.REGX_RIGHT);

			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, AuthzConstants.UPDATE_TIME)));
			Criteria criteria = Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE);
			if (!StringUtils.isEmpty(roleName)) {
				criteria.and(AuthzConstants.ROLE_NAME).regex(patternTenement);
			}
			query.addCriteria(criteria);

			if (!StringUtils.isEmpty(tenementName)) {
				// 模糊查询租户集合
				List<String> accessIdList = tenement.getAccessListByTenementName(tenementName, accessId);
				query.addCriteria(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).in(accessIdList));
			}
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_USERROLE_COLL);
			if (page.getTotalCount() > 0) {
				List<Map<String, Object>> listRole = page.getDatas();
				for (Map<String, Object> map : listRole) {
					String accId = map.get(AuthzConstants.TENEMENT_ACCESS_ID) + "";
					RpcResponse<Map<String, Object>> res = tenAccQuery.getTenmentAccByAccId(accId);
					if (res.isSuccess()) {
						map.putAll(res.getSuccessValue());
					}

				}
			}

			logger.debug(String.format("[getUserRoleInfoByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getUserRoleInfoByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getAccUserRoleInfoByPage(java.util.Map)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getAccUserRoleInfoByPage(JSONObject pageInfo) throws Exception {
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

			// 根据接入方秘钥查询接入方id
			String accessSecret = pageInfo.getString(AuthzConstants.ACCESS_SECRET);
			if (null == accessSecret) {
				logger.error(String.format("[getUserRoleInfoByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getUserRoleInfoByPage()->error:%s-->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.ACCESS_SECRET));
			}

			RpcResponse<String> res = tenAccQuery.getAccessIdBySecret(accessSecret);
			String accessId = null;
			if (res.isSuccess()) {
				accessId = res.getSuccessValue();
			} else {
				logger.error(String.format("[getUserRoleInfoByPage()->error:%s]", res.getMessage()));
				return RpcResponseBuilder
						.buildErrorRpcResp(String.format("[getUserRoleInfoByPage()->error:%s]", res.getMessage()));
			}

			String state = pageInfo.getString("state");

			// 组织id
			String orgId = pageInfo.getString(AuthzConstants.ORGANIZED_ID);

			Query query = new Query();
			// 基础查询条件
			query.with(new Sort(new Order(Direction.DESC, AuthzConstants.UPDATE_TIME)));
			query.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));

			// 接入方id查询条件
			query.addCriteria(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));

			// 组装角色条件
			String roleName = pageInfo.getString(AuthzConstants.ROLE_NAME);
			Pattern patternTenement = Pattern.compile(AuthzConstants.REGX_LEFT + roleName + AuthzConstants.REGX_RIGHT);
			if (!StringUtils.isEmpty(roleName)) {
				query.addCriteria(Criteria.where(AuthzConstants.ROLE_NAME).regex(patternTenement));
			}
			if (!org.apache.commons.lang3.StringUtils.isBlank(state)) {
				query.addCriteria(Criteria.where("state").is(state));
			}

			List<String> roIds = null;
			if (!StringUtils.isEmpty(orgId)) {
				// 查询组织下面的所有子组织id
				RpcResponse<List<String>> allOrgs = userQuery.querySourceChild(orgId);
				// 根据组织id查询组织下面所有的角色id
				Query queryRole = new Query(Criteria.where(AuthzConstants.ORGANIZED_ID).in(allOrgs.getSuccessValue()));
				roIds = tenementTemplateUtil.getListByKey(MongodbConstants.ROLE_RS_ORGAN, queryRole,
						AuthzConstants.ROLE_ID);
				query.addCriteria(Criteria.where(UscConstants.ID_).in(roIds));
			}

			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_USERROLE_COLL);
			// 获取角色id集合
			if (page.getTotalCount() != 0) {
				List<String> role = new ArrayList<>();
				List<Map<String, Object>> date = page.getDatas();
				// 组装角色id集合
				for (Map<String, Object> map : date) {
					role.add(map.get(UscConstants.ID_) + "");
				}
				// 根据角色id集合查询组织机构集合
				Query queryOrgs = new Query(Criteria.where(AuthzConstants.ROLE_ID).in(role));
				// 查询组织与角色的关联表
				List<Map> orgIdMap = tenementTemplate.find(queryOrgs, Map.class, MongodbConstants.ROLE_RS_ORGAN);
				if (orgIdMap != null && orgIdMap.size() != 0) {
					List<String> orgIds = new ArrayList<>();
					for (Map<String, Object> map : orgIdMap) {
						orgIds.add(map.get(AuthzConstants.ORGANIZED_ID) + "");
					}
					// 根据组织机构ids集合查询组织机构信息
					RpcResponse<List<Map<String, Object>>> orgs = accSourceQuery.getSourceMessByIds(orgIds);
					List<Map<String, Object>> map = null;
					if (orgs.isSuccess()) {
						map = orgs.getSuccessValue();
					} else {
						logger.error(String.format("[getUserRoleInfoByPage()->error:%s]", orgs.getMessage()));
						return RpcResponseBuilder.buildErrorRpcResp(
								String.format("[getUserRoleInfoByPage()->error:%s]", orgs.getMessage()));
					}
					// 封装组织与角色与组织名称之间的关系
					for (Map<String, Object> orgRoleRs : orgIdMap) {
						for (Map<String, Object> messOrg : map) {
							if (orgRoleRs.get(AuthzConstants.ORGANIZED_ID).equals(messOrg.get(AuthzConstants.ID_))) {
								orgRoleRs.put(AuthzConstants.ORGANIZED_NAME, messOrg.get(AuthzConstants.SOURCE_NAME));
							}
						}
					}

					// 封装组织机构名称,一个岗位可能属于多个组织
					for (Map<String, Object> mapRs : date) {
						List<Map<String, Object>> orgsMap = new ArrayList<>();
						/* if (null != orgIdMap && !orgIdMap.isEmpty()) { */
						for (int i = 0; i < orgIdMap.size(); i++) {
							Map<String, Object> orgMap = new HashMap<>();
							if (mapRs.get(AuthzConstants.ID_).equals(orgIdMap.get(i).get(AuthzConstants.ROLE_ID))) {
								orgMap.put(AuthzConstants.ORGANIZED_NAME,
										orgIdMap.get(i).get(AuthzConstants.ORGANIZED_NAME));
								orgMap.put(AuthzConstants.ORGANIZED_ID,
										orgIdMap.get(i).get(AuthzConstants.ORGANIZED_ID));
							}
							if (!orgMap.isEmpty()) {
								orgsMap.add(orgMap);
							}
						}
						mapRs.put(AuthzConstants.ORG_MESS, orgsMap);

					}
				}
			}

			logger.debug(String.format("[getUserRoleInfoByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getUserRoleInfoByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getAccUserRoleInfoByPage(java.util.Map)
	 */
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getAccInterfaceByPage(JSONObject pageInfo) throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getAccInterfaceByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页页数
			String pageNumber = pageInfo.getString(AuthzConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getAccInterfaceByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页大小
			String pageSize = pageInfo.getString(AuthzConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = AuthzConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getAccInterfaceByPage()->error:%s-->%s]", AuthzConstants.CHECK_BUSINESS,
						AuthzConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 根据接入方秘钥查询接入方id
			String accessSecret = pageInfo.getString(AuthzConstants.ACCESS_SECRET);
			if (null == accessSecret) {
				logger.error(String.format("[getAccInterfaceByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getAccInterfaceByPage()->error:%s-->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.ACCESS_SECRET));
			}

			RpcResponse<String> res = tenAccQuery.getAccessIdBySecret(accessSecret);
			String accessId = null;
			if (res.isSuccess()) {
				accessId = res.getSuccessValue();
			} else {
				logger.error(String.format("[getAccInterfaceByPage()->error:%s]", res.getMessage()));
				return RpcResponseBuilder
						.buildErrorRpcResp(String.format("[getAccInterfaceByPage()->error:%s]", res.getMessage()));
			}

			// 根据接入方id查询接入方所授权的服务信息
			RpcResponse<String[]> serverList = goverService.getAllGovernanceByaccessId(accessId);
			String[] servers = new String[0];
			if (serverList.isSuccess()) {
				servers = serverList.getSuccessValue();
			}

			// 根据serverId查询所有的服务信息
			List<String> listServerIds = Arrays.asList(servers);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put(ParamGovernanceConstants.PAGESIZE, pageSize);
			map.put(ParamGovernanceConstants.PAGENUMBER, pageNumber);
			map.put(ParamGovernanceConstants.GOVERNANCE_SERVICEIDS, listServerIds);

			RpcResponse<Pagination<Map<String, Object>>> resInterface = goverService.getInterfaceByPage(map);
			logger.debug(String.format("[getAccInterfaceByPage()->success:%s]", resInterface));
			return resInterface;
		} catch (Exception e) {
			logger.error("[getAccInterfaceByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getUserRoleInfoByUserId(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getUserRoleInfoByUserId(JSONObject pageInfo) throws Exception {
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
			// 用户id
			String userId = pageInfo.getString(AuthzConstants.USER_ID);
			if (StringUtils.isEmpty(userId)) {
				logger.error(String.format("[getUserRoleInfoByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 接入方id
			String accessId = pageInfo.getString(AuthzConstants.TENEMENT_ACCESS_ID);
			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[getUserRoleInfoByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.TENEMENT_ACCESS_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// valid查询用户拥有的角色信息 invalid 查询用户未拥有的角色信息
			String havaOwn = pageInfo.getString(AuthzConstants.HAVE_OWN);

			// 根据用户id查询用户所拥有的角色信息
			Query queryRole = new Query();
			queryRole.addCriteria(Criteria.where(AuthzConstants.USER_ID).is(userId));
			// 得到角色id List
			List<String> userRoleList = tenementTemplateUtil.getListByKey(MongodbConstants.ROLE_RS_USER, queryRole,
					AuthzConstants.ROLE_ID);
			Query queryAccRole = new Query(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));
			queryAccRole.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
			queryAccRole.addCriteria(Criteria.where(AuthzConstants.STATE).is(AuthzConstants.STATE_NORMAL_ONE));
			// 查询这个用户所属接入方的角色列表
			List<String> accRoleList = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_USERROLE_COLL,
					queryAccRole, AuthzConstants.ID_);
			// 只筛选用户在这个接入方下面的所拥有的所有角色信息
			List<String> role = new ArrayList<String>();
			// 筛选这个用户在这个接入方下面未拥有的所有角色信息
			List<String> inrole = new ArrayList<String>();
			if (null != accRoleList && accRoleList.size() != 0) {
				for (String userRole : accRoleList) {
					boolean check = false;
					for (String accRole : userRoleList) {
						if (userRole.equals(accRole)) {
							check = true;
							break;
						}
					}
					if (check) {
						role.add(userRole);
					} else {
						inrole.add(userRole);
					}
				}
			}

			Query query = new Query();
			if (AuthzConstants.STATE_NORMAL_ONE.equals(havaOwn)) {
				query.addCriteria(Criteria.where(AuthzConstants.ID_).in(role));
			} else {
				query.addCriteria(Criteria.where(AuthzConstants.ID_).in(inrole));
			}
			query.with(new Sort(new Order(Direction.DESC, AuthzConstants.UPDATE_TIME)));
			Criteria criteria = Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE);
			query.addCriteria(criteria);
			query.addCriteria(Criteria.where(AuthzConstants.STATE).is(AuthzConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));

			// 组装角色条件
			String roleName = pageInfo.getString(AuthzConstants.ROLE_NAME);
			Pattern patternTenement = Pattern.compile(AuthzConstants.REGX_LEFT + roleName + AuthzConstants.REGX_RIGHT);
			if (!StringUtils.isEmpty(roleName)) {
				query.addCriteria(Criteria.where(AuthzConstants.ROLE_NAME).regex(patternTenement));
			}

			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_USERROLE_COLL);
			logger.debug(String.format("[getUserRoleInfoByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getUserRoleInfoByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#checkUserRoleName(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> checkUserRoleName(String accessId, String roleName) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[checkUserRoleName()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.TENEMENT_ACCESS_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(roleName)) {
				logger.error(String.format("[checkUserRoleName()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ROLE_NAME));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 判断该接入方下面的角色是否存在该名称
			Query queryUser = new Query();

			queryUser.addCriteria(Criteria.where(AuthzConstants.ROLE_NAME).is(roleName));
			queryUser.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
			queryUser.addCriteria(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));

			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC,
					tenementTemplate.exists(queryUser, MongodbConstants.MONGODB_USERROLE_COLL));
		} catch (Exception e) {
			logger.error("[checkUserRoleName()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#checkUserRoleName(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Boolean> checkOrgRoleName(String orgId, String roleName) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(orgId)) {
				logger.error(String.format("[checkUserRoleName()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ORGANIZED_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(roleName)) {
				logger.error(String.format("[checkUserRoleName()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ROLE_NAME));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 判断该组织下面的所有角色信息
			Query queryRoleId = new Query();
			queryRoleId.addCriteria(Criteria.where(AuthzConstants.ORGANIZED_ID).is(orgId));

			// 查询所有的角色id集合
			List<String> roleId = tenementTemplateUtil.getListByKey(MongodbConstants.ROLE_RS_ORGAN, queryRoleId,
					AuthzConstants.ROLE_ID);

			// 根据角色id查询角色信息集合
			Query query = new Query(Criteria.where(AuthzConstants.ID_).in(roleId));
			query.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(AuthzConstants.ROLE_NAME).is(roleName));

			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC,
					tenementTemplate.exists(query, MongodbConstants.MONGODB_USERROLE_COLL));
		} catch (Exception e) {
			logger.error("[checkUserRoleName()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getUserIdByRoleId(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getUserIdByRoleId(String roleId) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(roleId)) {
				logger.error(String.format("[getUserIdByRoleId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ROLE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 根据角色id查询该角色下面所有的用户id
			Query queryUser = new Query();
			queryUser.addCriteria(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));

			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC,
					tenementTemplate.find(queryUser, Map.class, MongodbConstants.ROLE_RS_USER));
		} catch (Exception e) {
			logger.error("[getUserIdByRoleId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getRoleMessById(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Map<String, Object>> getRoleMessById(String roleId) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(roleId)) {
				logger.error(String.format("[getUserIdByRoleId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ROLE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 根据角色id查询角色信息
			Query queryUser = new Query();
			queryUser.addCriteria(Criteria.where(AuthzConstants.ID_).is(roleId));

			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC,
					tenementTemplate.findOne(queryUser, Map.class, MongodbConstants.MONGODB_USERROLE_COLL));
		} catch (Exception e) {
			logger.error("[getUserIdByRoleId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getUserMenuByToken(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<List<Map>> getUserMenuByToken(String userId, String accessId) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(userId)) {
				logger.error(String.format("[getUserIdByRoleId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[getUserIdByRoleId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.TENEMENT_ACCESS_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 根据用户id查询用户所拥有的角色信息
			Query queryRole = new Query();
			queryRole.addCriteria(Criteria.where(AuthzConstants.USER_ID).is(userId));
			// 得到角色id List
			List<String> userRoleList = tenementTemplateUtil.getListByKey(MongodbConstants.ROLE_RS_USER, queryRole,
					AuthzConstants.ROLE_ID);

			Query queryAccRole = new Query(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));
			// 查询这个用户所属接入方的角色列表
			List<String> accRoleList = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_USERROLE_COLL,
					queryAccRole, AuthzConstants.ID_);

			// 只筛选用户在这个接入方下面的所拥有的所有角色信息
			List<String> role = new ArrayList<String>();
			if (null != userRoleList && userRoleList.size() != 0) {
				for (String userRole : userRoleList) {
					for (String accRole : accRoleList) {
						if (userRole.equals(accRole)) {
							role.add(userRole);
						}
					}
				}
			}
			// 根据角色id查询所有的菜单权限
			Query queryMenu = new Query();
			queryMenu.addCriteria(Criteria.where(AuthzConstants.ROLE_ID).in(role));

			return null;
		} catch (Exception e) {
			logger.error("[getUserMenuByToken()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getUserIdByOrg(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUserIdByOrg(List<String> orgIds) throws Exception {
		// 根据用户id查询用户所拥有的角色信息
		Query queryRole = new Query(Criteria.where(AuthzConstants.ORGANIZED_ID).in(orgIds));
		// 得到角色id List
		List<String> userRoleList = tenementTemplateUtil.getListByKey(MongodbConstants.ROLE_RS_USER, queryRole,
				AuthzConstants.USER_ID);

		return userRoleList;
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getRoleMessByUserId(java.util.List)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Map> getRoleMessByUserId(String userId, String accessId) {
		// 根据用户id查询用户所拥有的角色信息
		Query queryRole = new Query(Criteria.where(AuthzConstants.USER_ID).is(userId));
		queryRole.addCriteria(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));

		// 得到角色id List
		List<Map> userRole = tenementTemplate.find(queryRole, Map.class, MongodbConstants.ROLE_RS_USER);

		if (userRole != null && userRole.size() != 0) {
			for (int i = 0; i < userRole.size(); i++) {
				Map oragin = userRole.get(i);
				// 角色id
				String roleId = userRole.get(i).get(AuthzConstants.ROLE_ID) + "";

				// 根据角色id获取角色信息
				JSONObject res = tenementTemplateUtil
						.getModelById(logger, "getRoleMessByUserId", MongodbConstants.MONGODB_USERROLE_COLL, roleId)
						.getSuccessValue();

				if (null != oragin && oragin.size() != 0) {
					userRole.get(i).put(AuthzConstants.ORGANIZED_NAME, oragin.get(AuthzConstants.SOURCE_NAME));
					userRole.get(i).put(AuthzConstants.ORGANIZED_ID, oragin.get(AuthzConstants.ORGANIZED_ID));
				}
				if (res != null) {
					userRole.get(i).put(AuthzConstants.ROLE_NAME, res.getString(AuthzConstants.ROLE_NAME));
				}
			}
		}

		return userRole;

	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getRoleMessByUserId(java.util.List)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<List<Map>> getRoleMessBySecret(String userId, String accessSecret) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(userId)) {
				logger.error(String.format("[getRoleMessBySecret()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			if (StringUtils.isEmpty(accessSecret)) {
				logger.error(String.format("[getRoleMessBySecret()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 根据接入方秘钥查询接入方信息
			RpcResponse<String> accInfo = tenAccQuery.getAccessIdBySecret(accessSecret);
			String accessId = accInfo.getSuccessValue();

			// 根据用户id查询用户所拥有的角色信息
			Query queryRole = new Query(Criteria.where(AuthzConstants.USER_ID).is(userId));
			queryRole.addCriteria(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));
			// 得到角色id List
			List<Map> userRole = tenementTemplate.find(queryRole, Map.class, MongodbConstants.ROLE_RS_USER);

			List<Map> userRoles = null;

			if (userRole != null && userRole.size() != 0) {
				userRoles = new ArrayList<>();
				for (Map map : userRole) {
					Map mapUserRole = new HashMap<>();
					// 根据角色id查询组织
					String roleId = (String) map.get(AuthzConstants.ROLE_ID);
					Query roleInfoQuery = new Query(Criteria.where(AuthzConstants.ID_).is(roleId)
							.and(AuthzConstants.STATE).is(AuthzConstants.STATE_NORMAL_ONE));
					if (!tenementTemplate.exists(roleInfoQuery, MongodbConstants.MONGODB_USERROLE_COLL)) {
						continue;// 判断角色是否停用
					}

					// 封装组织信息
					mapUserRole.put(AuthzConstants.ORGANIZED_NAME, map.get(AuthzConstants.SOURCE_NAME));
					mapUserRole.put(AuthzConstants.ORGANIZED_ID, map.get(AuthzConstants.ORGANIZED_ID));

					// 角色名称
					String roleName = tenementTemplateUtil
							.getModelById(logger, "getRoleMessBySecret", MongodbConstants.MONGODB_USERROLE_COLL, roleId)
							.getSuccessValue().getString(AuthzConstants.ROLE_NAME);
					mapUserRole.put(AuthzConstants.ROLE_NAME, roleName);
					mapUserRole.put(AuthzConstants.ROLE_ID, roleId);
					userRoles.add(mapUserRole);
				}
			}
			logger.debug(String.format("[getRoleMessBySecret()->success:%s]", userRoles));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, userRoles);
		} catch (Exception e) {
			logger.error("[getUserIdByRoleId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.api.base.crud.UserRoleBaseCrudService#checkRoleUserExist(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Boolean> checkRoleUserExist(JSONObject roleInfo) throws Exception {
		try {
			// 参数有效性校验
			RpcResponse<Boolean> rs = ExceptionChecked.checkRequestKey(logger, "checkRoleUserExist", roleInfo,
					AuthzConstants.ROLE_ID);
			if (rs != null) {
				return rs;
			}

			// 查询角色是否存在
			String roleId = roleInfo.getString(AuthzConstants.ROLE_ID);
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));
			if (!StringUtils.isEmpty(roleInfo.getString(AuthzConstants.ORGANIZED_ID))) {
				JSONArray orgIds = roleInfo.getJSONArray(AuthzConstants.ORGANIZED_ID);
				query.addCriteria(Criteria.where(AuthzConstants.ORGANIZED_ID).in(orgIds));
			}
			List<String> userIds = tenementTemplateUtil.getListByKey(MongodbConstants.ROLE_RS_USER, query,
					AuthzConstants.USER_ID);
			// 根据人员id查询人员信息
			RpcResponse<List<Map>> res = accUser.getListUserByUserIds(userIds);
			if (res.isSuccess()) {
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, true);
			} else {
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, false);
			}

		} catch (Exception e) {
			logger.error("[checkRoleUserExist()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getRoleMenuById(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<List<Map>> getRoleMenuById(String roleId, String applicationType) {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(roleId)) {
				logger.error(String.format("[getRoleMenuById()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ROLE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[checkRoleUserExist()->error:%s-->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.ROLE_ID));

			}

			if (StringUtils.isEmpty(applicationType)) {
				logger.error(String.format("[getRoleMenuById()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.APPLICATION_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[checkRoleUserExist()->error:%s-->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.APPLICATION_TYPE));

			}

			// 根据角色id查询这个角色下面所有的权限Id集合
			Query queryPermi = new Query(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));
			List<String> permiIds = tenementTemplateUtil.getListByKey(MongodbConstants.PERMISSION_RS_ROLE, queryPermi,
					AuthzConstants.PERMI_ID);

			// 根据这个权限id集合查询这个权限所包含的功能项集合
			Query queryFunc = new Query(Criteria.where(AuthzConstants.PERMI_ID).in(permiIds));
			List<String> funcIds = tenementTemplateUtil.getListByKey(MongodbConstants.PERMISSION_RS_FUNC, queryFunc,
					AuthzConstants.FUNC_ID);

			// 根据这个功能项查询菜单集合
			Query queryMenu = new Query(Criteria.where(AuthzConstants.ITEM_ID).in(funcIds));
			List<String> menuIds = tenementTemplateUtil.getListByKey(MongodbConstants.ITEM_RS_MENU, queryMenu,
					AuthzConstants.MENU_ID);

			// 去除重复的menuId
			List<String> listMenuIds = new ArrayList<>();
			HashSet hashList = new HashSet(menuIds);
			listMenuIds.addAll(hashList);

			// 根据这个子菜单结合查询子菜单所有的父类菜单
			List allMenuIds = new ArrayList<>();
			for (String menuId : listMenuIds) {
				getParentMenuIs(menuId, allMenuIds);
			}

			// 去除重复的menuId
			List<String> realMenuIds = new ArrayList<>();
			HashSet hashLists = new HashSet(allMenuIds);
			realMenuIds.addAll(hashLists);

			// 根据菜单id查询菜单资源信息
			if (realMenuIds.isEmpty()) {
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, null);
			}
			RpcResponse<List<Map>> res = accSourceQuery.getListMenuByIds(realMenuIds, applicationType);
			return res;
		} catch (Exception e) {
			logger.error("[getRoleMenuById()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getParentMenuIs(String menuId, List allMenuIds) {
		allMenuIds.add(menuId);
		Map map = accSourceQuery.getSourceMessById(menuId);
		String parentId = (String) map.get(UscConstants.PARENT_ID);
		if (!StringUtils.isEmpty(parentId)) {
			getParentMenuIs(parentId, allMenuIds);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#checkOrgUserExist(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Boolean> checkOrgUserExist(JSONObject orgInfo) {
		try {
			// 参数有效性校验
			RpcResponse<Boolean> rs = ExceptionChecked.checkRequestKey(logger, "checkOrgUserExist", orgInfo,
					AuthzConstants.ORGANIZED_ID);
			if (rs != null) {
				return rs;
			}
			// 查询组织关联的人员信息
			String organizeId = orgInfo.getString(AuthzConstants.ORGANIZED_ID);
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.ORGANIZED_ID).is(organizeId));
			List<String> userIds = tenementTemplateUtil.getListByKey(MongodbConstants.ROLE_RS_USER, query,
					AuthzConstants.USER_ID);
			// 根据人员id查询人员信息
			RpcResponse<List<Map>> res = accUser.getListUserByUserIds(userIds);
			if (res.isSuccess()) {
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, true);
			} else {
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, false);
			}
		} catch (Exception e) {
			logger.error("[checkOrgUserExist()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getRoleListByOrgId(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<List<Map>> getRoleListByOrgId(String orgId) {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(orgId)) {
				logger.error(String.format("[getRoleMenuById()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ORGANIZED_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[checkRoleUserExist()->error:%s-->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.ORGANIZED_ID));
			}

			Query query = new Query(Criteria.where(AuthzConstants.ORGANIZED_ID).is(orgId));

			// 根据组织id查询角色信息集合
			List<String> roleIds = tenementTemplateUtil.getListByKey(MongodbConstants.ROLE_RS_ORGAN, query,
					AuthzConstants.ROLE_ID);

			Query queryRole = new Query(Criteria.where(AuthzConstants.ID_).in(roleIds));
			queryRole.addCriteria(Criteria.where(AuthzConstants.STATE).is(AuthzConstants.STATE_NORMAL_ONE));
			queryRole.addCriteria(Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));

			List<Map> roleMessList = tenementTemplate.find(queryRole, Map.class,
					MongodbConstants.MONGODB_USERROLE_COLL);

			logger.debug(String.format("[getRoleMessBySecret()->success:%s]", roleMessList));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, roleMessList);
		} catch (Exception e) {
			logger.error("[getRoleMenuById()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<Map> getOrgByRoleId(String roleId) {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(roleId)) {
				logger.error(String.format("[getRoleMenuById()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ROLE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[checkRoleUserExist()->error:%s-->%s]",
						AuthzConstants.NO_BUSINESS, AuthzConstants.ROLE_ID));
			}

			Query query = new Query(Criteria.where(AuthzConstants.ROLE_ID).is(roleId));

			// 根据角色id查询组织信息集合
			Map map = tenementTemplate.findOne(query, Map.class, MongodbConstants.ROLE_RS_ORGAN);
			String organizeId = "";
			if (null != map && map.size() > 0) {
				organizeId = map.get(AuthzConstants.ORGANIZED_ID).toString();
				RpcResponse<Map> org = accSourceQuery.getSourceMessageById(organizeId);
				if (org.isSuccess()) {
					logger.debug(String.format("[getRoleMessBySecret()->success:%s]", org.getMessage()));
					return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, org.getSuccessValue());
				} else {
					logger.debug(String.format("[getRoleMessBySecret()->success:%s]", org.getMessage()));
					return RpcResponseBuilder.buildErrorRpcResp(org.getMessage());
				}
			} else {
				logger.debug(String.format("[getRoleMessBySecret()->success:%s]", AuthzConstants.GET_SUCC));
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, null);
			}

		} catch (Exception e) {
			logger.error("[getRoleMenuById()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getUserListByOrgId(JSONObject paramInfo) throws Exception {
		try {
			// 参数基础校验
			if (org.apache.commons.lang3.StringUtils.isBlank(paramInfo.getString(AuthzConstants.ORGANIZED_ID))) {
				logger.error(String.format("[getUserByOrgId()->fail:组织id不能为空！]"));
				return RpcResponseBuilder.buildErrorRpcResp("组织id不能为空！");
			}

			// 获取组织id
			String organizeId = paramInfo.getString(AuthzConstants.ORGANIZED_ID);
			String receiveSms = paramInfo.getString("receiveSms");

			List<String> list = new ArrayList<String>();
			list.add(organizeId);

			// 获取该组织下的用户id集合
			List<String> listId = userRoleQuery.getUserIdByOrg(list);

			JSONObject paramJson = new JSONObject();
			paramJson.put("userIds", listId);
			paramJson.put("receiveSms", receiveSms);

			RpcResponse<List<Map>> userList = userQuery.getUserByUserIds(paramJson);

			if (userList.isSuccess()) {
				logger.info(String.format("[getUserByOrgId()->success:%s]", userList.getMessage()));
				return RpcResponseBuilder.buildSuccessRpcResp(userList.getMessage(), userList.getSuccessValue());
			} else {
				logger.error(String.format("[getUserByOrgId()->fail:%s]", userList.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(userList.getMessage());
			}

		} catch (Exception e) {
			logger.error("[getUserByOrgId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.authz.base.query.UserRoleBaseQueryService#getUserListByRoleName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUserListByRoleName(String roleName, String accessId) {

		// 根据接入方名称和角色名称模糊查询角色id集合
		Query query = new Query(Criteria.where(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId));
		Pattern patternTenement = Pattern.compile(AuthzConstants.REGX_LEFT + roleName + AuthzConstants.REGX_RIGHT);
		if (!StringUtils.isEmpty(roleName)) {
			query.addCriteria(Criteria.where(AuthzConstants.ROLE_NAME).regex(patternTenement));
		}
		List<String> roleIds = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_USERROLE_COLL, query,
				AuthzConstants.ID_);

		// 根据角色id集合查询用户id集合
		Query queryUser = new Query(Criteria.where(AuthzConstants.ROLE_ID).in(roleIds));
		return tenementTemplateUtil.getListByKey(MongodbConstants.ROLE_RS_USER, queryUser, AuthzConstants.USER_ID);

	}



	@SuppressWarnings("rawtypes")
	public RpcResponse<List<Map>> getAllUserListByOrgId(JSONObject paramInfo) throws Exception {
		try {
			// 参数基础校验
			if (org.apache.commons.lang3.StringUtils.isBlank(paramInfo.getString(AuthzConstants.ORGANIZED_ID))) {
				logger.error(String.format("[getAllUserListByOrgId()->fail:组织id不能为空！]"));
				return RpcResponseBuilder.buildErrorRpcResp("组织id不能为空！");
			}

			// 获取组织id
			String organizeId = paramInfo.getString(AuthzConstants.ORGANIZED_ID);
			String receiveSms = paramInfo.getString("receiveSms");

			// 获取该（organizeId）组织的所有父组织id以及该组织id
			RpcResponse<List<String>> findAllOrgParentId = accSourceQuery.findAllOrgParentId(organizeId);

			// 获取该组织下的用户id集合
			List<String> listId = userRoleQuery.getUserIdByOrg(findAllOrgParentId.getSuccessValue());
			// 用户id去重
			for (int i = 0; i < listId.size(); i++) {
				if (!listId.contains(listId.get(i))) {
					listId.add(listId.get(i));
				}
			}
			JSONObject paramJson = new JSONObject();
			paramJson.put("userIds", listId);
			paramJson.put("receiveSms", receiveSms);

			RpcResponse<List<Map>> userList = userQuery.getUserByUserIds(paramJson);

			if (userList.isSuccess()) {
				logger.info(String.format("[getUserByOrgId()->success:%s]", userList.getMessage()));
				return RpcResponseBuilder.buildSuccessRpcResp(userList.getMessage(), userList.getSuccessValue());
			} else {
				logger.error(String.format("[getUserByOrgId()->fail:%s]", userList.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(userList.getMessage());
			}

		} catch (Exception e) {
			logger.error("[getUserByOrgId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
