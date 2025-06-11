package wos.server.dao.work;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import wos.server.entity.work.WorkOrder;

/**
 * 工单DAO接口
 * @author Tyr Tao
 * @version 2025-06-11
 */
@MyBatisDao
public interface WorkOrderDao extends CrudDao<WorkOrder> {
	
}