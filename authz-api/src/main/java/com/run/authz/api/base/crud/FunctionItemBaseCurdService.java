package com.run.authz.api.base.crud;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

public interface FunctionItemBaseCurdService {

	/**
	 * @Description:RPC保存功能项接口
	 * @param functionItemInfo
	 * @return RpcResponse<JSONObject>
	 * @throws Exception
	 */
	RpcResponse<JSONObject> saveFunctionItemInfo(JSONObject functionItemInfo) throws Exception;



	/**
	 * @Description:RPC修改功能项接口
	 * @param functionItemInfo
	 * @return RpcResponse<JSONObject>
	 * @throws Exception
	 */
	RpcResponse<JSONObject> updateFunctionItemInfo(JSONObject functionItemInfo) throws Exception;



	/**
	 * @Description:RPC批量删除功能接口
	 * @param ids
	 * @return RpcResponse<JSONArray>
	 * @throws Exception
	 */
	RpcResponse<JSONArray> deleteFunctionItemInfo(JSONArray ids) throws Exception;



	/**
	 * @Description:保存功能项与菜单的关系
	 * @param itemId
	 * @param menuArray
	 * @return RpcResponse<String>
	 */
	RpcResponse<String> addItemRsMenu(String itemId, JSONArray menuArray);



	/**
	 * @Description:保存功能项与按钮的关系
	 * @param itemId
	 * @param buttonArray
	 * @return RpcResponse<String>
	 */
	RpcResponse<String> addItemRsButton(String itemId, JSONArray buttonArray);



	/**
	 * @Description:保存功能项与接口地址关系
	 * @param itemId
	 * @param urlArray
	 * @return RpcResponse<String>
	 */
	RpcResponse<String> addItemRsInterUrl(String itemId, JSONArray urlArray);

}
