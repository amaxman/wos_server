package wos.server.service.work;

import java.util.List;

import com.jeesite.common.collect.ListUtils;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.mybatis.mapper.query.QueryType;
import com.jeesite.common.service.CrudServiceEx;
import com.jeesite.modules.file.entity.FileUpload;
import com.jeesite.modules.file.utils.FileUploadUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import wos.server.entity.work.WorkOrder;
import wos.server.entity.work.WorkOrderStaff;
import wos.server.dao.work.WorkOrderStaffDao;

/**
 * 工单执行人Service
 * @author Tyr Tao
 * @version 2025-06-11
 */
@Service
public class WorkOrderStaffService extends CrudServiceEx<WorkOrderStaffDao, WorkOrderStaff> {

	private final String bizType="workOrderStaff_image";
	
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
		// 保存上传图片
		FileUploadUtils.saveFileUpload(workOrderStaff, workOrderStaff.getId(), bizType);
		deleteRemovedFile(workOrderStaff.getId(), bizType);	//删除已经删除的文件
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
		dao.phyDelete(workOrderStaff);
		List<FileUpload> fileUploadList=FileUploadUtils.findFileUpload(workOrderStaff.getId(),bizType);
		deleteFileUpload(fileUploadList);
	}

	@Transactional
	public void deleteByIdList(String compId,List<String> idList) {
		if (ListUtils.isEmpty(idList) || StringUtils.isEmpty(compId)) return;

		WorkOrderStaff where=new WorkOrderStaff();
		where.sqlMap().getWhere().and("id", QueryType.IN,idList);

		//#region 获取所有将要被删除的数据
		List<WorkOrderStaff> list=dao.findList(where);
		list.forEach(item->{
			List<FileUpload> fileUploadList=FileUploadUtils.findFileUpload(item.getId(),bizType);
			deleteFileUpload(fileUploadList);
		});
		//#endregion

		dao.phyDeleteByEntity(where);
	}

	/**
	 * 判定是否允许保存
	 * @param id
	 * @param woId
	 * @param staffId
	 * @return
	 */
	public boolean allow2Saved(String id,String woId,String staffId) {
		if (StringUtils.isEmpty(woId) || StringUtils.isEmpty(staffId)) return false;
		WorkOrderStaff where=new WorkOrderStaff();
		where.setWoId(woId);
		where.setStaffId(staffId);
		if (StringUtils.isNotEmpty(id)) where.sqlMap().getWhere().and("id",QueryType.NE,id);
		List<WorkOrderStaff> list=dao.findList(where);
		return list==null || list.isEmpty();
	}

	public Page<WorkOrderStaff> findMyPage(WorkOrderStaff workOrderStaff) {
		Page<WorkOrderStaff> page=workOrderStaff.getPage();
		if (page==null) {
			page=new Page<>();
			workOrderStaff.setPage(page);
		}
		List<WorkOrderStaff> list=dao.findMyList(workOrderStaff);
		page.setList(list);
		return page;
	}
	
}