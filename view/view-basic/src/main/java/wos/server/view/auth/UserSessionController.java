package wos.server.view.auth;

import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wos.server.entity.auth.UserSession;
import wos.server.rest.auth.Caches;
import wos.server.service.auth.UserSessionService;
import wos.server.view.BasicController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录令牌Controller
 *
 * @author Tyr Tao
 * @version 2022-05-15
 */
@Controller
@RequestMapping(value = "${adminPath}/auth/userSession")
public class UserSessionController extends BasicController {

    @Autowired
    private UserSessionService userSessionService;

    /**
     * 获取数据
     */
    @ModelAttribute
    public UserSession get(String userCode, boolean isNewRecord) {
        return userSessionService.get(userCode, isNewRecord);
    }

    /**
     * 查询列表
     */
    @RequiresPermissions("auth:userSession:view")
    @RequestMapping(value = {"list", ""})
    public String list(UserSession userSession, Model model) {
        model.addAttribute("userSession", userSession);
        return "modules/auth/userSessionList";
    }

    /**
     * 查询列表数据
     */
    @RequiresPermissions("auth:userSession:view")
    @RequestMapping(value = "listData")
    @ResponseBody
    public Page<UserSession> listData(UserSession userSession, HttpServletRequest request, HttpServletResponse response) {
        userSession.setPage(new Page<>(request, response));
        Page<UserSession> page = userSessionService.findPage(userSession);
        return page;
    }

    /**
     * 查看编辑表单
     */
    @RequiresPermissions("auth:userSession:view")
    @RequestMapping(value = "form")
    public String form(UserSession userSession, Model model) {
        model.addAttribute("userSession", userSession);
        return "modules/auth/userSessionForm";
    }

    /**
     * 保存数据
     */
    @RequiresPermissions("auth:userSession:edit")
    @PostMapping(value = "save")
    @ResponseBody
    public String save(HttpServletRequest request, @Validated UserSession userSession) {
        userSessionService.save(userSession);
        return renderResult(Global.TRUE, _text(request, "token.info.save.suc"));
    }

    /**
     * 删除数据
     */
    @RequiresPermissions("auth:userSession:edit")
    @RequestMapping(value = "delete")
    @ResponseBody
    public String delete(UserSession userSession) {
        String userCode = userSession.getUserCode();
        if (StringUtils.isNotEmpty(userCode)) {
            try {
                Caches.removeUserSessionByUserCode(userCode);
            } catch (Exception ex) {

            }
        }
        userSessionService.delete(userSession);
        return renderResult(Global.TRUE, text("删除用户登录令牌成功！"));
    }

}