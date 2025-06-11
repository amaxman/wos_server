package wos.server.view;

import com.jeesite.common.config.Global;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.file.dao.FileUploadDao;
import com.jeesite.modules.sys.entity.Company;
import com.jeesite.modules.sys.entity.Employee;
import com.jeesite.modules.sys.entity.Office;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.modules.sys.service.CompanyService;
import com.jeesite.modules.sys.service.EmployeeService;
import com.jeesite.modules.sys.service.OfficeService;
import com.jeesite.modules.sys.service.UserService;
import com.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public abstract class BasicController extends BaseController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private OfficeService officeService;

    @Autowired
    private UserService userService;

    @Autowired
    protected FileUploadDao fileUploadDao;

    @Autowired  // 字段注入
    private MessageSource messageSource;

    //#region Employee 员工信息
    /**
     * 通过用户编码获取企业信息
     * @param userCode
     * @return
     */
    protected Employee getEmployeeByUserCode(String userCode) {
        Employee employee=employeeService.get(userCode);
        return employee;
    }
    //#endregion

    //#region Company 企业信息
    /**
     * 通过用户编码获取所属企业标识
     * @param userCode
     * @return
     */
    protected String getCompCodeByUserCode(String userCode) {
        Employee employee=getEmployeeByUserCode(userCode);
        return employee==null || employee.getCompany()==null?"":employee.getCompany().getCompanyCode();
    }



    /**
     * 通过CompId获取企业信息
     * @param compId
     * @return
     */
    protected Company getCompanyByCompId(String compId) {
        if (StringUtils.isEmpty(compId)) return null;
        return companyService.get(compId);
    }

    /**
     * 获取登录用户所属企业编码
     * @return
     */
    private String getCompCode() {
        try {
            String userCode= UserUtils.getLoginInfo().getId();
            if (StringUtils.isEmpty(userCode)) return "";
            return getCompCodeByUserCode(userCode);
        } catch (Exception ex) {
            return "";
        }


    }
    //#endregion

    //#region office组织架构

    /**
     * 通过OfficeCode获取Office信息
     * @param officeCode
     * @return
     */
    protected Office getOffice(String officeCode) {
        if (StringUtils.isEmpty(officeCode)) return null;
        return officeService.get(officeCode);
    }

    protected Office getOfficeByUserCode(String userCode) {
        if (StringUtils.isEmpty(userCode)) return null;
        Employee employee=getEmployeeByUserCode(userCode);
        if (employee==null) return null;
        return employee.getOffice();
    }

    /**
     * 通过员工编号获取OfficeCode
     * @param userCode
     * @return
     */
    protected String getOfficeCodeByUserCode(String userCode) {
        if (StringUtils.isEmpty(userCode)) return null;
        Employee employee=getEmployeeByUserCode(userCode);
        if (employee==null) return null;
        Office office=employee.getOffice();
        if (office==null) return null;
        if (office.getParent()==null) return office.getOfficeCode();
        String parentsCodes=office.getParentCodes();
        if (StringUtils.isEmpty(parentsCodes)) return office.getOfficeCode();
        String[] officeCodeArr=parentsCodes.split(",");
        if (officeCodeArr.length<2) return office.getOfficeCode();
        return officeCodeArr[1];
    }

    /**
     * 获取工厂标识
     * @return
     */
    protected String getCompId() {
        String userCode=getUserCode();
        if (StringUtils.isEmpty(userCode)) return null;
        return getOfficeCodeByUserCode(userCode);
    }
    //#endregion

    //#region 员工信息
    protected String getUserCode() {
        try {
            String userCode= UserUtils.getLoginInfo().getId();
            return userCode;
        } catch (Exception ex) {
            return "";
        }
    }

    protected String getUserName() {
        try {
            String userName= UserUtils.getUser().getUserName();
            return userName;
        } catch (Exception ex) {
            return "";
        }
    }

    protected String getUserRefName() {
        try {
            String userName= UserUtils.getUser().getRefName();
            return userName;
        } catch (Exception ex) {
            return "";
        }
    }
    //#endregion

    //#region 用户信息
    protected User getUserByLoginCode(String loginCode) {
        if (StringUtils.isEmpty(loginCode)) return null;
        return UserUtils.getByLoginCode(loginCode);
    }

    protected User getUserByUserCode(String userCode) {
        if (StringUtils.isEmpty(userCode)) return null;
        return UserUtils.get(userCode);
    }
    //#endregion


    //#region renderResult
    protected String renderResultError(String message) {
        return renderResult(Global.FALSE, message);
    }

    protected String renderResultError(String message,Object data) {
        return renderResult(Global.FALSE, message,data);
    }

    protected String renderResultSuccess(String message) {
        return renderResult(Global.TRUE, message);
    }

    protected String renderResultSuccess(String message,Object obj) {
        return renderResult(Global.TRUE, message,obj);
    }
    //#endregion

    //#region 多语言

    /**
     * 根据编号获取语言内容
     *
     * @param request
     * @param code
     * @return
     */
    protected String _text(HttpServletRequest request, String code) {
        Locale locale = RequestContextUtils.getLocale(request);
        return messageSource.getMessage(code, null, locale);
    }
    //#endregion
}
