package wos.server.dao.auth;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import wos.server.entity.auth.UserSession;

/**
 * 用户登录令牌DAO接口
 * @author Tyr Tao
 * @version 2022-05-15
 */
@MyBatisDao
public interface UserSessionDao extends CrudDao<UserSession> {
	public void deleteBySessionId(UserSession userSession);
}