/*
 * File name: UscConstants.java
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

package com.run.authz.api.constants;

/**
 * @Description: 权限中心静态类
 * @author: zhabing
 * @version: 1.0, 2017年6月21日
 */

public class AuthzConstants {
	// 用户认证信息
	public static final String	QU_INFO							= "quInfo";
	// 认证成功后的访问令牌
	public static final String	TOKEN							= "token";
	public static final String	USER_ID							= "userId";
	public static final String	TOKEN_ID						= "tokenId";

	/** 表唯一标识 */
	public static final String	ID_								= "_id";
	public static final String	CREATE_TIME						= "createTime";
	public static final String	UPDATE_TIME						= "updateTime";

	/** 验证信息 */
	public static final String	AUTHZ_INFO						= "authzInfo";

	public static final String	STATE							= "state";
	public static final String	IS_DELETE						= "isDelete";
	/** 正常 */
	public static final String	STATE_NORMAL_ONE				= "valid";
	/** 已删除 */
	public static final String	STATE_STOP_ZERO					= "invalid";

	/** 接入方id */
	public static final String	TENEMENT_ACCESS_ID				= "accessId";
	/** 接入方类型 */
	public static final String	ACCESS_TYPE						= "accessType";
	public static final String	TENEMENT_ACCESS_NAME			= "accessName";
	/** 租户id */
	public static final String	TENEMENT_ID						= "tenementId";
	public static final String	TENEMENT_NAME					= "tenementName";
	public static final String	ACCESS_TENEMENT_NAME			= "accessTenementName";

	/** 菜单id */
	public static final String	MENU_ID							= "menuId";

	/** 按钮id */
	public static final String	BUTTON_ID						= "buttonId";

	/** 角色名称 */
	public static final String	ROLE_NAME						= "roleName";

	/** 功能项名称 */
	public static final String	ITEM_NAME						= "itemName";

	/** 功能项ID */
	public static final String	ITEM_ID							= "itemId";

	/** 批量删除id */
	public static final String	IDS								= "ids";

	/** 分页页数 */
	public static final String	PAGENUMBER						= "pageNo";

	/** 分页大小 */
	public static final String	PAGESIZE						= "pageSize";

	/** 默认分页大小 */
	public static final String	PAGESIZEDEFAULT					= "6";

	/** 正则表达式 */
	public static final String	REGX_LEFT						= "^.*";
	public static final String	REGX_RIGHT						= ".*$";

	/** 角色信息 */
	public static final String	ROLE_INFO						= "roleInfo";

	/** 角色Id */
	public static final String	ROLE_ID							= "roleId";

	/** 应用类型 （app or pc） */
	public static final String	APPLICATION_TYPE				= "applicationType";

	/** 缓存key */
	public static final String	KEY								= "key";

	/** 缓存value */
	public static final String	VALUE							= "value";

	/** 组织id */
	public static final String	ORGANIZED_ID					= "organizeId";

	/** 组织名称 */
	public static final String	ORGANIZED_NAME					= "organizedName";

	/** 资源名称 */
	public static final String	SOURCE_NAME						= "sourceName";

	/** 接入方秘钥 */
	public static final String	ACCESS_SECRET					= "accessSecret";

	/** 组织信息 */
	public static final String	ORG_MESS						= "orgMess";

	/** 菜单信息 */
	public static final String	MENU_MESS						= "menuMess";

	/** url信息 */
	public static final String	URL_MESS						= "urlMess";
	/** 菜单名称 */
	public static final String	MENU_NAME						= "menuName";
	/** 查询用户是否拥有角色标识 */
	public static final String	HAVE_OWN						= "havaOwn";
	/** url id */
	public static final String	URLID							= "urlId";
	/** 权限名称 */
	public static final String	PERMI_NAME						= "permissionName";
	/** 角色id数组 */
	public static final String	ROLE_ARRAY						= "roleArray";
	/** 权限数组 */
	public static final String	PERMI_ARRAY						= "permiArray";
	/** 功能项数组 */
	public static final String	FUNC_ARRAY						= "funcArray";
	/** 权限id */
	public static final String	PERMI_ID						= "permiId";
	/** 功能项id */
	public static final String	FUNC_ID							= "funcId";
	/** 黑白名单类型 */
	public static final String	TYPE							= "type";
	/** 黑白名单地址 */
	public static final String	ADDRESS							= "address";
	/** 黑白名单名称 */
	public static final String	NAME							= "name";
	/** 备注 */
	public static final String	REMARK							= "remark";

	public static final String	APP								= "APP";

	public static final String	PC								= "PC";
	/** 旧的组织数组 */
	public static final String	OLD_ORG_MESS					= "oldOrgMess";
	/** 新的组织数组 */
	public static final String	NEW_ORG_MESS					= "newOrgMess";
    /**日志记录key*/
	public static final String	LOGKEY							= "authz";

	/** 提示信息 */
	public static final String	NO_BUSINESS						= "没有业务数据！";
	public static final String	GET_SUCC						= "查询成功！";
	public static final String	GET_FAIL						= "查询失败！";
	public static final String	EMPTYOBJECT						= "对象为空！";

	public static final String	SAVE_SUCC						= "保存成功！";
	public static final String	SAVE_FAIL						= "保存失败！";

	public static final String	UPDATE_SUCC						= "修改成功！";
	public static final String	UPDATE_FAIL						= "修改失败！";

	public static final String	DEL_SUCC						= "删除成功！";
	public static final String	DEL_FAIL						= "删除失败！";

	public static final String	ROLE_SAVE_FAIL_NAME_EXITES		= "角色重名保存失败！";
	public static final String	PER_SAVE_FAIL_NAME_EXITES		= "权限重名保存失败！";
	public static final String	ITEM_SAVE_FAIL_NAME_EXITES		= "功能项重名保存失败！";
	public static final String	ITEM_UPDATE_FAIL_NAME_EXITES	= "功能项重名修改失败！";
	public static final String	GET_ROLE_STATE_FAIL				= "该岗位已被停用，请刷新页面！";

	public static final String	CHECK_BUSINESS					= "参数不合法！";
	/* token */
	public static final String	TOKEN_AUTHZ_SUCCESS				= "token验证成功！";
	public static final String	TOKEN_AUTHZ_FAIL				= "token验证失败！";
	public static final String	TOKEN_CREATE_SUCCESS			= "生成token成功！";
	public static final String	TOKEN_REMOVE_SUCCESS			= "token移除成功！";
	public static final String	TOKEN_REMOVE_FAIL				= "token移除失败！";
	public static final String	TOKEN_GET_USERID_SUCCESS		= "根据token查询userid成功！";
	public static final String	TOKEN_GET_USERID_FAIL			= "根据token查询userid失败！";
	public static final String	REDISCACHE_CREATE_SUCCESS		= "redis缓存生成成功！";
	public static final String	KEY_REFRESH_SUCCESS				= "key刷新成功！";
}
