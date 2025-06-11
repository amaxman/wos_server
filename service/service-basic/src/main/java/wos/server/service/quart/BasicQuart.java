package wos.server.service.quart;

import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.mybatis.mapper.query.QueryType;
import com.jeesite.modules.file.dao.FileUploadDao;
import com.jeesite.modules.file.entity.FileUpload;
import com.jeesite.modules.sys.entity.Employee;
import com.jeesite.modules.sys.entity.Office;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.modules.sys.service.EmployeeService;
import com.jeesite.modules.sys.service.OfficeService;
import com.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicQuart {
    //#region 变量

    @Autowired
    private OfficeService officeService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FileUploadDao fileUploadDao;

    //#endregion

    /**
     * 获取所有工厂信息
     * @return
     */
    protected List<Office> getFactList() {
        Office where=new Office();
        where.setStatus(Office.STATUS_NORMAL);
        where.setParentCode("0");
        List<Office> officeList=officeService.findList(where);
        return officeList;
    }

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

    /**
     * 根据FactId获取Office
     * @param factId
     * @return
     */
    protected List<Office> getAllOfficeByFactId(String factId) {
        List<Office> officeList=officeService.findListByParentCodes(factId);
        return officeList;
    }

    /**
     * 获取制定OfficeId列表下所有员工
     * @param officeCodeList
     * @return
     */
    protected List<Employee> getAllEmployeeByOfficeCode(List<String> officeCodeList,List<String> dingDingAttUserIdList) {
        if (officeCodeList==null || officeCodeList.isEmpty()) return new ArrayList<>();
        Employee employee=new Employee();
        employee.sqlMap().getWhere().and("office_code", QueryType.IN,officeCodeList);
        employee.sqlMap().getWhere().and("emp_code", QueryType.IN,dingDingAttUserIdList);
        return employeeService.findList(employee);
    }

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



    private String getText(FileUpload fileUpload) {
        try {
            if (fileUpload==null || fileUpload.getFileEntity()==null || StringUtils.isEmpty(fileUpload.getFileEntity().getFileRealPath())) return "";
            String fileName=fileUpload.getFileEntity().getFileRealPath();
            File file=new File(fileName);
            if (file==null || !file.exists() || file.isDirectory()) return "";
            List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
            return String.join("\n",lines);
        } catch (Exception ex) {
            return "";
        }

    }

    public String drooolsCode2String(String codes) {
        if (StringUtils.isEmpty(codes)) return "";
        codes=codes.replace("\\42","\"");
        codes=codes.replace("＜","<").replace("＞",">");
        codes=codes.replace("\\n","\n");
        return codes;

    }



    /**
     * delete Files
     * @param fileNameList
     */
    protected void deleteFile(List<String> fileNameList) {
        if (fileNameList==null || fileNameList.isEmpty()) return;
        fileNameList.forEach(fileName->{
            if (StringUtils.isEmpty(fileName)) return;
            File file = new File(fileName);
            if (file.isFile() && file.exists()) file.delete();
        });
    }
}
