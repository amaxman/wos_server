package wos.server.rest;

import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.mybatis.mapper.query.QueryType;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.file.dao.FileUploadDao;
import com.jeesite.modules.file.entity.FileUpload;
import com.jeesite.modules.sys.entity.Company;
import com.jeesite.modules.sys.entity.Employee;
import com.jeesite.modules.sys.entity.Office;
import com.jeesite.modules.sys.service.CompanyService;
import com.jeesite.modules.sys.service.EmployeeService;
import com.jeesite.modules.sys.service.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("${adminPath}/rest/basic")
public class BasicRestController extends BaseController {

    //#region 变量
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private OfficeService officeService;

    @Autowired
    private FileUploadDao fileUploadDao;

    @Autowired  // 字段注入
    private MessageSource messageSource;
    //#endregion

    //#region 组织架构

    /**
     * 通过OfficeCode获取Office信息
     *
     * @param officeCode
     * @return
     */
    protected Office getOffice(String officeCode) {
        if (StringUtils.isEmpty(officeCode)) return null;
        return officeService.get(officeCode);
    }

    /**
     * 通过用户编码获取所属企业标识
     *
     * @param userCode
     * @return
     */
    protected String getCompCodeByUserCode(String userCode) {
        Employee employee = employeeService.get(userCode);
        return employee == null || employee.getCompany() == null ? "" : employee.getCompany().getCompanyCode();
    }

    /**
     * 通过用户编码获取企业信息
     *
     * @param userCode
     * @return
     */
    protected Employee getEmployeeByUserCode(String userCode) {
        Employee employee = employeeService.get(userCode);
        return employee;
    }

    /**
     * 通过CompId获取企业信息
     *
     * @param compId
     * @return
     */
    protected Company getCompanyByCompId(String compId) {
        if (StringUtils.isEmpty(compId)) return null;
        return companyService.get(compId);
    }

    protected List<Company> findListByCompIdList(List<String> compIdList) {
        if (compIdList == null || compIdList.isEmpty()) return new ArrayList<>();
        Company company = new Company();
        company.sqlMap().getWhere().and("company_code", QueryType.IN, compIdList);
        return companyService.findList(company);
    }


    //#endregion

    //#region JSON消息

    /**
     * 错误
     *
     * @param err
     * @param <T>
     * @return
     */
    protected <T> JsonMsg<T> error(String err) {
        return JsonMsg.error(err);
    }

    protected <T> JsonMsg<T> error(HttpServletRequest request, String code) {
        return JsonMsg.error(_text(request,code));
    }

    /**
     * 成功
     *
     * @param msg
     * @param <T>
     * @return
     */
    protected <T extends Object> JsonMsg<T> success(String msg) {
        return success(null, msg);
    }

    protected <T extends Object> JsonMsg<T> success_code(HttpServletRequest request, String code) {
        return success(null, _text(request,code));
    }

    /**
     * 成功
     *
     * @param t
     * @param msg
     * @param <T>
     * @return
     */
    protected <T extends Object> JsonMsg<T> success(T t, String msg) {
        return JsonMsg.instance(t, msg);
    }
    //#endregion


    //#region 数据类型转换
    protected double convertDouble(Double value, int precision) {
        if (value == null || precision <= 0) return 0;
        return Double.parseDouble(String.format("%." + precision + "f", value));
    }
    //#endregion

    //#region 文件
    protected List<FileUpload> getFileUploadList(List<String> bizKeyList, String bizType) {
        if (bizKeyList != null && !bizKeyList.isEmpty() && !StringUtils.isEmpty(bizType)) {
            FileUpload fileUpload = new FileUpload();
            fileUpload.sqlMap().getWhere().and("biz_key", QueryType.IN, bizKeyList);
            fileUpload.setBizType(bizType);
            fileUpload.setStatus("0");
            return this.fileUploadDao.findList(fileUpload);
        } else {
            return new ArrayList();
        }
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
