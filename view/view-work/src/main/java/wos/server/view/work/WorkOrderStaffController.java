package wos.server.view.work;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.mybatis.mapper.query.QueryType;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.modules.sys.service.UserService;
import com.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.web.BaseController;
import wos.server.entity.work.WorkOrderStaff;
import wos.server.service.work.WorkOrderStaffService;
import wos.server.view.BasicController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工单执行人Controller
 * @author Tyr Tao
 * @version 2025-06-11
 */
@Controller
@RequestMapping(value = "${adminPath}/work_order/workOrderStaff")
public class WorkOrderStaffController extends BasicController {

	@Autowired
	private WorkOrderStaffService workOrderStaffService;

	@Autowired
	private UserService userService;
	
	/**
	 * 获取数据
	 */
	@ModelAttribute
	public WorkOrderStaff get(String id, boolean isNewRecord) {
		return workOrderStaffService.get(id, isNewRecord);
	}
	
	/**
	 * 查询列表
	 */
	@RequiresPermissions("work_order:workOrderStaff:view")
	@RequestMapping(value = {"list", ""})
	public String list(WorkOrderStaff workOrderStaff, Model model) {
		model.addAttribute("workOrderStaff", workOrderStaff);
		return "modules/work_order/workOrderStaffList";
	}
	
	/**
	 * 查询列表数据
	 */
	@RequiresPermissions("work_order:workOrderStaff:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public Page<WorkOrderStaff> listData(WorkOrderStaff workOrderStaff, HttpServletRequest request, HttpServletResponse response) {
		workOrderStaff.setPage(new Page<>(request, response));
		Page<WorkOrderStaff> page = workOrderStaffService.findPage(workOrderStaff);
		List<WorkOrderStaff> workOrderStaffList=page.getList();
		if (workOrderStaffList!=null && !workOrderStaffList.isEmpty()) {
			List<String> userIdList=workOrderStaffList.stream().filter(p->StringUtils.isNotEmpty(p.getStaffId())).map(p->p.getStaffId()).distinct().collect(Collectors.toList());
			if (!userIdList.isEmpty()) {
				User userWhere=new User();
				userWhere.sqlMap().getWhere().and("user_code", QueryType.IN,userIdList);
				List<User> userList=userService.findList(userWhere);
				if (!userList.isEmpty()) {
					workOrderStaffList.stream().filter(p->StringUtils.isNotEmpty(p.getStaffId())).forEach(item->{
						String staffId=item.getStaffId();
						User user=userList.stream().filter(p->StringUtils.equalsIgnoreCase(p.getUserCode(),staffId)).findFirst().orElse(null);
						if (user!=null) {
							item.setStaffName(user.getUserName());
						}
					});
				}
			}

		}
		return page;
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("work_order:workOrderStaff:view")
	@RequestMapping(value = "form")
	public String form(WorkOrderStaff workOrderStaff, Model model) {
		model.addAttribute("workOrderStaff", workOrderStaff);
		if (!workOrderStaff.getIsNewRecord()) {
			String staffId=workOrderStaff.getStaffId();
			if (StringUtils.isNotEmpty(staffId)) {
				User user= UserUtils.get(staffId);
				if (user!=null) {
					workOrderStaff.setStaffName(user.getUserName());
				}
			}
		}
		return "modules/work_order/workOrderStaffForm";
	}

	/**
	 * 保存数据
	 */
	@RequiresPermissions("work_order:workOrderStaff:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated WorkOrderStaff workOrderStaff) {
		String woId=workOrderStaff.getWoId(),staffId=workOrderStaff.getStaffId();
		if (StringUtils.isEmpty(woId)) {
			return renderResultError("工单标识不允许为空");
		}
		if (StringUtils.isEmpty(staffId)) {
			return renderResultError("员工标识不允许为空");
		}
		if (!workOrderStaffService.allow2Saved(workOrderStaff.getId(),woId,staffId)) {
			return renderResultError("不允许保存，可能该员工已经被指定至该工单");
		}
		workOrderStaffService.save(workOrderStaff);
		return renderResult(Global.TRUE, text("保存工单执行人成功！"));
	}
	
	/**
	 * 删除数据
	 */
	@RequiresPermissions("work_order:workOrderStaff:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(WorkOrderStaff workOrderStaff) {
		workOrderStaffService.delete(workOrderStaff);
		return renderResult(Global.TRUE, text("删除工单执行人成功！"));
	}

	@RequiresPermissions("work_order:workOrderStaff:edit")
	@RequestMapping(value = "deletes")
	@ResponseBody
	public String deletes(String ids) {
		if (StringUtils.isEmpty(ids)) return renderResultError("批量删除失败，标识不允许为空！");
		List<String> idList= Arrays.asList(ids.split(","));
		if (idList.isEmpty()) return renderResultError("需要删除的标识异常");
		workOrderStaffService.deleteByIdList(getCompId(),idList);
		return renderResultSuccess("删除成功");
	}
	
}