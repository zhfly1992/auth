package com.run.authz.base.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;

public interface FunctionItemBaseQueryService {

	/**
	 * @Description:RPC分页查询功能项
	 * @param pageInfo
	 * @return RpcResponse<Pagination<Map<String, Object>>>
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getFunctionItemByPage(Map<String, String> pageInfo) throws Exception;



	/**
	 * @Description:根据Id查询功能项信息
	 * @param itemId
	 * @return RpcResponse<Map<String, Object>>
	 * @throws Exception
	 */
	RpcResponse<Map<String, Object>> getFunctionItemById(String itemId) throws Exception;



	/**
	 * @Description:根据接入方秘钥查询接入方信息
	 * @param accessSecret
	 * @return RpcResponse<Map<String, Object>>
	 * @throws Exception
	 */
	RpcResponse<String> getAccessInfoBySecret(String accessSecret) throws Exception;



	/**
	 * @Description:根据接入方Id分页查询功能项
	 * @param pageInfo
	 * @param accessId
	 * @return RpcResponse<Pagination<Map<String, Object>>>
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getFunctionItemByAccessId(Map<String, String> pageInfo,
			String accessId) throws Exception;



	/**
	 * @Description:根据功能项Id查询与之关联的菜单id
	 * @param itemId
	 * @return RpcResponse<List<Map<String,Object>>>
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getMenuIdByItemId(String itemId) throws Exception;



	/**
	 * @Description:根据功能项id查询与之关联的按钮id
	 * @param itemId
	 * @return RpcResponse<List<Map>>
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getButtonIdByItemId(String itemId) throws Exception;



	/**
	 * @Description:根据权限id查询与之关联的功能项id集合
	 * @param permiIds
	 * @return RpcResponse<List<Map>>
	 * @throws Exception
	 */
	RpcResponse<List<String>> getItemIdByPermiIds(ArrayList<String> permiIds) throws Exception;



	/**
	 * @Description:根据功能项id集合按钮id集合
	 * @param itemIds
	 * @return RpcResponse<List<Map>>
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getListButtonByItemIds(ArrayList<String> itemIds) throws Exception;



	/**
	 * @Description:根据功能项id查询与之关联的urlId
	 * @param itemId
	 * @return RpcResponse<List<Map>>
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getUrlIdByItemId(String itemId) throws Exception;



	/**
	 * @Description:校验功能项是否重复
	 * @param itemName
	 * @return RpcResponse<Boolean>
	 * @throws Exception
	 */
	RpcResponse<Boolean> checkItemName(String itemName, String accessType) throws Exception;



	/**
	 * 
	 * @Description:通过菜单id查询功能项信息
	 * @param menuId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getItemBymenuId(String menuId) throws Exception;
}
