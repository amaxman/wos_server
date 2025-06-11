package wos.server.view.sys;

import com.jeesite.common.collect.MapUtils;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.sys.entity.Office;
import com.jeesite.modules.sys.entity.Role;
import com.jeesite.modules.sys.service.RoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/basic/sys/role")
public class RoleBasicController extends BaseController {

    @Autowired
    private RoleService roleService;

    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    @ResponseBody
    public List<Map<String, Object>> treeDataAllFact() {
        List<Map<String, Object>> mapList=new ArrayList<>();

        Role where=new Role();
        where.setStatus(Office.STATUS_NORMAL);
//        where.setIsSys("0");

        List<Role> officeList=roleService.findList(where);

        officeList.forEach(office -> {
            Map<String, Object> map = MapUtils.newHashMap();
            map.put("id", office.getRoleCode());
            map.put("name", office.getRoleName());
            map.put("title", office.getRoleName());
            map.put("isParent", false);
            mapList.add(map);
        });
        return mapList;
    }
}
