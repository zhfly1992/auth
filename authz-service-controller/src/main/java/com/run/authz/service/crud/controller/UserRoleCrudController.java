/*
 * File name: UserRoleCrudController.java
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
import com.run.authz.crud.service.UserRoleCrudService;
import com.run.entity.common.Result;

/**
 * @Description: 用户角色crud控制类
 * @author: zhabing
 * @version: 1.0, 2017年7月19日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = AuthzUrlConstants.USER_ROLE)
public class UserRoleCrudController {
	@Autowired
	private UserRoleCrudService userRoleService;



	@RequestMapping(value = AuthzUrlConstants.SAVE_USER_ROLE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONObject> saveUserRole(@RequestBody String roleInfo) {
		return userRoleService.saveUserRole(roleInfo);
	}



	@RequestMapping(value = AuthzUrlConstants.UPDATE_USER_ROLE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONObject> updateUserRole(@RequestBody String roleInfo) {
		return userRoleService.updateUserRole(roleInfo);
	}



	@RequestMapping(value = AuthzUrlConstants.DEL_USER_ROLE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONArray> delUserRole(@RequestBody String ids) {
		return userRoleService.delUserRole(ids);
	}



	/**
	 * 
	 * @Description:添加角色与用户的关联
	 * @param roleUserInfo
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.SAVE_ROLE_RS_USER, method = RequestMethod.POST)
	public Result<String> addRoleRsUser(@RequestBody String roleUserInfo) {
		return userRoleService.addRoleRsUser(roleUserInfo);
	}



	/**
	 * 
	 * @Description:批量删除角色下面所关联的用户 或者 批量删除用户所关联的角色
	 * @param roleUserInfo
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = AuthzUrlConstants.DEL_ROLE_RS_USER, method = RequestMethod.POST)
	public Result<String> delRoleRsUser(@RequestBody String roleUserInfo) {
		return userRoleService.delRoleRsUser(roleUserInfo);
	}



	/**
	 * 
	 * @Description:保存角色关联信息
	 * @param roleInfo
	 * @return
	 */
	@RequestMapping(value = AuthzUrlConstants.SAVE_USER_ROLE_RS, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONObject> saveUserRoleRs(@RequestBody String roleInfo) {
		return userRoleService.saveUserRoleRs(roleInfo);
	}



	/**
	 * 修改角色关联信息
	 * 
	 * @Description:
	 * @param roleInfo
	 * @return
	 */

	@RequestMapping(value = AuthzUrlConstants.UPDATE_USER_ROLE_RS, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONObject> updateUserRoleRs(@RequestBody String roleInfo) {
		return userRoleService.updateUserRoleRs(roleInfo);
	}



	/**
	 * @Description:角色岗位状态
	 * @param roleInfo
	 * @return
	 */
	@RequestMapping(value = AuthzUrlConstants.SWATE_USERROLE_STATE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<String> swateUserRoleState(@RequestBody String roleInfo) {
		return userRoleService.swateUserRoleState(roleInfo);
	}

}
