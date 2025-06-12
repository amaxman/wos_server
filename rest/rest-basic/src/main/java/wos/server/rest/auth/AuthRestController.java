package wos.server.rest.auth;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.entity.Page;
import com.jeesite.common.idgen.IdGen;
import com.jeesite.common.lang.DateUtils;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.modules.sys.entity.Company;
import com.jeesite.modules.sys.entity.Employee;
import com.jeesite.modules.sys.entity.Role;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.modules.sys.service.UserService;
import com.jeesite.modules.sys.utils.PwdUtils;
import com.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import wos.server.entity.auth.UserSession;
import wos.server.entity.mobile.MobileFunc;
import wos.server.rest.BasicRestController;
import wos.server.rest.JsonMsg;
import wos.server.rest.auth.entity.UserPermRestEntity;
import wos.server.rest.auth.entity.UserSessionRestEntity;
import wos.server.service.auth.UserSessionService;
import wos.server.service.mobile.MobileFuncService;
import wos.server.service.mobile.MobileRoleFuncService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "${adminPath}/rest/auth")
public class AuthRestController extends BasicRestController {

    //#region 变量

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private UserService userService;

    @Autowired
    public MobileRoleFuncService mobileRoleFuncService;

    @Autowired
    private MobileFuncService mobileFuncService;

    //#endregion

    //#region 自动加载
    @PostConstruct
    private void loadCache() {
        System.out.println("===============Start Load Cache==================");
        loadAccessToken();
        System.out.println("===============Load Cache Complete===============");
    }

    private void loadAccessToken() {
        //#region 分页获取登陆令牌信息
        int pageNo = 1, pageSize = 20;
        Page<UserSession> page = new Page<>(pageNo, pageSize);
        UserSession userSession = new UserSession();
        userSession.setPage(page);
        page = userSessionService.findPage(userSession);

        while (page != null && pageSize * pageNo >= page.getCount()) {
            //#region 分页获取用户登陆信息 userSessionListPage
            List<UserSession> userSessionListPage = page.getList();
            //#endregion

            userSessionListPage.stream()
                    .filter(p -> StringUtils.isNotEmpty(p.getLoginId()))
                    .forEach(item -> {
                        UserSessionRestEntity userSessionRestEntity = UserSessionRestEntity.getInstance(item);
                        if (userSessionRestEntity == null) return;
                        Caches.putUserSession(userSessionRestEntity);
                    });

            page.setPageNo(++pageNo);
            if (pageSize * pageNo > page.getCount()) break;
            page = userSessionService.findPage(userSession);
        }
        //#endregion
    }


    //#endregion

    //#region 登陆

    /**
     * 通过企业自主账号登陆
     *
     * @param userId
     * @param userPassword
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/login")
    @ResponseBody
    @ApiOperation(value = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户名", required = true, paramType = "query", type = "String"),
            @ApiImplicitParam(name = "userPassword", value = "密码", paramType = "query", required = true, type = "String"),
    })
    public JsonMsg<UserSessionRestEntity> login(HttpServletRequest request, String userId, String userPassword) {
        //#region 参数校验
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userPassword))
            return error("用户名与密码不允许为空");
        //#endregion

        //#region 授权
        try {
            UserSessionRestEntity userSessionRest = getUserSession(request, userId, userPassword);
            if (userSessionRest == null) return error("登陆失败");
            Caches.putUserSession(userSessionRest);
            return JsonMsg.instance(userSessionRest,"登陆成功");
        } catch (Exception ex) {
            return JsonMsg.error(ex.getMessage());
        }
        //#endregion
    }

    /**
     *  获取用户令牌
     * @param userId
     * @param userPassword
     * @return
     * @throws Exception
     */
    /**
     * 获取用户令牌
     *
     * @param userId
     * @param userPassword
     * @return
     * @throws Exception
     */
    private UserSessionRestEntity getUserSession(HttpServletRequest request, String userId, String userPassword) throws Exception {

        //#region 校验用户信息
        User userQuery = new User();
        userQuery.setLoginCode(userId);
        User user = userService.getByLoginCode(userQuery);
        if (user == null) throw new Exception("用户不存在");
        String userCode = user.getUserCode();
        //#endregion

        //#region 验证旧密码
        if (!PwdUtils.validatePassword(userPassword, user.getPassword()))
            throw new Exception("密码错误");
        //#endregion

        //#region 校验员工状态
        if (!StringUtils.equalsIgnoreCase(user.getStatus(), DataEntity.STATUS_NORMAL))
            throw new Exception("状态异常");
        //#endregion

        //#region User Session
        UserSession userSession = userSessionService.get(userCode);
        if (userSession == null) {
            userSession = new UserSession();
            userSession.setUserCode(user.getUserCode());
            userSession.setLoginId(userId);
            userSession.setStaffName(user.getUserName());
            userSession.setSessionId(IdGen.uuid());
            userSession.setIsNewRecord(true);
        }
        userSession.setLoginTime(new Date());
        userSessionService.save(userSession);
        //#endregion

        //#region 返回信息
        UserSessionRestEntity userSessionRest = Caches.getUserSessionByUserCode(userSession.getUserCode());
        if (userSessionRest != null) {
            userSessionRest.setLoginTime(new Date());
            return userSessionRest;
        }
        userSessionRest = UserSessionRestEntity.getInstance(userSession);
        //#endregion

        return userSessionRest;
    }

    /**
     * 通过令牌登陆
     *
     * @param sessionId
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/loginBySession")
    @ResponseBody
    @ApiOperation(value = "登录")
    public JsonMsg<UserSessionRestEntity> login(HttpServletRequest request, String sessionId) {
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId); //自缓存中获取用户信息
        if (userSessionRestEntity != null) {
            return success(userSessionRestEntity,"登陆成功");
        }

        UserSession userSession = userSessionService.getBySessionId(sessionId);
        if (userSession == null) return JsonMsg.error("登陆失败");

        try {
            userSessionRestEntity = UserSessionRestEntity.getInstance(userSession);
        } catch (Exception ex) {
            return error(ex.getMessage());
        }

        Caches.putUserSession(userSessionRestEntity);

        return JsonMsg.instance(userSessionRestEntity, "登陆成功");

    }

    /**
     * 校验企业与用户状态
     *
     * @param userCode
     * @throws Exception
     */
    private void checkAuth(HttpServletRequest request, String userCode) throws Exception {
        //#region 企业信息
        if (StringUtils.isEmpty(userCode))
            throw new Exception("用户编码不允许为空");

        //#endregion

        //#region 员工信息
        User user = userService.get(userCode);
        if (user == null) throw new Exception("用户不存在");
        if (!StringUtils.equalsIgnoreCase(user.getStatus(), DataEntity.STATUS_NORMAL))
            throw new Exception("用户状态异常");
        //#endregion

    }

    //#endregion

    //#region 修改密码

    /**
     * 修改当前用户密码
     *
     * @param sessionId
     * @param password
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/changePassword")
    @ResponseBody
    public JsonMsg<Void> changePassword(HttpServletRequest request, String sessionId, String password, String newPassword) {
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(newPassword))
            return error("新旧密码均不允许为空");
        if (StringUtils.equalsIgnoreCase(password, newPassword))
            return error("新旧密码不允许相同");

        //#region 用户信息
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId);
        if (userSessionRestEntity == null) return JsonMsg.error("请先登陆");
        //#endregion

        //#region 修改密码
        String userCode = userSessionRestEntity.getUserCode();
        User user = userService.get(userCode);
        if (user == null) return error("用户不存在");
        if (!PwdUtils.validatePassword(password, user.getPassword()))
            return error("用户密码错误");
        try {
            userService.updatePassword(user.getUserCode(), newPassword);
        } catch (Exception ex) {
            return error(ex.getMessage());
        }

        //#endregion

        return success("修改密码成功");
    }
    //#endregion

    //#region 权限
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, path = "/getAllPermission")
    @ResponseBody
    public JsonMsg<List<UserPermRestEntity>> getAllPermission(HttpServletRequest request, String sessionId) {
        if (StringUtils.isEmpty(sessionId))
            return error("参数错误");

        //#region 用户信息
        UserSessionRestEntity userSessionRest = Caches.getUserSession(sessionId);
        if (userSessionRest == null) return JsonMsg.error("请先登陆");
        String userCode = userSessionRest.getUserCode(), loginCode = userSessionRest.getUserId();
        if (StringUtils.isEmpty(userCode)) return error("用户编码不存在");
        //#endregion

        //#region 根据用户名获取角色信息
        User user = UserUtils.getByLoginCode(loginCode);
        if (user == null) return error("用户不存在，可能已经被删除");
        if (!StringUtils.equalsIgnoreCase(user.getStatus(), DataEntity.STATUS_NORMAL))
            return error("状态异常");
        //#endregion

        //#region 获取用户角色
        List<Role> roleList = user.getRoleList();
        if (roleList == null || roleList.isEmpty()) return error("无权限");
        //#endregion

        //#region 获取角色权限列表
        List<String> roleCodeList = roleList.stream().map(p -> p.getRoleCode()).distinct().collect(Collectors.toList());
        List<MobileFunc> mobileFuncList = mobileFuncService.getListByRoleCodeList(roleCodeList);
        if (mobileFuncList == null || mobileFuncList.isEmpty())
            return error("无权限");
        //#endregion

        //#region 附加图片信息
        mobileFuncService.attacheImage(mobileFuncList);
        //#endregion

        //#region 返回数据
        List<UserPermRestEntity> resultList = mobileFuncList.stream().map(p -> UserPermRestEntity.getInstance(p)).collect(Collectors.toList());
        return success(resultList, "获取权限成功");
        //#endregion
    }
    //#endregion

}
