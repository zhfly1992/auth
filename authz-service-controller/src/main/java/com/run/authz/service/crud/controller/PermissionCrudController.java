/*
 * File name: PermissionCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年8月28日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.service.crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.constants.AuthzUrlConstants;
import com.run.authz.crud.service.PermissionCrudService;
import com.run.entity.common.Result;

/**
 * @Description: 权限curdcontroller类
 * @author: zhabing
 * @version: 1.0, 2017年8月28日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = AuthzUrlConstants.PERMISSIOM)
public class PermissionCrudController {

	@Autowired
	private PermissionCrudService permiCurdService;



	@RequestMapping(value = AuthzUrlConstants.SAVE_PREMISSION, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONObject> savePermission(@RequestBody String roleInfo) {
		return permiCurdService.savePermission(roleInfo);
	}



	@RequestMapping(value = AuthzUrlConstants.UPDATE_PREMISSION, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONObject> updatePermission(@RequestBody String roleInfo) {
		return permiCurdService.updatePermissionInfo(roleInfo);
	}



	@RequestMapping(value = AuthzUrlConstants.DEL_PREMISSION, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONArray> delPermission(@RequestBody String ids) {
		return permiCurdService.delPermission(ids);
	}



	@RequestMapping(value = AuthzUrlConstants.ADD_PERMISSION_RS_ROLE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<List<JSONObject>> addPermiRsRoleRole(@RequestBody String perRoleInfo) {
		return permiCurdService.addPermiRsRole(perRoleInfo);
	}



	@RequestMapping(value = AuthzUrlConstants.DEL_PERMISSION_RS_ROLE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<List<JSONObject>> delPermiRsRole(@RequestBody String perRoleInfo) {
		return permiCurdService.delPermiRsRole(perRoleInfo);
	}



	@RequestMapping(value = AuthzUrlConstants.ADD_PERMISSION_RS_FUNC, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<List<JSONObject>> addPermiRsFunc(@RequestBody String perRoleInfo) {
		return permiCurdService.addPermiRsFunc(perRoleInfo);
	}



	@RequestMapping(value = AuthzUrlConstants.BIND_ITEM_BY_PERMIS_ID, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<List<JSONObject>> permisBindFunc(@PathVariable String id, @RequestBody String itemInfo) {
		return permiCurdService.permisBindFunc(id, itemInfo);
	}
	
	@RequestMapping(value = AuthzUrlConstants.ADD_ACCESS_RS_PERMISSION, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<List<JSONObject>> addAccessRsPermi(@RequestBody String accessRsPermiInfo) {
		return permiCurdService.addAccessRsPermi(accessRsPermiInfo);
	}
	
	@RequestMapping(value = AuthzUrlConstants.DEL_ACCESS_RS_PERMISSION, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<Integer> delAccessRsPermi(@RequestBody String accessRsPermiInfo) {
		return permiCurdService.delAccessRsPermi(accessRsPermiInfo);
	}

}
