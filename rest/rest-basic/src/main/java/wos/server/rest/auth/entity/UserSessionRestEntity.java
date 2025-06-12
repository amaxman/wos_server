package wos.server.rest.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import wos.server.entity.auth.UserSession;

import java.util.Date;

/**
 * 用户登陆信息
 */
@JsonIgnoreProperties({"userId", "userCode"})
public class UserSessionRestEntity {
    private String sessionId;   //令牌
    /**
     * 登陆ID
     */
    private String userId;
    /**
     * 用户系统标识
     */
    private String userCode;
    /**
     * 用户姓名
     */
    private String staffName;
    /**
     *企业名称
     */
    private String compName;
    /**
     * 企业简称
     */
    private String compAbbr;

    private Date loginTime=new Date();

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getCompAbbr() {
        return compAbbr;
    }

    public void setCompAbbr(String compAbbr) {
        this.compAbbr = compAbbr;
    }

    public static UserSessionRestEntity getInstance(UserSession userSession) {
        if (userSession==null) return null;
        UserSessionRestEntity userSessionRest=new UserSessionRestEntity();
        userSessionRest.setSessionId(userSession.getSessionId());
        userSessionRest.setUserId(userSession.getLoginId());
        userSessionRest.setUserCode(userSession.getUserCode());
        userSessionRest.setStaffName(userSession.getStaffName());
        userSessionRest.setLoginTime(userSession.getLoginTime()==null?new Date():userSession.getLoginTime());

        return userSessionRest;
    }
}
