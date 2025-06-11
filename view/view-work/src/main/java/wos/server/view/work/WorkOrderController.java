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
import wos.server.entity.work.WorkOrder;
import wos.server.service.work.WorkOrderService;

/**
 * 工单Controller
 * @author Tyr Tao
 * @version 2025-06-11
 */
@Controller
@RequestMapping(value = "${adminPath}/work_order/workOrder")
public class WorkOrderController extends BaseController {

	@Autowired
	private WorkOrderService workOrderService;
	
	/**
	 * 获取数据
	 */
	@ModelAttribute
	public WorkOrder get(String id, boolean isNewRecord) {
		return workOrderService.get(id, isNewRecord);
	}
	
	/**
	 * 查询列表
	 */
	@RequiresPermissions("work_order:workOrder:view")
	@RequestMapping(value = {"list", ""})
	public String list(WorkOrder workOrder, Model model) {
		model.addAttribute("workOrder", workOrder);
		return "modules/work_order/workOrderList";
	}
	
	/**
	 * 查询列表数据
	 */
	@RequiresPermissions("work_order:workOrder:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public Page<WorkOrder> listData(WorkOrder workOrder, HttpServletRequest request, HttpServletResponse response) {
		workOrder.setPage(new Page<>(request, response));
		Page<WorkOrder> page = workOrderService.findPage(workOrder);
		return page;
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("work_order:workOrder:view")
	@RequestMapping(value = "form")
	public String form(WorkOrder workOrder, Model model) {
		model.addAttribute("workOrder", workOrder);
		return "modules/work_order/workOrderForm";
	}

	/**
	 * 保存数据
	 */
	@RequiresPermissions("work_order:workOrder:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated WorkOrder workOrder) {
		workOrderService.save(workOrder);
		return renderResult(Global.TRUE, text("保存工单成功！"));
	}
	
	/**
	 * 删除数据
	 */
	@RequiresPermissions("work_order:workOrder:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(WorkOrder workOrder) {
		workOrderService.delete(workOrder);
		return renderResult(Global.TRUE, text("删除工单成功！"));
	}
	
}