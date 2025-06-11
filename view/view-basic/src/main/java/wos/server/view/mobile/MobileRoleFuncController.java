package wos.server.view.mobile;

import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.mybatis.mapper.query.QueryType;
import com.jeesite.modules.sys.entity.Role;
import com.jeesite.modules.sys.service.RoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wos.server.entity.mobile.MobileFunc;
import wos.server.entity.mobile.MobileRoleFunc;
import wos.server.service.mobile.MobileFuncService;
import wos.server.service.mobile.MobileRoleFuncService;
import wos.server.view.BasicController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色功能Controller
 * @author Tyr Tao
 * @version 2023-03-07
 */
@Controller
@RequestMapping(value = "${adminPath}/mobile/mobileRoleFunc")
public class MobileRoleFuncController extends BasicController {

	@Autowired
	private MobileRoleFuncService mobileRoleFuncService;

	@Autowired
	private MobileFuncService mobileFuncService;

	@Autowired
	private RoleService roleService;
	
	/**
	 * 获取数据
	 */
	@ModelAttribute
	public MobileRoleFunc get(String id, boolean isNewRecord) {
		return mobileRoleFuncService.get(id, isNewRecord);
	}
	
	/**
	 * 查询列表
	 */
	@RequiresPermissions("mobile:mobileRoleFunc:view")
	@RequestMapping(value = {"list", ""})
	public String list(MobileRoleFunc mobileRoleFunc, Model model) {
		model.addAttribute("mobileRoleFunc", mobileRoleFunc);
		return "modules/mobile/mobileRoleFuncList";
	}
	
	/**
	 * 查询列表数据
	 */
	@RequiresPermissions("mobile:mobileRoleFunc:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public Page<MobileRoleFunc> listData(MobileRoleFunc mobileRoleFunc, HttpServletRequest request, HttpServletResponse response) {
		mobileRoleFunc.setPage(new Page<>(request, response));
		Page<MobileRoleFunc> page = mobileRoleFuncService.findPage(mobileRoleFunc);
		if (page!=null && page.getList()!=null && !page.getList().isEmpty()) {
			List<MobileRoleFunc> list=page.getList();
			List<String> roleCodeList=list.stream().filter(p->StringUtils.isNotEmpty(p.getRoleId())).map(p->p.getRoleId()).distinct().collect(Collectors.toList());
			Role whereRole=new Role();
			whereRole.sqlMap().getWhere().and("role_code", QueryType.IN,roleCodeList);
			List<Role> roleList=roleService.findList(whereRole);
			if (roleList!=null && !roleList.isEmpty()) {
				list.stream().filter(p->StringUtils.isNotEmpty(p.getRoleId())).forEach(item->{
					String roleCode=item.getRoleId();
					Role role=roleList.stream().filter(p->StringUtils.equalsIgnoreCase(p.getRoleCode(),roleCode)).findFirst().orElse(null);
					if (role!=null) {
						item.setRoleName(role.getRoleName());
					}
				});
			}
		}
		return page;
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("mobile:mobileRoleFunc:view")
	@RequestMapping(value = "form")
	public String form(MobileRoleFunc mobileRoleFunc, Model model) {
		model.addAttribute("mobileRoleFunc", mobileRoleFunc);
		if (StringUtils.isNotEmpty(mobileRoleFunc.getFuncId())) {
			MobileFunc mobileFunc=mobileFuncService.get(mobileRoleFunc.getFuncId());
			if (mobileFunc!=null) {
				mobileRoleFunc.setFuncTitle(mobileFunc.getFuncTitle());
			}
		}
		return "modules/mobile/mobileRoleFuncForm";
	}

	/**
	 * 保存数据
	 */
	@RequiresPermissions("mobile:mobileRoleFunc:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated MobileRoleFunc mobileRoleFunc) {


		String roleId=mobileRoleFunc.getRoleId(),funcId=mobileRoleFunc.getFuncId();
		if (StringUtils.isEmpty(roleId)) return renderResult(Global.FALSE, text("角色标识不允许为空"));
		if (StringUtils.isEmpty(funcId)) return renderResult(Global.FALSE, text("功能标识不允许为空"));


		if (roleService.get(roleId)==null) return renderResult(Global.FALSE, text("角色不存在，可能已经被删除"));

		if (mobileFuncService.get(funcId)==null) return renderResult(Global.FALSE, text("功能不存在，可能已经被删除"));

		if (mobileRoleFuncService.get(roleId,funcId)!=null) return renderResult(Global.FALSE, text("该功能已经添加"));

		mobileRoleFuncService.save(mobileRoleFunc);
		return renderResult(Global.TRUE, text("保存角色功能成功！"));
	}
	
	/**
	 * 删除数据
	 */
	@RequiresPermissions("mobile:mobileRoleFunc:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(MobileRoleFunc mobileRoleFunc) {


		mobileRoleFuncService.delete(mobileRoleFunc);
		return renderResult(Global.TRUE, text("删除角色功能成功！"));
	}

	@RequiresPermissions("mobile:mobileRoleFunc:edit")
	@RequestMapping(value = "deletes")
	@ResponseBody
	public String deletes(String ids) {
		String compId=getCompId();
		if (StringUtils.isEmpty(ids)) return renderResult(Global.FALSE, text("批量删除失败，标识为空！"));
		if (StringUtils.isEmpty(compId)) return renderResult(Global.FALSE, text("批量删除失败，未能获取企业信息"));
		String[] idArray=ids.split(",");
		MobileRoleFunc entityDelete=new MobileRoleFunc();
		entityDelete.setId_in(idArray);
		mobileRoleFuncService.deleteByIds(entityDelete);
		return renderResult(Global.TRUE, text("批量删除成功！"));
	}
	
}