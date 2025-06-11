package wos.server.service.mobile;

import com.jeesite.common.entity.Page;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.service.CrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wos.server.dao.mobile.MobileRoleFuncDao;
import wos.server.entity.mobile.MobileRoleFunc;

import java.util.List;

/**
 * 角色功能Service
 * @author Tyr Tao
 * @version 2023-03-07
 */
@Service
@Transactional(readOnly=true)
public class MobileRoleFuncService extends CrudService<MobileRoleFuncDao, MobileRoleFunc> {
	
	/**
	 * 获取单条数据
	 * @param mobileRoleFunc
	 * @return
	 */
	@Override
	public MobileRoleFunc get(MobileRoleFunc mobileRoleFunc) {
		return super.get(mobileRoleFunc);
	}
	
	/**
	 * 查询分页数据
	 * @param mobileRoleFunc 查询条件
	 * @return
	 */
	@Override
	public Page<MobileRoleFunc> findPage(MobileRoleFunc mobileRoleFunc) {
		return super.findPage(mobileRoleFunc);
	}
	
	/**
	 * 查询列表数据
	 * @param mobileRoleFunc
	 * @return
	 */
	@Override
	public List<MobileRoleFunc> findList(MobileRoleFunc mobileRoleFunc) {
		return super.findList(mobileRoleFunc);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param mobileRoleFunc
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(MobileRoleFunc mobileRoleFunc) {
		super.save(mobileRoleFunc);
	}
	
	/**
	 * 更新状态
	 * @param mobileRoleFunc
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(MobileRoleFunc mobileRoleFunc) {
		super.updateStatus(mobileRoleFunc);
	}
	
	/**
	 * 删除数据
	 * @param mobileRoleFunc
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(MobileRoleFunc mobileRoleFunc) {
		dao.phyDelete(mobileRoleFunc);
	}

	/**
	 * 通过角色标识与功能标识获取角色功能
	 * @param roleId
	 * @param funcId
	 * @return
	 */
	public MobileRoleFunc get(String roleId,String funcId) {
		if (StringUtils.isEmpty(roleId) || StringUtils.isEmpty(funcId)) return null;
		MobileRoleFunc mobileRoleFunc=new MobileRoleFunc();
		mobileRoleFunc.setRoleId(roleId);
		mobileRoleFunc.setFuncId(funcId);
		mobileRoleFunc.setStatus("");
		List<MobileRoleFunc> mobileRoleFuncList=findList(mobileRoleFunc);
		if (mobileRoleFuncList==null || mobileRoleFuncList.isEmpty()) return null;
		return mobileRoleFuncList.get(0);
	}

	@Override
	@Transactional(readOnly=false)
	public void deleteByIds(MobileRoleFunc mobileRoleFunc) {
		dao.phyDeleteByEntity(mobileRoleFunc);
	}
	
}