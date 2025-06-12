package wos.server.service.work;

import java.util.List;

import com.jeesite.common.collect.ListUtils;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.mybatis.mapper.query.QueryType;
import com.jeesite.common.service.CrudServiceEx;
import com.jeesite.modules.file.entity.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import wos.server.dao.work.WorkOrderStaffDao;
import wos.server.entity.work.WorkOrder;
import wos.server.dao.work.WorkOrderDao;
import com.jeesite.modules.file.utils.FileUploadUtils;
import wos.server.entity.work.WorkOrderStaff;

/**
 * 工单Service
 * @author Tyr Tao
 * @version 2025-06-11
 */
@Service
public class WorkOrderService extends CrudServiceEx<WorkOrderDao, WorkOrder> {

	@Autowired
	private WorkOrderStaffDao workOrderStaffDao;

	public static final String bizType="workOrder_image";
	private final String staffBizType="workOrderStaff_image";

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
		FileUploadUtils.saveFileUpload(workOrder, workOrder.getId(), bizType);
		deleteRemovedFile(workOrder.getId(), bizType);	//删除已经删除的文件
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
		if (StringUtils.isNotEmpty(workOrder.getId())) {
			WorkOrderStaff workOrderStaff=new WorkOrderStaff();
			workOrderStaff.setWoId(workOrder.getId());
			deleteFileUpload(workOrderStaff);
			workOrderStaffDao.phyDeleteByEntity(workOrderStaff);
		}

		List<FileUpload> fileUploadList=FileUploadUtils.findFileUpload(workOrder.getId(),bizType);
		deleteFileUpload(fileUploadList);

		dao.phyDelete(workOrder);
	}

	/**
	 * 删除执行人文件
	 * @param workOrderStaff
	 */
	private void deleteFileUpload(WorkOrderStaff workOrderStaff) {
		List<WorkOrderStaff> workOrderStaffList=workOrderStaffDao.findList(workOrderStaff);
		workOrderStaffList.forEach(item->{
			List<FileUpload> fileUploadList=FileUploadUtils.findFileUpload(item.getId(),staffBizType);
			deleteFileUpload(fileUploadList);
		});
	}

	@Transactional
	public void deleteByIdList(String compId,List<String> idList) {
		if (ListUtils.isEmpty(idList) || StringUtils.isEmpty(compId)) return;

		//#region 删除所有员工信息
		WorkOrderStaff whereStaff=new WorkOrderStaff();
		whereStaff.sqlMap().getWhere().and("wo_id",QueryType.IN,idList);
		deleteFileUpload(whereStaff);
		workOrderStaffDao.phyDeleteByEntity(whereStaff);
		//#endregion

		WorkOrder where=new WorkOrder();
		where.sqlMap().getWhere().and("id", QueryType.IN,idList);
		//#region 获取所有将要被删除的数据
		List<WorkOrder> workOrderList=dao.findList(where);
		workOrderList.forEach(item->{
			List<FileUpload> fileUploadList=FileUploadUtils.findFileUpload(item.getId(),bizType);
			deleteFileUpload(fileUploadList);
		});
		//#endregion

		dao.phyDeleteByEntity(where);
	}
	
}