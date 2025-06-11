package wos.server.service;

import com.jeesite.common.config.Global;
import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.mybatis.mapper.query.QueryType;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.file.dao.FileEntityDao;
import com.jeesite.modules.file.dao.FileUploadDao;
import com.jeesite.modules.file.entity.FileEntity;
import com.jeesite.modules.file.entity.FileUpload;
import com.jeesite.modules.file.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class CrudServiceEx<D extends CrudDao<T>, T extends DataEntity<?>> extends CrudService<D,T> {
    @Autowired
    private FileUploadDao fileUploadDao;

    @Autowired
    private FileEntityDao fileEntityDao;

    /**
     * 通过ID与类别删除所有文件
     * @param bizKey
     * @param bizType
     */
    protected void deleteFileUpload(String bizKey, String bizType) {
        if (StringUtils.isEmpty(bizKey) || StringUtils.isEmpty(bizType)) return;
        List<FileUpload> fileUploadList= FileUploadUtils.findFileUpload(bizKey,bizType);
        if (fileUploadList==null || fileUploadList.isEmpty()) return;
        deleteFileUpload(fileUploadList);
    }

    /**
     * 通过ID与类别删除所有失效文件
     * @param bizKey
     * @param bizType
     */
    protected void deleteRemovedFile(String bizKey, String bizType) {
        if (StringUtils.isEmpty(bizKey) || StringUtils.isEmpty(bizType)) return;
        FileUpload fileUpload=new FileUpload();
        fileUpload.setBizKey(bizKey);
        fileUpload.setBizType(bizType);
        fileUpload.setStatus(DataEntity.STATUS_DELETE);
        List<FileUpload> fileUploadList=fileUploadDao.findList(fileUpload);
        deleteFileUpload(fileUploadList);

    }

    /**
     * 删除文件
     * @param fileUploadList
     */
    protected void deleteFileUpload(List<FileUpload> fileUploadList) {
        if (fileUploadList==null || fileUploadList.isEmpty()) return;
        fileUploadList.forEach(item->{
            fileUploadDao.phyDelete(item);
            FileEntity fileEntity=item.getFileEntity();
            fileEntityDao.phyDelete(fileEntity);
            String fileName= Global.getUserfilesBaseDir(item.getFileUrl());
            fileName=formatFileName(fileName);

            File file=new File(fileName);
            if (file.isFile() && file.exists()) {
                try {
                    file.delete();
                } catch (Exception ex) {

                }

            }
        });
    }

    protected String formatFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) return fileName;
        String fileNameLastStr=fileName.substring(fileName.length()-1);
        if (StringUtils.equalsIgnoreCase(fileNameLastStr,"/") || StringUtils.equalsIgnoreCase(fileNameLastStr,"\\")) fileName=fileName.substring(0,fileName.length()-1);
        return fileName;

    }

    /**
     * 通过key列表获取所有上传文件列表
     * @param bizKeyList
     * @param bizType
     * @return
     */
    protected List<FileUpload> getFileUploadList(List<String> bizKeyList,String bizType) {
        if (bizKeyList==null || bizKeyList.isEmpty() || StringUtils.isEmpty(bizType)) return new ArrayList<>();

        FileUpload fileUpload=new FileUpload();
        fileUpload.sqlMap().getWhere().and("biz_key", QueryType.IN,bizKeyList);
        fileUpload.setBizType(bizType);
        fileUpload.setStatus(DataEntity.STATUS_NORMAL);
        return fileUploadDao.findList(fileUpload);
    }

}
