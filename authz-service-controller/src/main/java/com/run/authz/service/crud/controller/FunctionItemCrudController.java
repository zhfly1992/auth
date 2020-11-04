package com.run.authz.service.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.constants.AuthzUrlConstants;
import com.run.authz.crud.service.FunctionItemCrudService;
import com.run.entity.common.Result;

/**
 * @Description: 权限中心-功能项Contro
 * @author: wangsheng
 * @version: 1.0, 2017年8月29日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = AuthzUrlConstants.ITEM)
public class FunctionItemCrudController {

	@Autowired
	private FunctionItemCrudService functionItemCrudService;



	/**
	 * @Description:保存功能项
	 * @param itemInfo
	 * @return Result<JSONObject>
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.SAVE_FUNCTION_ITEM, method = RequestMethod.POST)
	public Result<JSONObject> saveFunctionItem(@RequestBody String itemInfo) {
		return functionItemCrudService.saveFunctionItem(itemInfo);
	}



	/**
	 * @Description:根据ID修改功能项
	 * @param itemInfo
	 * @return Result<JSONObject>
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.UPDATE_FUNCTION_ITEM, method = RequestMethod.POST)
	public Result<JSONObject> updateFunctionItem(@RequestBody String itemInfo) {
		return functionItemCrudService.updateFunctionItem(itemInfo);
	}



	/**
	 * @Description:根据多个ID批量删除功能项
	 * @param ids
	 * @return Result<JSONArray>
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.DELETE_FUNCTION_ITEM, method = RequestMethod.POST)
	public Result<JSONArray> delFunctionItem(@RequestBody String ids) {
		return functionItemCrudService.delFunctionItem(ids);
	}



	/**
	 * @Description:保存功能项与菜单关系
	 * @param itemMenuInfo
	 * @return Result<String>
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.SAVE_ITEM_RS_MENU, method = RequestMethod.POST)
	public Result<String> addItemRsMenu(@RequestBody String itemMenuInfo) {
		return functionItemCrudService.addItemRsMenu(itemMenuInfo);
	}



	/**
	 * @Description:保存功能项与按钮关系
	 * @param itemButtonInfo
	 * @return Result<String>
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.SAVE_ITEM_RS_BUTTON, method = RequestMethod.POST)
	public Result<String> addItemRsButton(@RequestBody String itemButtonInfo) {
		return functionItemCrudService.addItemRsButton(itemButtonInfo);
	}



	/**
	 * @Description:保存功能项与接口地址关系
	 * @param itemUrlInfo
	 * @return Result<String>
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.SAVE_ITEM_RS_URL, method = RequestMethod.POST)
	public Result<String> addItemRsInterUrl(@RequestBody String itemUrlInfo) {
		return functionItemCrudService.addItemRsInterUrl(itemUrlInfo);
	}

}
