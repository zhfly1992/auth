/*
 * File name: UscCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月21日 ... ...
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

import com.run.authz.api.constants.AuthzUrlConstants;
import com.run.authz.crud.service.AuthzCrudService;
import com.run.entity.common.Result;

/**
 * @Description: 权限中心token增删改
 * @author: zhabing
 * @version: 1.0, 2017年6月21日
 */

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = AuthzUrlConstants.API_USER_TOKEN)
public class AuthzCrudController {
	@Autowired
	private AuthzCrudService authzCurdService;



	@SuppressWarnings("rawtypes")
	@RequestMapping(value = AuthzUrlConstants.API_CODE_REMOVE_TOKEN, method = RequestMethod.POST)
	public Result removeToken(@RequestBody String authzInfo) {
		return authzCurdService.removeToken(authzInfo);
	}

}
