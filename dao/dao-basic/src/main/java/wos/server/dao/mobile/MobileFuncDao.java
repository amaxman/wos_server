package wos.server.dao.mobile;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import wos.server.entity.mobile.MobileFunc;

/**
 * 移动端功能码DAO接口
 * @author Tyr Tao
 * @version 2023-03-07
 */
@MyBatisDao
public interface MobileFuncDao extends CrudDao<MobileFunc> {
	
}