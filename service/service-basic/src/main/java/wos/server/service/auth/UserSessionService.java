package wos.server.service.auth;

import com.jeesite.common.entity.Page;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wos.server.dao.auth.UserSessionDao;
import wos.server.entity.auth.UserSession;

import java.util.List;

/**
 * 用户登录令牌Service
 * @author Tyr Tao
 * @version 2022-05-15
 */
@Service
@Transactional(readOnly=true)
public class UserSessionService extends CrudService<UserSessionDao, UserSession> {

	@Autowired
	private UserSessionDao userSessionDao;
	
	/**
	 * 获取单条数据
	 * @param userSession
	 * @return
	 */
	@Override
	public UserSession get(UserSession userSession) {
		return super.get(userSession);
	}
	
	/**
	 * 查询分页数据
	 * @param userSession 查询条件
	 * @return
	 */
	@Override
	public Page<UserSession> findPage(UserSession userSession) {
		return super.findPage(userSession);
	}
	
	/**
	 * 查询列表数据
	 * @param userSession
	 * @return
	 */
	@Override
	public List<UserSession> findList(UserSession userSession) {
		return super.findList(userSession);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param userSession
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(UserSession userSession) {
		super.save(userSession);
	}
	
	/**
	 * 更新状态
	 * @param userSession
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(UserSession userSession) {
		super.updateStatus(userSession);
	}
	
	/**
	 * 删除数据
	 * @param userSession
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(UserSession userSession) {
		dao.phyDelete(userSession);
	}

	/**
	 * 通过令牌获取实体
	 * @param sessionId
	 * @return
	 */
	public UserSession getBySessionId(String sessionId) {
		if (StringUtils.isEmpty(sessionId)) return null;
		UserSession userSession=new UserSession();
		userSession.setSessionId(sessionId);

		List<UserSession> userSessionList=findList(userSession);
		return userSessionList==null || userSessionList.isEmpty()?null:userSessionList.get(0);
	}

	/**
	 * 通过令牌删除实体
	 * @param sessionId
	 * @return
	 */
	@Transactional(readOnly=false)
	public boolean deleteBySessionId(String sessionId) {
		if (StringUtils.isEmpty(sessionId)) return false;
		UserSession userSession=new UserSession();
		userSession.setSessionId(sessionId);
		userSessionDao.deleteBySessionId(userSession);
		return true;

	}

	@Transactional(readOnly=false)
	public void phyDeleteByEntity(UserSession userSession) {
		dao.phyDeleteByEntity(userSession);
	}

	/**
	 * 通过企业标识与登陆ID获取用户登陆授权
	 * @param compId
	 * @param userId
	 * @return
	 */
	public UserSession get(String compId,String userId) {
		UserSession where=new UserSession();
		where.setLoginId(userId);
		List<UserSession> list=dao.findList(where);
		return list==null || list.isEmpty()?null:list.get(0);
	}
	
}