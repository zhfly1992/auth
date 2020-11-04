/*
 * File name: UserRoleQueryController.java
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

package com.run.authz.service.query.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.api.constants.AuthzUrlConstants;
import com.run.authz.base.query.AuthzBaseQueryService;
import com.run.authz.base.query.FunctionItemBaseQueryService;
import com.run.authz.base.query.PermiBaseQueryService;
import com.run.authz.base.query.UserRoleBaseQueryService;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Pagination;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;

/**
 * @Description: 用户角色查询控制类
 * @author: zhabing
 * @version: 1.0, 2017年7月19日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = AuthzUrlConstants.USER_ROLE)
public class UserRoleQueryController {
	private static final Logger				logger	= Logger.getLogger(AuthzConstants.LOGKEY);

	@Autowired
	private UserRoleBaseQueryService		userRoleQuery;

	@Autowired
	private AuthzBaseQueryService			authzQuery;

	@Autowired
	private PermiBaseQueryService			permiQuery;

	@Autowired
	private FunctionItemBaseQueryService	functionItemQuery;



	/**
	 * 
	 * @Description:分页查询角色信息
	 * @param userRolePageInfo
	 *            角色分页信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_USER_ROLE_PAGE, method = RequestMethod.POST)
	public Result getUserRoleByPage(@RequestBody String userRolePageInfo) {
		logger.info(String.format("[getUserRoleByPage()->request param:%s]", userRolePageInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(userRolePageInfo);
			if (null != checResult) {
				logger.error(String.format("[getUserRoleByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject tenementJson = JSON.parseObject(userRolePageInfo);

			// 检验里面的值是否是json格式
			String pageInfoJson = tenementJson.getString(AuthzConstants.ROLE_INFO);
			Result checPage = ExceptionChecked.checkRequestParam(pageInfoJson);
			if (null != checPage) {
				logger.error(String.format("[getUserRoleByPage()->fail:%s]", checPage.getException()));
				return checPage;
			}
			JSONObject pageInfo = JSON.parseObject(pageInfoJson);

			// 分页大小
			String pageSize = pageInfo.getString(AuthzConstants.PAGESIZE);
			// 分页页数
			String pageNumber = pageInfo.getString(AuthzConstants.PAGENUMBER);
			// 查询条件
			String roleName = pageInfo.getString(AuthzConstants.ROLE_NAME);
			String tenementName = pageInfo.getString(AuthzConstants.TENEMENT_NAME);
			String accessId=pageInfo.getString(AuthzConstants.TENEMENT_ACCESS_ID);

			Map<String, String> map = new HashMap<String, String>();
			map.put(AuthzConstants.PAGESIZE, pageSize);
			map.put(AuthzConstants.PAGENUMBER, pageNumber);
			map.put(AuthzConstants.ROLE_NAME, roleName);
			map.put(AuthzConstants.TENEMENT_NAME, tenementName);
			map.put(AuthzConstants.TENEMENT_ACCESS_ID, accessId);

			RpcResponse<Pagination<Map<String, Object>>> res = userRoleQuery.getUserRoleInfoByPage(map);
			if (res.isSuccess()) {
				logger.info(String.format("[getUserRoleByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getUserRoleByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getUserRoleByPage()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:检查角色名称是否重复
	 * @param accUserNameInfo
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.CHECK_ROLE_NAME, method = RequestMethod.POST)
	public Result<Boolean> checkRoleName(@RequestBody String userRoleNameInfo) {
		logger.info(String.format("[checkRoleName()->request param:%s]", userRoleNameInfo));
		try {
			// 参数基础校验
			Result<Boolean> checResult = ExceptionChecked.checkRequestParam(userRoleNameInfo);
			if (null != checResult) {
				logger.error(String.format("[checkRoleName()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject roleInfo = JSON.parseObject(userRoleNameInfo);
			RpcResponse<Boolean> res = userRoleQuery.checkUserRoleName(
					roleInfo.getString(AuthzConstants.TENEMENT_ACCESS_ID),
					roleInfo.getString(AuthzConstants.ROLE_NAME));
			if (res.isSuccess()) {
				logger.info(String.format("[checkRoleName()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkRoleName()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[checkRoleName()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:校验岗位名称是否重复
	 * @param accUserNameInfo
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.CHECK_ORG_ROLE_NAME, method = RequestMethod.POST)
	public Result<Boolean> checkOrgRoleName(@RequestBody String userRoleNameInfo) {
		logger.info(String.format("[checkOrgRoleName()->request param:%s]", userRoleNameInfo));
		try {
			// 参数基础校验
			Result<Boolean> checResult = ExceptionChecked.checkRequestParam(userRoleNameInfo);
			if (null != checResult) {
				logger.error(String.format("[checkOrgRoleName()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject roleInfo = JSON.parseObject(userRoleNameInfo);
			RpcResponse<Boolean> res = userRoleQuery.checkOrgRoleName(roleInfo.getString(AuthzConstants.ORGANIZED_ID),
					roleInfo.getString(AuthzConstants.ROLE_NAME));
			if (res.isSuccess()) {
				logger.info(String.format("[checkOrgRoleName()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkOrgRoleName()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[checkOrgRoleName()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据用户id 分页查询该用户所拥有或者没有拥有的角色信息
	 * @param userRolePageInfo
	 *            角色分页信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_USER_ROLE_BY_USERID, method = RequestMethod.POST)
	public Result getUserRoleInfoByUserId(@RequestBody String userRolePageInfo) {
		logger.info(String.format("[getUserRoleByPage()->request param:%s]", userRolePageInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(userRolePageInfo);
			if (null != checResult) {
				logger.error(String.format("[getUserRoleByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(userRolePageInfo);
			RpcResponse<Pagination<Map<String, Object>>> res = userRoleQuery.getUserRoleInfoByUserId(pageInfo);
			if (res.isSuccess()) {
				logger.info(String.format("[getUserRoleByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getUserRoleByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getUserRoleByPage()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据token查询用户所拥有的菜单权限
	 * @param accUserNameInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_USER_MENU_BY_TOKEN, method = RequestMethod.POST)
	public Result<List> getUserMenuByToken(@RequestBody String tokenInfo) {
		logger.info(String.format("[getUserRoleInfoByToken()->request param:%s]", tokenInfo));
		try {
			// 参数基础校验
			Result<List> checResult = ExceptionChecked.checkRequestParam(tokenInfo);
			if (null != checResult) {
				logger.error(String.format("[getUserRoleInfoByToken()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject userJson = JSON.parseObject(tokenInfo);
			// 根据token查询用户id
			String token = userJson.getString(AuthzConstants.TOKEN);
			// 接入方id
			String accessId = userJson.getString(AuthzConstants.TENEMENT_ACCESS_ID);
			RpcResponse<String> userInfo = authzQuery.getCacheValueById(token);
			String userId = null;
			if (userInfo.isSuccess()) {
				userId = userInfo.getSuccessValue();
			} else {
				return ResultBuilder.failResult(userInfo.getMessage());
			}

			RpcResponse<List<Map>> res = userRoleQuery.getUserMenuByToken(userId, accessId);
			if (res.isSuccess()) {
				logger.info(String.format("[getUserRoleInfoByToken()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getUserRoleInfoByToken()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[getUserRoleInfoByToken()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:分页查询接入方角色信息
	 * @param userRolePageInfo
	 *            角色分页信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_ACC_USER_ROLE_PAGE, method = RequestMethod.POST)
	public Result getAccUserRoleByPage(@RequestBody String userRolePageInfo) {
		logger.info(String.format("[getAccUserRoleByPage()->request param:%s]", userRolePageInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(userRolePageInfo);
			if (null != checResult) {
				logger.error(String.format("[getAccUserRoleByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(userRolePageInfo);
			RpcResponse<Pagination<Map<String, Object>>> res = userRoleQuery.getAccUserRoleInfoByPage(pageInfo);
			if (res.isSuccess()) {
				logger.info(String.format("[getAccUserRoleByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getAccUserRoleByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getAccUserRoleByPage()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = AuthzUrlConstants.CHECK_ROLE_USER_EXIST, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<String> checkRoleUserExist(@RequestBody String roleInfo) {
		logger.info(String.format("[checkRoleUserExist()->request param:%s]", roleInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(roleInfo);
			if (null != checResult) {
				logger.error(String.format("[checkRoleUserExist()->fail:%s]", roleInfo));
				return checResult;
			}
			RpcResponse<Boolean> res = userRoleQuery.checkRoleUserExist(JSON.parseObject(roleInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[checkRoleUserExist()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue() + "", res.getMessage());
			} else {
				logger.error(String.format("[checkRoleUserExist()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[checkRoleUserExist()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = AuthzUrlConstants.GET_ROLE_MENU, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<List<Map>> getRoleMenuById(@RequestBody String roleInfo) {
		logger.info(String.format("[getRoleMenuById()->request param:%s]", roleInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(roleInfo);
			if (null != checResult) {
				logger.error(String.format("[getRoleMenuById()->fail:%s]", roleInfo));
				return checResult;
			}
			// 获取角色id
			JSONObject roleJson = JSON.parseObject(roleInfo);
			String roleId = roleJson.getString(AuthzConstants.ROLE_ID);
			String applicationType = roleJson.getString(AuthzConstants.APPLICATION_TYPE);
			RpcResponse<List<Map>> res = userRoleQuery.getRoleMenuById(roleId, applicationType);
			if (res.isSuccess()) {
				logger.info(String.format("[getRoleMenuById()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getRoleMenuById()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getRoleMenuById()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = AuthzUrlConstants.CHECK_USER_ORG_EXIST, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<String> checkOrgUserExist(@RequestBody String roleInfo) {
		logger.info(String.format("[checkOrgUserExist()->request param:%s]", roleInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(roleInfo);
			if (null != checResult) {
				logger.error(String.format("[checkOrgUserExist()->fail:%s]", roleInfo));
				return checResult;
			}
			RpcResponse<Boolean> res = userRoleQuery.checkOrgUserExist(JSON.parseObject(roleInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[checkOrgUserExist()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue() + "", res.getMessage());
			} else {
				logger.error(String.format("[checkOrgUserExist()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[checkOrgUserExist()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:分页查询接入方接口信息
	 * @param userRolePageInfo
	 *            角色分页信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_ACC_INTERFACE_PAGE, method = RequestMethod.POST)
	public Result getAccInterfaceByPage(@RequestBody String userRolePageInfo) {
		logger.info(String.format("[getAccUserRoleByPage()->request param:%s]", userRolePageInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(userRolePageInfo);
			if (null != checResult) {
				logger.error(String.format("[getAccUserRoleByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(userRolePageInfo);
			RpcResponse<Pagination<Map<String, Object>>> res = userRoleQuery.getAccInterfaceByPage(pageInfo);
			if (res.isSuccess()) {
				logger.info(String.format("[getAccUserRoleByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getAccUserRoleByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getAccUserRoleByPage()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据用户id和接入方秘钥查询角色信息
	 * @param userInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_ROLE_MESS_BY_SECRET, method = RequestMethod.POST)
	public Result<List> getRoleMessBySecret(@RequestBody String userInfo) {
		logger.info(String.format("[getRoleMessBySecret()->request param:%s]", userInfo));
		try {
			// 参数基础校验
			Result<List> checResult = ExceptionChecked.checkRequestParam(userInfo);
			if (null != checResult) {
				logger.error(String.format("[getRoleMessBySecret()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject userJson = JSON.parseObject(userInfo);
			// 根据token查询用户id
			String userId = userJson.getString(AuthzConstants.USER_ID);
			// 接入方id
			String accessSecret = userJson.getString(AuthzConstants.ACCESS_SECRET);
			RpcResponse<List<Map>> res = userRoleQuery.getRoleMessBySecret(userId, accessSecret);
			if (res.isSuccess()) {
				logger.info(String.format("[getRoleMessBySecret()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getRoleMessBySecret()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[getRoleMessBySecret()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据组织id查询组织下面的岗位信息
	 * @param userInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_ROLE_LIST_BY_ORGID, method = RequestMethod.POST)
	public Result<List> getRoleListByOrgId(@RequestBody String orgInfo) {
		logger.info(String.format("[getRoleListByOrgId()->request param:%s]", orgInfo));
		try {
			// 参数基础校验
			Result<List> checResult = ExceptionChecked.checkRequestParam(orgInfo);
			if (null != checResult) {
				logger.error(String.format("[getRoleListByOrgId()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject orgJson = JSON.parseObject(orgInfo);
			// 获取组织id
			String orgId = orgJson.getString(AuthzConstants.ORGANIZED_ID);
			RpcResponse<List<Map>> res = userRoleQuery.getRoleListByOrgId(orgId);
			if (res.isSuccess()) {
				logger.info(String.format("[getRoleListByOrgId()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getRoleListByOrgId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[getRoleListByOrgId()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_BUTTON_LIST_BY_ROLEID, method = RequestMethod.POST)
	public Result getListButtonByRoleId(@RequestBody String roleInfo) {
		logger.info(String.format("[getListButtonByRoleId()->request param:%s]", roleInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParamHasKey(roleInfo, AuthzConstants.ROLE_ID);
			if (null != checResult) {
				logger.error(String.format("[getListButtonByRoleId()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject roleInfoJson = JSON.parseObject(roleInfo);
			String roleId = roleInfoJson.getString(AuthzConstants.ROLE_ID);

			RpcResponse<List<String>> res = permiQuery.getPermiIdIdByRoleId(roleId);

			List<String> listItemId = new ArrayList<>();
			if (res.isSuccess()) {
				RpcResponse<List<String>> listValue = functionItemQuery
						.getItemIdByPermiIds((ArrayList<String>) res.getSuccessValue());
				if (listValue.isSuccess()) {
					listItemId = listValue.getSuccessValue();
				}
			}

			RpcResponse<List<Map>> buttonList = functionItemQuery
					.getListButtonByItemIds((ArrayList<String>) listItemId);

			if (buttonList.isSuccess()) {
				logger.info(String.format("[getListButtonByRoleId()->success:%s]", buttonList.getMessage()));
				return ResultBuilder.successResult(buttonList.getSuccessValue(), buttonList.getMessage());
			} else {
				logger.error(String.format("[getListButtonByRoleId()->fail:%s]", buttonList.getMessage()));
				return ResultBuilder.failResult(buttonList.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getListButtonByRoleId()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_ITEM_LIST_BY_ROLEID_AND_MENUID, method = RequestMethod.POST)
	public Result getListItemByRoleIdAndMenuId(@RequestBody String paramInfo) {
		logger.info(String.format("[getListButtonByRoleId()->request param:%s]", paramInfo));
		try {
			// 参数基础校验
			Result checkRoleId = ExceptionChecked.checkRequestParamHasKey(paramInfo, AuthzConstants.ROLE_ID);
			if (null != checkRoleId) {
				logger.error(String.format("[getListItemByRoleIdAndMenuId()->fail:%s]", checkRoleId.getException()));
				return checkRoleId;
			}
			Result checkMenuId = ExceptionChecked.checkRequestParamHasKey(paramInfo, AuthzConstants.MENU_ID);
			if (null != checkMenuId) {
				logger.error(String.format("[getListItemByRoleIdAndMenuId()->fail:%s]", checkMenuId.getException()));
				return checkMenuId;
			}
			
			JSONObject roleInfoJson = JSON.parseObject(paramInfo);
			String roleId = roleInfoJson.getString(AuthzConstants.ROLE_ID);
			String menuId = roleInfoJson.getString(AuthzConstants.MENU_ID);
			
			//校验角色是否正常（删除或者停用）
			Boolean check=permiQuery.checkRoleNormal(roleId);
			if(!check){
				logger.error(String.format("[getListItemByRoleIdAndMenuId()->fail:%s]", AuthzConstants.GET_FAIL));
				return ResultBuilder.failResult(AuthzConstants.GET_ROLE_STATE_FAIL);
			}

			RpcResponse<List<String>> res = permiQuery.getPermiIdIdByRoleId(roleId);

			List<String> listItemId = new ArrayList<>();
			if (res.isSuccess()) {
				RpcResponse<List<String>> listValue = functionItemQuery
						.getItemIdByPermiIds((ArrayList<String>) res.getSuccessValue());
				if (listValue.isSuccess()) {
					listItemId = listValue.getSuccessValue();
				}
			}

			RpcResponse<List<Map>> itemInfo = functionItemQuery.getItemBymenuId(menuId);

			List<String> itemTypeList = new ArrayList<>();
			if (res.isSuccess() && listItemId.size() > 0 && itemInfo.isSuccess()
					&& itemInfo.getSuccessValue().size() > 0) {
				List<Map> list = itemInfo.getSuccessValue();
				for (int i = 0; i < listItemId.size(); i++) {
					for (int j = 0; j < list.size(); j++) {
						if (listItemId.get(i).equals(list.get(j).get(AuthzConstants.ID_))
								&& list.get(j).containsKey("itemType")) {
							itemTypeList.add(list.get(j).get("itemType").toString());
						}
					}
				}
			}
			
			//去除空值，去除重复字符串
			itemTypeList.remove("");
			List<String> newList = new ArrayList<String>(new HashSet<String>(itemTypeList));
			
			if (res.isSuccess() && itemInfo.isSuccess()) {
				logger.info(String.format("[getListItemByRoleIdAndMenuId()->success:%s]", AuthzConstants.GET_SUCC));
				return ResultBuilder.successResult(newList, AuthzConstants.GET_SUCC);
			} else {
				logger.error(String.format("[getListItemByRoleIdAndMenuId()->fail:%s]", AuthzConstants.GET_FAIL));
				return ResultBuilder.failResult(AuthzConstants.GET_FAIL);
			}
		} catch (Exception e) {
			logger.error("[getListItemByRoleIdAndMenuId()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据组织id获取该组织下能或不能接收短信的用户
	 * @param userInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.GET_USER_BY_ORGID, method = RequestMethod.POST)
	public Result<List<Map>> getUserByOrgId(@RequestBody String paramInfo) {
		logger.info(String.format("[getUserByOrgId()->request param:%s]", paramInfo));
		try {
			// 参数基础校验
			Result<List<Map>> checResult = ExceptionChecked.checkRequestParam(paramInfo);
			if (null != checResult) {
				logger.error(String.format("[getUserByOrgId()->fail:%s]", checResult.getException()));
				return checResult;
			}

			JSONObject json = JSON.parseObject(paramInfo);

			// 获取该组织下的用户id集合
			RpcResponse<List<Map>> list = userRoleQuery.getUserListByOrgId(json);

			if (list.isSuccess()) {
				logger.info(String.format("[getUserByOrgId()->success:%s]", list.getMessage()));
				return ResultBuilder.successResult(list.getSuccessValue(), list.getMessage());
			} else {
				logger.error(String.format("[getUserByOrgId()->fail:%s]", list.getMessage()));
				return ResultBuilder.failResult(list.getMessage());
			}

		} catch (Exception e) {
			logger.error("[getUserByOrgId()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	/**
	 * 
	* @Description:测试getAllUserByOrgId（根据组织id查询该组织以及父组织下能或不能接收短信的用户）接口
	* @param paramInfo
	* @return
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value ="/getAllUserByOrgId", method = RequestMethod.POST)
	public Result<List<Map>> getAllUserByOrgId(@RequestBody String paramInfo) {
		logger.info(String.format("[getAllUserByOrgId()->request param:%s]", paramInfo));
		try {
			// 参数基础校验
			Result<List<Map>> checResult = ExceptionChecked.checkRequestParam(paramInfo);
			if (null != checResult) {
				logger.error(String.format("[getAllUserByOrgId()->fail:%s]", checResult.getException()));
				return checResult;
			}

			JSONObject json = JSON.parseObject(paramInfo);

			// 获取该组织下的用户id集合
			RpcResponse<List<Map>> list = userRoleQuery.getAllUserListByOrgId(json);

			if (list.isSuccess()) {
				logger.info(String.format("[getAllUserByOrgId()->success:%s]", list.getMessage()));
				return ResultBuilder.successResult(list.getSuccessValue(), list.getMessage());
			} else {
				logger.error(String.format("[getAllUserByOrgId()->fail:%s]", list.getMessage()));
				return ResultBuilder.failResult(list.getMessage());
			}

		} catch (Exception e) {
			logger.error("[getAllUserByOrgId()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
