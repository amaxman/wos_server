package wos.server.entity.auth;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;

import java.util.Date;

/**
 * 用户登录令牌Entity
 * @author Tyr Tao
 * @version 2022-05-15
 */
@Table(name="tb_user_session", alias="a", label="用户登录令牌信息", columns={
		@Column(name="user_code", attrName="userCode", label="用户编码", isPK=true),//登陆Code
		@Column(name="login_id", attrName="loginId", label="用户登陆标识"),//登陆ID
		@Column(name="staff_name", attrName="staffName", label="姓名"),	//姓名
		@Column(name="session_id", attrName="sessionId", label="登录令牌"),	//登陆令牌
		@Column(name="login_time", attrName="loginTime", label="登录时间", isUpdateForce=true),	//登陆时间
		@Column(includeEntity= DataEntity.class),
	}, orderBy="a.login_time DESC"
)
public class UserSession extends DataEntity<UserSession> {
	//#region 数据库
	private static final long serialVersionUID = 1L;

	private String userCode;		// 用户编码
	private String loginId;		// 用户标识
	private String staffName;	//姓名
	private String sessionId;		// 登录令牌
	private Date loginTime;		// 登录时间

	public UserSession(String id) {
		super(id);
	}

	public UserSession() {
		this(null);
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}


}