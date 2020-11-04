/*
 * File name: WhiteBlackCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年9月6日 ... ...
 * ...
 *
 ***************************************************/

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
import com.run.authz.crud.service.WhiteBlackCrudService;
import com.run.entity.common.Result;

/**
 * @Description: 黑白名单控制类
 * @author: zhabing
 * @version: 1.0, 2017年9月6日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = AuthzUrlConstants.WHITE_BLACK)
public class WhiteBlackCrudController {

	@Autowired
	private WhiteBlackCrudService whiteBlackCrud;



	/**
	 * 
	 * @Description:添加黑白名单
	 * @param whiteBlackInfo
	 *            黑白名单信息
	 * @return 添加黑白名单信息
	 */
	@RequestMapping(value = AuthzUrlConstants.ADD_WHITE_BLACK, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONObject> addWhiteBlack(@RequestBody String whiteBlackInfo) {
		return whiteBlackCrud.addWhiteBlack(whiteBlackInfo);
	}



	/**
	 * 
	 * @Description:修改黑白名单
	 * @param whiteBlackInfo
	 *            黑白名单信息
	 * @return 添加黑白名单信息
	 */
	@RequestMapping(value = AuthzUrlConstants.UPDATE_WHITE_BLACK, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONObject> updateWhiteBlack(@RequestBody String whiteBlackInfo) {
		return whiteBlackCrud.updateWhiteBlack(whiteBlackInfo);
	}



	/**
	 * 
	 * @Description:删除黑白名单
	 * @param whiteBlackInfo
	 *            黑白名单信息
	 * @return 添加黑白名单信息
	 */
	@RequestMapping(value = AuthzUrlConstants.DEL_WHITE_BLACK, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONArray> delWhiteBlack(@RequestBody String whiteBlackInfo) {
		return whiteBlackCrud.delWhiteBlack(whiteBlackInfo);
	}

}
