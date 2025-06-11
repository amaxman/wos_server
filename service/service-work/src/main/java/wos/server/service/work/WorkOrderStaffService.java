package wos.server.service.work;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import wos.server.entity.work.WorkOrderStaff;
import wos.server.dao.work.WorkOrderStaffDao;

/**
 * 工单执行人Service
 * @author Tyr Tao
 * @version 2025-06-11
 */
@Service
public class WorkOrderStaffService extends CrudService<WorkOrderStaffDao, WorkOrderStaff> {
	
	/**
	 * 获取单条数据
	 * @param workOrderStaff
	 * @return
	 */
	@Override
	public WorkOrderStaff get(WorkOrderStaff workOrderStaff) {
		return super.get(workOrderStaff);
	}
	
	/**
	 * 查询分页数据
	 * @param workOrderStaff 查询条件
	 * @param workOrderStaff page 分页对象
	 * @return
	 */
	@Override
	public Page<WorkOrderStaff> findPage(WorkOrderStaff workOrderStaff) {
		return super.findPage(workOrderStaff);
	}
	
	/**
	 * 查询列表数据
	 * @param workOrderStaff
	 * @return
	 */
	@Override
	public List<WorkOrderStaff> findList(WorkOrderStaff workOrderStaff) {
		return super.findList(workOrderStaff);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param workOrderStaff
	 */
	@Override
	@Transactional
	public void save(WorkOrderStaff workOrderStaff) {
		super.save(workOrderStaff);
	}
	
	/**
	 * 更新状态
	 * @param workOrderStaff
	 */
	@Override
	@Transactional
	public void updateStatus(WorkOrderStaff workOrderStaff) {
		super.updateStatus(workOrderStaff);
	}
	
	/**
	 * 删除数据
	 * @param workOrderStaff
	 */
	@Override
	@Transactional
	public void delete(WorkOrderStaff workOrderStaff) {
		super.delete(workOrderStaff);
	}
	
}