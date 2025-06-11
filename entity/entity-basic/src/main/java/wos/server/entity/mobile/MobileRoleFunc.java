package wos.server.entity.mobile;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;

import javax.validation.constraints.Size;

/**
 * 角色功能Entity
 * @author Tyr Tao
 * @version 2023-03-07
 */
@Table(name="tb_mobile_role_func", alias="a", label="角色功能信息", columns={
		@Column(name="id", attrName="id", label="id", isPK=true),
		@Column(name="role_id", attrName="roleId", label="角色标识", isUpdate=false),
		@Column(name="func_id", attrName="funcId", label="功能码标识", isUpdate=false),
		@Column(includeEntity= DataEntity.class),
	}, orderBy="a.create_date DESC"
)
public class MobileRoleFunc extends DataEntity<MobileRoleFunc> {

	//#region 数据库
	private static final long serialVersionUID = 1L;
	private String roleId;		// 角色标识
	private String funcId;		// 功能码标识
	
	public MobileRoleFunc() {
		this(null);
	}
	
	public MobileRoleFunc(String id){
		super(id);
	}
	
	@Size(min=0, max=64, message="角色标识长度不能超过 64 个字符")
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	@Size(min=0, max=64, message="功能码标识长度不能超过 64 个字符")
	public String getFuncId() {
		return funcId;
	}

	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}
	//#endregion

	/**
	 * 功能码名称
	 */
	private String funcTitle;

	/**
	 * 功能码类别
	 */
	private String funcCate;

	private String roleName;

	public String getFuncTitle() {
		return funcTitle;
	}

	public void setFuncTitle(String funcTitle) {
		this.funcTitle = funcTitle;
	}

	public String getFuncCate() {
		return funcCate;
	}

	public void setFuncCate(String funcCate) {
		this.funcCate = funcCate;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}