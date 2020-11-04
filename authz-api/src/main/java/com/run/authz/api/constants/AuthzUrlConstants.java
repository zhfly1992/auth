/*
 * File name: UscUrlConstants.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月22日 ... ...
 * ...
 *
 ***************************************************/

package com.run.authz.api.constants;

/**
 * @Description:权限中心url地址管理
 * @author: zhabing
 * @version: 1.0, 2017年6月22日
 */

public class AuthzUrlConstants {

	/** ------------------------token url地址------------------------------- */
	/** token路径 **/
	public static final String	API_USER_TOKEN						= "/token";

	/* 验证用户token */
	public static final String	API_CODE_AUTHENTICATE				= "/authenticate";

	/* 获取或者生成token */
	public static final String	API_CODE_CREATE_TOKEN				= "/createtoken";

	/* 移除token */
	public static final String	API_CODE_REMOVE_TOKEN				= "/removetoken";

	/* 根据token获取userId */
	public static final String	API_CODE_GET_USERID					= "/getuserid";

	/** -------------------------用户角色 url地址--------------------------------- */
	/** 角色根路径 */
	public static final String	USER_ROLE							= "/userRole";
	/** 保存用户角色 */
	public static final String	SAVE_USER_ROLE						= "/saveUserRole";
	/** 保存用户角色以及他的关联信息（组织以及菜单权限） */
	public static final String	SAVE_USER_ROLE_RS					= "/saveUserRoleRs";
	/** 修改用户角色以及他的关联信息（组织以及菜单权限） */
	public static final String	UPDATE_USER_ROLE_RS					= "/updateUserRoleRs";
	/** 转换用户角色状态 */
	public static final String	SWATE_USERROLE_STATE				= "/swateUserRoleState";
	/** 校验岗位组织下面是否存在人员信息 */
	public static final String	CHECK_ROLE_USER_EXIST				= "/checkRoleUserExist";
	/** 根据角色id查询角色所绑定的菜单信息 */
	public static final String	GET_ROLE_MENU						= "/getRoleMenuById";
	/** 檢查資源下面是否存在人員信息 */
	public static final String	CHECK_USER_ORG_EXIST				= "/checkOrgUserExist";
	/** 修改用户角色 */
	public static final String	UPDATE_USER_ROLE					= "/updateUserRole";
	/** 删除用户角色 */
	public static final String	DEL_USER_ROLE						= "/delUserRole";
	/** 分页查询用户角色 */
	public static final String	GET_USER_ROLE_PAGE					= "/getUserRoleByPage";
	/** 分页查询接入方用户角色 */
	public static final String	GET_ACC_USER_ROLE_PAGE				= "/getAccUserRoleByPage";
	/** 分页查询接入方用户角色 */
	public static final String	GET_ACC_INTERFACE_PAGE				= "/getAccInterfaceByPage";
	/** 分页查询用户自己的用户信息 */
	public static final String	GET_USER_ROLE_BY_USERID				= "/getUserRoleInfoByUserId";
	/** 分页查询用户所拥有的所有菜单信息 */
	public static final String	GET_USER_MENU_BY_TOKEN				= "/getUserMenuByToken";
	/** 根据用户id和接入方秘钥查询角色信息 */
	public static final String	GET_ROLE_MESS_BY_SECRET				= "/getRoleMessBySecret";
	/** 根据组织id查询组织下面的岗位信息 */
	public static final String	GET_ROLE_LIST_BY_ORGID				= "/getRoleListByOrgId";
	/** 查询当前角色下拥有的按钮 */
	public static final String	GET_BUTTON_LIST_BY_ROLEID			= "/getButtonListByRoleId";
	/** 根据角色id和菜单id查询共有的功能类型 */
	public static final String	GET_ITEM_LIST_BY_ROLEID_AND_MENUID	= "/getItemListByRoleIdAndMenuId";
	/** 根据组织id获取该组织下能或不能接收短信的用户 */
	public static final String	GET_USER_BY_ORGID					= "/getUserByOrgId";

	/** 检查角色名称 */
	public static final String	CHECK_ROLE_NAME						= "/checkRoleName";
	/** 检查组织角色名称 */
	public static final String	CHECK_ORG_ROLE_NAME					= "/checkOrgRoleName";
	/** 添加角色与菜单资源的关联 */
	public static final String	SAVE_ROLE_RS_MENU					= "/addRoleRsMenu";
	/** 添加角色与用户的关联 */
	public static final String	SAVE_ROLE_RS_USER					= "/addRoleRsUser";
	/** 批量删除角色与用户的关联 */
	public static final String	DEL_ROLE_RS_USER					= "/delRoleRsUser";

	/** 权限根路径 */
	public static final String	PERMISSIOM							= "/permi";
	/** 保存权限 */
	public static final String	SAVE_PREMISSION						= "/savePermission";
	/** 修改权限 */
	public static final String	UPDATE_PREMISSION					= "/updatePermission";
	/** 删除权限 */
	public static final String	DEL_PREMISSION						= "/delPermission";
	/** 权限分页查询 */
	public static final String	GET_PREMISSION_BYPAGE				= "/getPermissionByPage";
	/** 查询角色已经拥有的或者未拥有的权限信息 */
	public static final String	GET_ROLE_PREMISSION_BYPAGE			= "/getRolePermissionByPage";
	/** 查询接入方已经拥有的或者未拥有的权限信息 */
	public static final String	GET_ACCESS_PREMISSION_BYPAGE		= "/getAccessPermissionByPage";
	/** 查询角色已经拥有的或者未拥有的权限信息 */
	public static final String	GET_ROLE_PREMI_BYPAGE				= "/getRolePermiByPage";
	/** 查询权限已经拥有的或者未拥有的功能项信息 */
	public static final String	GET_FUNC_PREMISSION_BYPAGE			= "/getFuncPermissionByPage";
	/** 根据角色id获取功能项信息 */
	public static final String	GET_ITEM_BY_ROLE_ID					= "/getItemByRoleId/{permisId}";

	/** 校验权限名称 */
	public static final String	CHECK_PERMI_NAME					= "/checkPermiName";
	/** 角色与权限的绑定 */
	public static final String	ADD_PERMISSION_RS_ROLE				= "/addPermiRsRole";
	/** 角色与权限的解除 */
	public static final String	DEL_PERMISSION_RS_ROLE				= "/delPermiRsRole";

	/** 功能项与权限的绑定 */
	public static final String	ADD_PERMISSION_RS_FUNC				= "/addPermiRsFunc";
	/** 功能项与权限的解除 */
	public static final String	DEL_PERMISSION_RS_FUNC				= "/delPermiRsFunc";
	/** 绑定功能项 */
	public static final String	BIND_ITEM_BY_PERMIS_ID				= "/put/permis/{id}";
	/** 接入方绑定权限 */
	public static final String	ADD_ACCESS_RS_PERMISSION			= "/addAccessRsPermi";
	/** 解除接入方绑定 */
	public static final String	DEL_ACCESS_RS_PERMISSION			= "/delAccessRsPermi";

	/** 功能项根路径 */
	public static final String	ITEM								= "/item";
	/** 保存功能项 */
	public static final String	SAVE_FUNCTION_ITEM					= "/saveFunctionItem";
	/** 修改功能项 */
	public static final String	UPDATE_FUNCTION_ITEM				= "/updateFunctionItem";
	/** 批量删除功能项 */
	public static final String	DELETE_FUNCTION_ITEM				= "/delFunctionItem";
	/** 分页查询功能项 */
	public static final String	GET_FUNCTION_ITEM_BY_PAGE			= "/getFunctionItemByPage";
	/** 根据id查询功能项 */
	public static final String	GET_FUNCTION_ITEM_BY_ID				= "/getFunctionItemById";
	/** 根据接入方秘钥分页查询功能项 */
	public static final String	GET_FUNCTION_ITEM_BY_ACCESSSECRET	= "/getFunctionItemByAccessSecret";
	/** 保存功能项与菜单的关系 */
	public static final String	SAVE_ITEM_RS_MENU					= "/addItemRsMenu";
	/** 保存功能项与按钮的关系 */
	public static final String	SAVE_ITEM_RS_BUTTON					= "/addItemRsButton";
	/** 根据功能项id查询与之关联的菜单id */
	public static final String	GET_MENUID_BY_ITEMID				= "/getMenuIdByitemId";
	/** 根据功能项id查询与之关联的按钮id */
	public static final String	GET_BUTTONID_BY_ITEMID				= "/getButtonIdByitemId";
	/** 保存功能项与接口地址的关系 */
	public static final String	SAVE_ITEM_RS_URL					= "/addItemRsUrl";
	/** 根据功能项id查询与之关联的urlId */
	public static final String	GET_URLID_BY_ITEMID					= "/getUrlIdByitemId";
	/** 校验功能项名称是否重复 */
	public static final String	CHECK_ITEM_NAME						= "/checkItemName";
	/** 通过菜单id获取功能项信息 */
	public static final String	GET_ITEM_BY_MENU_ID					= "/get/meun/{menuId}";

	/** 黑白名单 */
	public static final String	WHITE_BLACK							= "/whiteBlack";
	/** 分页查询黑白名单 */
	public static final String	GET_WHITE_BLACK_BYPAGE				= "/getWhiteBlackByPage";
	/** 检查黑白名单名称 */
	public static final String	CHECK_WHITE_BLACK_NAME				= "/checkWhiteBlackName";
	/** 添加黑白名单 */
	public static final String	ADD_WHITE_BLACK						= "/addWhiteBlack";
	/** 添加黑白名单 */
	public static final String	UPDATE_WHITE_BLACK					= "/updateWhiteBlack";
	/** 删除黑白名单 */
	public static final String	DEL_WHITE_BLACK						= "/delWhiteBlack";

}
