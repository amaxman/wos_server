package wos.server.view.mobile;

import com.jeesite.common.collect.ListUtils;
import com.jeesite.common.collect.MapUtils;
import com.jeesite.common.config.Global;
import com.jeesite.common.entity.DataEntity;
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
import wos.server.entity.mobile.MobileFunc;
import wos.server.view.BasicController;
import wos.server.service.mobile.MobileFuncService;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 移动端功能码Controller
 * @author Tyr Tao
 * @version 2023-03-07
 */
@Controller
@RequestMapping(value = "${adminPath}/mobile/mobileFunc")
public class MobileFuncController extends BasicController {

	@Autowired
	private MobileFuncService mobileFuncService;
	
	/**
	 * 获取数据
	 */
	@ModelAttribute
	public MobileFunc get(String id, boolean isNewRecord) {
		return mobileFuncService.get(id, isNewRecord);
	}
	
	/**
	 * 查询列表
	 */
	@RequiresPermissions("mobile:mobileFunc:view")
	@RequestMapping(value = {"list", ""})
	public String list(MobileFunc mobileFunc, Model model) {
		model.addAttribute("mobileFunc", mobileFunc);
		return "modules/mobile/mobileFuncList";
	}
	
	/**
	 * 查询列表数据
	 */
	@RequiresPermissions("mobile:mobileFunc:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public Page<MobileFunc> listData(MobileFunc mobileFunc, HttpServletRequest request, HttpServletResponse response) {
		mobileFunc.setPage(new Page<>(request, response));
		Page<MobileFunc> page = mobileFuncService.findPage(mobileFunc);
		if (page!=null && page.getList()!=null && !page.getList().isEmpty()) {
			mobileFuncService.attacheImage(page.getList());
		}
		return page;
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("mobile:mobileFunc:view")
	@RequestMapping(value = "form")
	public String form(MobileFunc mobileFunc, Model model) {
		if (mobileFunc.getIsNewRecord()) {
			mobileFunc.setOrderNum(mobileFuncService.getNextOrderNo());
		}
		model.addAttribute("mobileFunc", mobileFunc);
		return "modules/mobile/mobileFuncForm";
	}

	/**
	 * 保存数据
	 */
	@RequiresPermissions("mobile:mobileFunc:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated MobileFunc mobileFunc) {
		if (!mobileFuncService.checkFunCode(mobileFunc)) return renderResultError("功能码已经被占用");
		mobileFuncService.save(mobileFunc);
		return renderResult(Global.TRUE, text("保存移动端功能码成功！"));
	}
	
	/**
	 * 删除数据
	 */
	@RequiresPermissions("mobile:mobileFunc:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(MobileFunc mobileFunc) {
		mobileFuncService.delete(mobileFunc);
		return renderResult(Global.TRUE, text("删除移动端功能码成功！"));
	}

	@RequiresPermissions("mobile:mobileFunc:edit")
	@RequestMapping(value = "deletes")
	@ResponseBody
	public String deletes(String ids) {
		if (StringUtils.isEmpty(ids)) return renderResult(Global.FALSE, text("删除失败，删除标识为空！"));
		String[] idArray=ids.split(",");
		MobileFunc where=new MobileFunc();
		where.setId_in(idArray);

		mobileFuncService.deleteByIds(where);

		return renderResult(Global.TRUE, text("批量删除移动端功能码成功！"));
	}

	@RequiresPermissions("mobile:mobileRoleFunc:edit")
	@RequestMapping(value = "treeData")
	@ResponseBody
	public List<Map<String, Object>> treeData() {

		List<Map<String, Object>> mapList = ListUtils.newArrayList();
		MobileFunc mobileFunc=new MobileFunc();
		mobileFunc.setStatus(DataEntity.STATUS_NORMAL);
		List<MobileFunc> mobileFuncList=mobileFuncService.findList(mobileFunc);
		if (mobileFuncList==null || mobileFuncList.isEmpty()) return mapList;

		mobileFuncList.forEach(item->{
			Map<String, Object> map = MapUtils.newHashMap();
			map.put("id", item.getId());
			map.put("pId", "");
			map.put("name", item.getFuncTitle());
			map.put("title", item.getFuncCode());
			map.put("isParent", false);
			mapList.add(map);
		});
		return mapList;
	}
	
}