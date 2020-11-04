/*
 * File name: MongodbConstants.java
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
 * @Description:
 * @author: zhabing
 * @version: 1.0, 2017年6月21日
 */

public class MongodbConstants {
	/** 用户角色信息集合 */
	public static final String	MONGODB_USERROLE_COLL			= "UserRole";
	/** 角色与用户的关系表 */
	public static final String	ROLE_RS_USER					= "RoleUserRs";
	/** 组织与用户的关系表 */
	public static final String	ORG_RS_USER						= "OrgUserRs";
	/** 角色与组织的关系表 */
	public static final String	ROLE_RS_ORGAN					= "RoleOrganRs";
	/** 权限信息集合 */
	public static final String	PERMISSION_COLL					= "Permission";
	/** 角色与权限关系表 */
	public static final String	PERMISSION_RS_ROLE				= "PermiRsRole";
	/** 接入方与权限关系表 */
	public static final String	PERMISSION_RS_ACCESS			= "PermiRsAcc";
	/** 功能项与权限关系表 */
	public static final String	PERMISSION_RS_FUNC				= "PermiRsFunc";
	/** 功能项集合 */
	public static final String	FUNCTION_ITEM					= "FunctionItem";
	/** 功能项与菜单的关系表 */
	public static final String	ITEM_RS_MENU					= "ItemMenuRs";
	/** 功能项与按钮的关系表 */
	public static final String	ITEM_RS_BUTTON					= "ItemButtonRs";
	/** 功能项与接口地址的关系表 */
	public static final String	ITEM_RS_INTER_URL				= "ItemInterUrlRs";
	/** 黑白名单info表 */
	public static final String	MONGODB_IP_WHITE_AND_BLACK_COLL	= "IpWhiteAndBlackList";
}