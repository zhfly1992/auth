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

import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.api.constants.MongodbConstants;
import com.run.authz.base.query.FunctionItemBaseQueryService;
import com.run.authz.service.util.MongoPageUtil;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.base.query.AccSourceBaseQueryService;
import com.run.usc.base.query.TenAccBaseQueryService;

public class FunctionItemQueryRpcServiceImpl implements FunctionItemBaseQueryService {

	private static final Logger			logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private MongoTemplate				tenementTemplate;

	@Autowired
	private TenAccBaseQueryService		tenAccQuery;

	@Autowired
	private AccSourceBaseQueryService	accSourceQuery;



	@SuppressWarnings({ "unchecked" })
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getFunctionItemByPage(Map<String, String> pageInfo)
			throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getFunctionItemByPage()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
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
			String itemName = pageInfo.get(AuthzConstants.ITEM_NAME);
			String accessType = pageInfo.get(AuthzConstants.ACCESS_TYPE);

			Pattern patternTenement = Pattern.compile(AuthzConstants.REGX_LEFT + itemName + AuthzConstants.REGX_RIGHT);

			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, AuthzConstants.UPDATE_TIME)));
			Criteria criteria = Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE);
			if (!org.apache.commons.lang3.StringUtils.isBlank(itemName)) {
				criteria.and(AuthzConstants.ITEM_NAME).regex(patternTenement);
			}
			if (!StringUtils.isEmpty(accessType)) {
				criteria.and(AuthzConstants.ACCESS_TYPE).is(accessType);
			}
			query.addCriteria(criteria);
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.FUNCTION_ITEM);

			/*
			 * // 获取数据查询接入方租户名称信息 List<Map<String, Object>> itemList =
			 * page.getDatas(); if (!itemList.isEmpty()) { for (Map<String,
			 * Object> data : itemList) {
			 * 
			 * RpcResponse<Map<String, Object>> accessInfo = tenAccQuery
			 * .getTenmentAccByAccId(data.get(AuthzConstants.TENEMENT_ACCESS_ID)
			 * .toString()); if (!accessInfo.isSuccess()) {
			 * logger.debug(String.format("[getTenmentAccByAccId()->fail:%s]",
			 * accessInfo)); return
			 * RpcResponseBuilder.buildErrorRpcResp("查询失败:没有对应的接入方和租户信息!"); }
			 * 
			 * Map<String, Object> map = accessInfo.getSuccessValue(); if (null
			 * != map || !map.isEmpty()) {
			 * data.put(AuthzConstants.TENEMENT_ACCESS_NAME,
			 * map.get(AuthzConstants.TENEMENT_ACCESS_NAME));
			 * data.put(AuthzConstants.TENEMENT_NAME,
			 * map.get(AuthzConstants.ACCESS_TENEMENT_NAME)); } else {
			 * logger.debug(String.format("[getTenmentAccByAccId()->fail:%s]",
			 * accessInfo)); return
			 * RpcResponseBuilder.buildErrorRpcResp("查询失败:没有对应的接入方和租户信息!"); } }
			 * }
			 */

			logger.debug(String.format("[getUserRoleInfoByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error(String.format("[getUserRoleInfoByPage()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings({ "unchecked"})
	@Override
	public RpcResponse<Map<String, Object>> getFunctionItemById(String itemId) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(itemId)) {
				logger.error(String.format("[getFunctionItemById()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ID_));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 根据功能项id查询功能项信息
			Query queryItem = new Query();
			queryItem.addCriteria(Criteria.where(AuthzConstants.ID_).is(itemId));

			// 查询功能项
			Map<String, Object> itemMap = tenementTemplate.findOne(queryItem, Map.class,
					MongodbConstants.FUNCTION_ITEM);
			if (null == itemMap || itemMap.isEmpty()) {
				logger.error(String.format("[getFunctionItemById()->fail:%s]", AuthzConstants.GET_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.GET_FAIL);
			}

			// 查询接入方和租户名称信息
			RpcResponse<Map<String, Object>> accessInfo = tenAccQuery
					.getTenmentAccByAccId(itemMap.get(AuthzConstants.TENEMENT_ACCESS_ID).toString());
			if (!accessInfo.isSuccess()) {
				logger.debug(String.format("[getTenmentAccByAccId()->fail:%s]", accessInfo));
				return RpcResponseBuilder.buildErrorRpcResp("查询失败:没有对应的接入方和租户信息!");
			}

			Map<String, Object> map = accessInfo.getSuccessValue();
			if (null != map && !map.isEmpty()) {
				itemMap.put(AuthzConstants.TENEMENT_ACCESS_NAME, map.get(AuthzConstants.TENEMENT_ACCESS_NAME));
				itemMap.put(AuthzConstants.TENEMENT_NAME, map.get(AuthzConstants.ACCESS_TENEMENT_NAME));
			} else {
				logger.debug(String.format("[getTenmentAccByAccId()->fail:%s]", accessInfo));
				return RpcResponseBuilder.buildErrorRpcResp("查询失败:没有对应的接入方和租户信息!");
			}

			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, itemMap);
		} catch (Exception e) {
			logger.error(String.format("[getFunctionItemById()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> getAccessInfoBySecret(String accessSecret) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(accessSecret)) {
				logger.error(String.format("[getAccessInfoBySecret()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 根据秘钥查询接入信息
			Query queryAccessInfo = new Query();
			queryAccessInfo.addCriteria(Criteria.where(AuthzConstants.ACCESS_SECRET).is(accessSecret)
					.and(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
			RpcResponse<String> accessInfoMap = tenAccQuery.getAccessIdBySecret(accessSecret);

			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, accessInfoMap.getSuccessValue());
		} catch (Exception e) {
			logger.error(String.format("[getAccessInfoBySecret()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getFunctionItemByAccessId(Map<String, String> pageInfo,
			String accessId) throws Exception {
		try {
			// 参数校验
			if (org.apache.commons.lang3.StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[getFunctionItemByAccessId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			if (null == pageInfo) {
				logger.error(String.format("[getFunctionItemByAccessId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页页数
			String pageNumber = pageInfo.get(AuthzConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getFunctionItemByAccessId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 分页大小
			String pageSize = pageInfo.get(AuthzConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = AuthzConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getFunctionItemByAccessId()->error:%s-->%s]",
						AuthzConstants.CHECK_BUSINESS, AuthzConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, AuthzConstants.UPDATE_TIME)));
			Criteria criteria = Criteria.where(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE)
					.and(AuthzConstants.TENEMENT_ACCESS_ID).is(accessId);
			query.addCriteria(criteria);
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.FUNCTION_ITEM);

			logger.debug(String.format("[getUserRoleInfoByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error(String.format("[getUserRoleInfoByPage()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getMenuIdByItemId(String itemId) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(itemId)) {
				logger.error(String.format("[getMenuIdByItemId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ITEM_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 根据功能id查询与之关联的菜单id
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.ITEM_ID).is(itemId));

			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC,
					tenementTemplate.find(query, Map.class, MongodbConstants.ITEM_RS_MENU));
		} catch (Exception e) {
			logger.error(String.format("[getMenuIdByItemId()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getButtonIdByItemId(String itemId) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(itemId)) {
				logger.error(String.format("[getButtonIdByItemId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ITEM_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 根据功能id查询与之关联的按钮id
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.ITEM_ID).is(itemId));

			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC,
					tenementTemplate.find(query, Map.class, MongodbConstants.ITEM_RS_BUTTON));
		} catch (Exception e) {
			logger.error(String.format("[getButtonIdByItemId()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<String>> getItemIdByPermiIds(ArrayList<String> permiIds) throws Exception {
		try {
			// 参数校验
			if (null == permiIds) {
				logger.error(String.format("[getItemIdByPermiIds()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.PERMI_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 根据权限id查询与之关联的功能项id
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.PERMI_ID).in(permiIds));

			List<Map> itemIdlist = tenementTemplate.find(query, Map.class, MongodbConstants.PERMISSION_RS_FUNC);
			ArrayList<String> resultList = new ArrayList<>();
			if (null != itemIdlist && itemIdlist.size() > 0) {
				for (int i = 0; i < itemIdlist.size(); i++) {
					resultList.add(itemIdlist.get(i).get(AuthzConstants.FUNC_ID).toString());
				}
			}

			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, resultList);
		} catch (Exception e) {
			logger.error(String.format("[getButtonIdByItemId()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getListButtonByItemIds(ArrayList<String> itemIds) throws Exception {
		try {
			// 参数校验
			if (null == itemIds) {
				logger.error(String.format("[getButtonIdByItemIds()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ITEM_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 根据功能id查询与之关联的按钮id
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.ITEM_ID).in(itemIds));

			List<Map> buttonlist = tenementTemplate.find(query, Map.class, MongodbConstants.ITEM_RS_BUTTON);
			ArrayList<String> buttonIds = new ArrayList<>();
			if (null != buttonlist && buttonlist.size() > 0) {
				for (int i = 0; i < buttonlist.size(); i++) {
					buttonIds.add(buttonlist.get(i).get(AuthzConstants.BUTTON_ID).toString());
				}
			}

			RpcResponse<List<Map>> resultList = accSourceQuery.getListButtonByid(buttonIds);

			if (resultList.isSuccess()) {
				logger.info(String.format("[getButtonIdByItemIds()->error:%s]", resultList.getMessage()));
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, resultList.getSuccessValue());
			}

			logger.error(String.format("[getButtonIdByItemIds()->error:%s]", resultList.getMessage()));
			return RpcResponseBuilder.buildErrorRpcResp(resultList.getMessage());

		} catch (Exception e) {
			logger.error(String.format("[getButtonIdByItemIds()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getUrlIdByItemId(String itemId) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(itemId)) {
				logger.error(String.format("[getMenuIdByItemId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ITEM_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 根据功能id查询与之关联的utlId
			Query query = new Query();
			query.addCriteria(Criteria.where(AuthzConstants.ITEM_ID).is(itemId));

			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC,
					tenementTemplate.find(query, Map.class, MongodbConstants.ITEM_RS_INTER_URL));
		} catch (Exception e) {
			logger.error(String.format("[getMenuIdByItemId()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Boolean> checkItemName(String itemName, String accessType) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(itemName)) {
				logger.error(String.format("[checkItemName()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ITEM_NAME));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(accessType)) {
				logger.error(String.format("[checkItemName()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.ACCESS_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}

			// 重名校验
			Query queryItem = new Query();
			queryItem.addCriteria(Criteria.where(AuthzConstants.ITEM_NAME).is(itemName).and(AuthzConstants.ACCESS_TYPE)
					.is(accessType).and(AuthzConstants.IS_DELETE).is(AuthzConstants.STATE_NORMAL_ONE));
			Boolean result = tenementTemplate.exists(queryItem, MongodbConstants.FUNCTION_ITEM);

			logger.info(String.format("[checkItemName()->success:%s]", AuthzConstants.GET_SUCC));
			return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, result);
		} catch (Exception e) {
			logger.error(String.format("[checkItemName()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getItemBymenuId(String menuId) throws Exception {
		try {
			// 参数校验
			if (org.apache.commons.lang3.StringUtils.isBlank(menuId)) {
				logger.error(String.format("[getItemBymenuId()->error:%s-->%s]", AuthzConstants.NO_BUSINESS,
						AuthzConstants.MENU_ID));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.NO_BUSINESS);
			}
			// 根据菜单id查询与之关联的ItemId
			List<String> item = getMenuId(menuId);
			Query query = new Query();
			List<Map> itemInfo = getItemInfo(item, query);
			if (itemInfo == null) {
				logger.error(String.format("[getItemBymenuId()->exception:%s]", AuthzConstants.GET_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(AuthzConstants.GET_FAIL);
			} else {
				logger.info(String.format("[getItemBymenuId()->success:%s]", AuthzConstants.GET_SUCC));
				return RpcResponseBuilder.buildSuccessRpcResp(AuthzConstants.GET_SUCC, itemInfo);
			}
		} catch (Exception e) {
			logger.error(String.format("[getItemBymenuId()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	private List<Map> getItemInfo(List<String> item, Query query) {
		query.addCriteria(Criteria.where(AuthzConstants.ID_).in(item));
		List<Map> find = tenementTemplate.find(query, Map.class, MongodbConstants.FUNCTION_ITEM);
		return find;
	}



	@SuppressWarnings("rawtypes")
	private List<String> getMenuId(String menuId) {
		Query query = new Query();
		query.addCriteria(Criteria.where(AuthzConstants.MENU_ID).is(menuId));
		List<Map> find = tenementTemplate.find(query, Map.class, MongodbConstants.ITEM_RS_MENU);
		return getIds(find, AuthzConstants.ITEM_ID);
	}



	@SuppressWarnings("rawtypes")
	private List<String> getIds(List<Map> list, String key) {
		List<String> idsList = new ArrayList<>();
		for (Map map : list) {
			if (map.containsKey(key)) {
				idsList.add((String) map.get(key));
			}
		}
		return idsList;
	}

}
