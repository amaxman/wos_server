package wos.server.dao.work;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import wos.server.entity.work.WorkOrderStaff;

import java.util.List;

/**
 * 工单执行人DAO接口
 * @author Tyr Tao
 * @version 2025-06-11
 */
@MyBatisDao
public interface WorkOrderStaffDao extends CrudDao<WorkOrderStaff> {
	List<WorkOrderStaff> findMyList(WorkOrderStaff workOrderStaff);
}