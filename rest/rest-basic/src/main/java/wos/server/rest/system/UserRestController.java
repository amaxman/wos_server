package wos.server.rest.system;

import com.alibaba.fastjson.JSONObject;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.modules.sys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wos.server.rest.BasicRestController;
import wos.server.rest.JsonMsg;
import wos.server.rest.auth.Caches;
import wos.server.rest.auth.entity.UserSessionRestEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "${adminPath}/rest/system/user")
public class UserRestController extends BasicRestController {
    @Autowired
    private UserService userService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/getAllUser")
    public JsonMsg<List<JSONObject>> getAllUser(String sessionId) {
        //#region 用户信息
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId);
        if (userSessionRestEntity == null) return error("请先登陆");
        //#endregion

        //#region 用户信息
        User where = new User();
        List<User> userList = userService.findList(where);
        //#endregion

        //#region 返回数据
        List<JSONObject> list = new ArrayList<>();
        userList.stream().filter(p->!Arrays.asList("admin","system").contains(p.getUserCode())).forEach(user -> {
            String userCode = user.getUserCode(), userName = user.getUserName();
            JSONObject json = new JSONObject();
            json.put("userCode", userCode);
            json.put("userName", userName);
            list.add(json);
        });
        return success(list, "获取用户成功");
        //#endregion
    }
}
