package wos.server.view.work;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

/**
 * 工单执行人Controller
 * @author Tyr Tao
 * @version 2025-06-11
 */
@Controller
@RequestMapping(value = "${adminPath}/work_order/workOrderStaff")
public class WorkOrderStaffController extends BaseController {

	@Autowired
	private WorkOrderStaffService workOrderStaffService;
	
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
		return page;
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("work_order:workOrderStaff:view")
	@RequestMapping(value = "form")
	public String form(WorkOrderStaff workOrderStaff, Model model) {
		model.addAttribute("workOrderStaff", workOrderStaff);
		return "modules/work_order/workOrderStaffForm";
	}

	/**
	 * 保存数据
	 */
	@RequiresPermissions("work_order:workOrderStaff:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated WorkOrderStaff workOrderStaff) {
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
	
}