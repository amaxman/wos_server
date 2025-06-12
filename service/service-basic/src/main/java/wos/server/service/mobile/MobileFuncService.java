package wos.server.service.mobile;

import com.jeesite.common.entity.Page;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.mybatis.mapper.query.QueryType;
import com.jeesite.common.service.CrudServiceEx;
import com.jeesite.modules.file.entity.FileEntity;
import com.jeesite.modules.file.entity.FileUpload;
import com.jeesite.modules.file.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wos.server.dao.mobile.MobileFuncDao;
import wos.server.dao.mobile.MobileRoleFuncDao;
import wos.server.entity.mobile.MobileFunc;
import wos.server.entity.mobile.MobileRoleFunc;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 移动端功能码Service
 * @author Tyr Tao
 * @version 2023-03-07
 */
@Service
@Transactional(readOnly=true)
public class MobileFuncService extends CrudServiceEx<MobileFuncDao, MobileFunc> {

	@Autowired
	private MobileRoleFuncDao mobileRoleFuncDao;
	
	/**
	 * 获取单条数据
	 * @param mobileFunc
	 * @return
	 */
	@Override
	public MobileFunc get(MobileFunc mobileFunc) {
		return super.get(mobileFunc);
	}
	
	/**
	 * 查询分页数据
	 * @param mobileFunc 查询条件
	 * @return
	 */
	@Override
	public Page<MobileFunc> findPage(MobileFunc mobileFunc) {
		return super.findPage(mobileFunc);
	}
	
	/**
	 * 查询列表数据
	 * @param mobileFunc
	 * @return
	 */
	@Override
	public List<MobileFunc> findList(MobileFunc mobileFunc) {
		return super.findList(mobileFunc);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param mobileFunc
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(MobileFunc mobileFunc) {
		super.save(mobileFunc);
		// 保存上传图片
		FileUploadUtils.saveFileUpload(mobileFunc, mobileFunc.getId(), "mobileFunc_image");
		deleteRemovedFile(mobileFunc.getId(), "mobileFunc_image");	//删除已经删除的文件
	}
	
	/**
	 * 更新状态
	 * @param mobileFunc
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(MobileFunc mobileFunc) {
		super.updateStatus(mobileFunc);
	}
	
	/**
	 * 删除数据
	 * @param mobileFunc
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(MobileFunc mobileFunc) {
		List<FileUpload> fileUploadList=FileUploadUtils.findFileUpload(mobileFunc.getId(),"mobileFunc_image");
		deleteFileUpload(fileUploadList);

		//#region 删除功能角色对应关系
		if (StringUtils.isNotEmpty(mobileFunc.getId())) {
			MobileRoleFunc mobileRoleFuncDelete=new MobileRoleFunc();
			mobileRoleFuncDelete.setFuncId(mobileFunc.getId());
			mobileRoleFuncDao.phyDeleteByEntity(mobileRoleFuncDelete);
		}
		//#endregion

		dao.phyDelete(mobileFunc);
	}

	@Override
	@Transactional
	public void deleteByIds(MobileFunc mobileFunc) {
		//#region 获取所有将要被删除的数据
		List<MobileFunc> mobileFuncList=dao.findList(mobileFunc);
		if (mobileFuncList!=null && !mobileFuncList.isEmpty()) {
			mobileFuncList.forEach(item->{
				List<FileUpload> fileUploadList=FileUploadUtils.findFileUpload(item.getId(),"mobileFunc_image");
				deleteFileUpload(fileUploadList);
			});
		}
		//#endregion

		//#region 删除功能角色对应关系
		List<String> funcIdList=mobileFuncList.stream().map(p->p.getId()).collect(Collectors.toList());
		if (!funcIdList.isEmpty()) {
			MobileRoleFunc mobileRoleFuncDelete=new MobileRoleFunc();
			mobileRoleFuncDelete.sqlMap().getWhere().and("func_id",QueryType.IN,funcIdList);
			mobileRoleFuncDao.phyDeleteByEntity(mobileRoleFuncDelete);
		}
		//#endregion

		dao.phyDeleteByEntity(mobileFunc);
	}

	/**
	 * 获取下一个功能码序号
	 * @return
	 */
	public int getNextOrderNo() {
		MobileFunc where=new MobileFunc();
		Page<MobileFunc> page=new Page<>(1,1);
		page.setOrderBy("a.order_num desc");
		where.setPage(page);
		List<MobileFunc> list=dao.findList(where);
		if (list==null || list.isEmpty()) return 1;
		MobileFunc mobileFunc=list.get(0);
		if (mobileFunc.getOrderNum()==null || mobileFunc.getOrderNum()<=0) return 1;
		return mobileFunc.getOrderNum().intValue()+1;

	}

	/**
	 * 检查功能码是否合法
	 * @param mobileFunc
	 * @return
	 */
	public boolean checkFunCode(MobileFunc mobileFunc) {
		if (mobileFunc==null) return false;
		if (StringUtils.isEmpty(mobileFunc.getFuncCode())) return false;
		MobileFunc where=new MobileFunc();
		Page<MobileFunc> page=new Page<>(1,1);
		where.setPage(page);
		where.setFuncCode(mobileFunc.getFuncCode());
		if (StringUtils.isNotEmpty(mobileFunc.getId())) where.sqlMap().getWhere().and("id", QueryType.NE,mobileFunc.getId());
		List<MobileFunc> list=dao.findList(where);
		if (list==null) return false;
		return list.isEmpty();

	}

	/**
	 * 根据角色列表获取功能列表
	 * @param roleCodeList
	 * @return
	 */
	public List<MobileFunc> getListByRoleCodeList(List<String> roleCodeList) {
		//#region 参数校验
		if (roleCodeList==null || roleCodeList.isEmpty()) return new ArrayList<>();
		//#endregion

		//#region 获取功能角色
		MobileRoleFunc mobileRoleFunc=new MobileRoleFunc();
		mobileRoleFunc.sqlMap().getWhere().and("role_id",QueryType.IN,roleCodeList);
		List<MobileRoleFunc> mobileRoleFuncList=mobileRoleFuncDao.findList(mobileRoleFunc);
		if (mobileRoleFuncList==null || mobileRoleFuncList.isEmpty()) return new ArrayList<>();
		//#endregion

		//#region 获取功能列表
		List<String> funcIdList=mobileRoleFuncList.stream().map(p->p.getFuncId()).distinct().collect(Collectors.toList());
		MobileFunc where=new MobileFunc();
		where.sqlMap().getWhere().and("id",QueryType.IN,funcIdList);
		return dao.findList(where);
		//#endregion
	}


	/**
	 * 附加图片信息
	 * @param list
	 */
	public void attacheImage(List<MobileFunc> list) {
		if (list==null ||list.isEmpty()) return;
		List<String> idList=list.stream().map(p->p.getId()).collect(Collectors.toList());
		List<FileUpload> fileUploadList=getFileUploadList(idList,"mobileFunc_image");
		if (fileUploadList==null || fileUploadList.isEmpty()) return;
		list.forEach(item->{
			String bizKey=item.getId();
			FileUpload fileUploadFirst=fileUploadList.stream().filter(p->StringUtils.equalsIgnoreCase(bizKey,p.getBizKey())).findFirst().orElse(null);
			if (fileUploadFirst==null) return;
			FileEntity fileEntity=fileUploadFirst.getFileEntity();
			if (fileEntity==null) return;
			item.setImagePath(fileEntity.getFileUrl());
		});

	}
}