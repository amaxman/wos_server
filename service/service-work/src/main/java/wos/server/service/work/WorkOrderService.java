package wos.server.service.work;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import wos.server.entity.work.WorkOrder;
import wos.server.dao.work.WorkOrderDao;
import com.jeesite.modules.file.utils.FileUploadUtils;

/**
 * 工单Service
 * @author Tyr Tao
 * @version 2025-06-11
 */
@Service
public class WorkOrderService extends CrudService<WorkOrderDao, WorkOrder> {
	
	/**
	 * 获取单条数据
	 * @param workOrder
	 * @return
	 */
	@Override
	public WorkOrder get(WorkOrder workOrder) {
		return super.get(workOrder);
	}
	
	/**
	 * 查询分页数据
	 * @param workOrder 查询条件
	 * @param workOrder page 分页对象
	 * @return
	 */
	@Override
	public Page<WorkOrder> findPage(WorkOrder workOrder) {
		return super.findPage(workOrder);
	}
	
	/**
	 * 查询列表数据
	 * @param workOrder
	 * @return
	 */
	@Override
	public List<WorkOrder> findList(WorkOrder workOrder) {
		return super.findList(workOrder);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param workOrder
	 */
	@Override
	@Transactional
	public void save(WorkOrder workOrder) {
		super.save(workOrder);
		// 保存上传图片
		FileUploadUtils.saveFileUpload(workOrder, workOrder.getId(), "workOrder_image");
	}
	
	/**
	 * 更新状态
	 * @param workOrder
	 */
	@Override
	@Transactional
	public void updateStatus(WorkOrder workOrder) {
		super.updateStatus(workOrder);
	}
	
	/**
	 * 删除数据
	 * @param workOrder
	 */
	@Override
	@Transactional
	public void delete(WorkOrder workOrder) {
		super.delete(workOrder);
	}
	
}