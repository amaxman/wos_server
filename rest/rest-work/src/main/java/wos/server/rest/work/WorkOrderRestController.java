package wos.server.rest.work;

import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.lang.DateUtils;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.mybatis.mapper.query.QueryType;
import com.jeesite.modules.file.utils.FileUploadUtils;
import com.jeesite.modules.sys.entity.DictData;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.modules.sys.utils.DictUtils;
import com.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import wos.server.entity.work.WorkOrder;
import wos.server.entity.work.WorkOrderStaff;
import wos.server.rest.BasicRestController;
import wos.server.rest.FileUploadRest;
import wos.server.rest.JsonMsg;
import wos.server.rest.auth.Caches;
import wos.server.rest.auth.entity.UserSessionRestEntity;
import wos.server.rest.work.entity.WorkOrderRestEntity;
import wos.server.rest.work.entity.WorkOrderStaffRestEntity;
import wos.server.service.work.WorkOrderService;
import wos.server.service.work.WorkOrderStaffService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "${adminPath}/rest/workOrder")
public class WorkOrderRestController extends BasicRestController {

    //#region 工单

    @Autowired
    private WorkOrderService workOrderService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/page")
    public JsonMsg<Page<WorkOrderRestEntity>> page(HttpServletRequest request, String sessionId, String keyword, int pageNo, int pageSize) {
        //#region 用户信息
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId);
        if (userSessionRestEntity == null) return error("请先登陆");
        //#endregion

        //#region 获取数据
        WorkOrder where = new WorkOrder();
        where.setKeyword(keyword);
        Page<WorkOrder> pageWhere = new Page<>(pageNo, pageSize);
        where.setPage(pageWhere);
        pageWhere = workOrderService.findPage(where);
        List<WorkOrder> whereList = pageWhere.getList();
        //#endregion

        //#region 补充数据
        List<DictData> dictDataListCate = DictUtils.getDictList("work_order_cate"), dictDataListLevel = DictUtils.getDictList("work_order_level");
        //#endregion

        //#region 返回数据
        Page<WorkOrderRestEntity> page = new Page<>(pageNo, pageSize);
        page.setCount(pageWhere.getCount());
        List<WorkOrderRestEntity> list = whereList.stream().map(p -> WorkOrderRestEntity.getInstance(p)).collect(Collectors.toList());
        list.stream().forEach(item -> {
            String cateCode = item.getCateCode(), levelCode = item.getLevelCode();
            if (StringUtils.isNotEmpty(cateCode)) {
                DictData dictDataCate = dictDataListCate.stream().filter(p -> StringUtils.equalsIgnoreCase(p.getDictValue(), cateCode)).findFirst().orElse(null);
                if (dictDataCate != null) item.setCateName(dictDataCate.getDictLabel());
            }

            if (StringUtils.isNotEmpty(levelCode)) {
                DictData dictDataCode = dictDataListLevel.stream().filter(p -> StringUtils.equalsIgnoreCase(p.getDictValue(), levelCode)).findFirst().orElse(null);
                if (dictDataCode != null) item.setLevelName(dictDataCode.getDictLabel());
            }
        });
        page.setList(list);
        return success(page, "分页获取工单成功");
        //#endregion
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/delete")
    public JsonMsg<Void> delete(HttpServletRequest request, String sessionId, String id) {
        //#region 用户信息
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId);
        if (userSessionRestEntity == null) return error("请先登陆");
        //#endregion

        //#region 获取数据
        if (StringUtils.isEmpty(id)) {
            return error("删除的工单标识不允许为空");
        }
        WorkOrder where = workOrderService.get(id);
        if (where == null) return success("工单不存在，可能已经被删除");
        //#endregion

        workOrderService.delete(where);
        return success("工单已被删除");
    }

    @RequestMapping(method = {RequestMethod.POST}, path = "/save/{sessionId}")
    public JsonMsg<String> save(HttpServletRequest request, @PathVariable("sessionId") String sessionId, @RequestBody WorkOrderRestEntity entity) {
        if (entity == null) return error("工单信息不允许为空");
        //#region 用户信息
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId);
        if (userSessionRestEntity == null) return error("请先登陆");
        //#endregion

        //#region 获取数据
        WorkOrder workOrder = null;
        if (StringUtils.isNotEmpty(entity.getId())) {
            workOrder = workOrderService.get(entity.getId());
        }
        if (workOrder == null) {
            workOrder = new WorkOrder();
            workOrder.setIsNewRecord(true);
            workOrder.setCreateBy(userSessionRestEntity.getUserCode());
            workOrder.setCreateDate(new Date());
        } else {
            workOrder.setIsNewRecord(false);
        }
        workOrder.setTitle(entity.getTitle());
        workOrder.setContent(entity.getContent());
        workOrder.setStartTime(entity.getStartTime());
        workOrder.setEndTime(entity.getEndTime());
        workOrder.setCateCode(entity.getCateCode());
        workOrder.setLevelCode(entity.getLevelCode());
        workOrder.setUpdateBy(userSessionRestEntity.getUserCode());
        workOrder.setUpdateDate(new Date());
        //#endregion

        workOrderService.save(workOrder);
        return success(workOrder.getId(), "保存工单成功");
    }

    @RequestMapping(method = {RequestMethod.POST}, path = "/uploadImages/{sessionId}/{id}")
    public JsonMsg<Void> uploadImages(@PathVariable("sessionId") String sessionId, @PathVariable("id") String id, HttpServletRequest request) {
        //#region 用户信息
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId);
        if (userSessionRestEntity == null) return error("请先登陆");
        //#endregion

        //#region 获取数据
        WorkOrder workOrder = workOrderService.get(id);
        if (workOrder == null) return error("工单不存在，可能已经被删除");
        //#endregion

        //#region 获取上传的文件路径
        if (StringUtils.isEmpty(FileUploadRest.cachePath)) {
            String dirPath = "cache" + File.separator + "images";
            FileUploadRest.cachePath = Global.getUserfilesBaseDir(dirPath);
        }
        File fileDir = new File(FileUploadRest.cachePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        //#endregion

        //#region 上传文件
        try {
            //#region 获取上传的文件信息
            StandardMultipartHttpServletRequest standardMultipartHttpServletRequest = (StandardMultipartHttpServletRequest) request;
            Map<String, MultipartFile> multipartFileMap = standardMultipartHttpServletRequest.getFileMap();
            MultipartFile multipartFile = multipartFileMap.get("media");
            String fileName = multipartFile.getOriginalFilename();
            //#endregion

            //#region 检查文件是否存在文件，如果存在，则删除
            File fileDest = new File(FileUploadRest.cachePath + File.separator + fileName);
            if (fileDest.exists()) {
                fileDest.delete();
            }
            //#endregion

            //#region 移动文件至目标目录
            multipartFile.transferTo(fileDest);
            //#endregion

            //#region 保存文件
            //FileUploadUtils.saveFileUpload(mobileUser, mobileUser.getId(), bizType);
            Map<String, Object> hm = FileUploadUtils.saveFileUpload(fileDest, fileName, id, WorkOrderService.bizType, "image");
            String result = String.valueOf(hm.get("result"));
            String message = String.valueOf(hm.get("message"));
            //#endregion

            //#region 返回数据
            if (StringUtils.equalsIgnoreCase(result, "true")) {
                return success(message);
            } else {
                return error(message);
            }
            //#endregion
        } catch (Exception ex) {
            ex.printStackTrace();
            return error("上传文件失败");
        }
        //#endregion
    }

    /**
     * 保存工单员工
     * @param request
     * @param sessionId
     * @param entity
     * @return
     */
    @RequestMapping(method = {RequestMethod.POST}, path = "/saveStaff/{sessionId}")
    public JsonMsg<String> saveStaff(HttpServletRequest request, @PathVariable("sessionId") String sessionId, @RequestBody WorkOrderStaffRestEntity entity) {
        if (entity == null) return error("员工信息不允许为空");
        //#region 用户信息
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId);
        if (userSessionRestEntity == null) return error("请先登陆");
        //#endregion
        //#region 获取工单信息
        String woId=entity.getWoId();
        if (StringUtils.isEmpty(woId)) return error("工单标识不允许为空");
        WorkOrder workOrder=workOrderService.get(woId);
        if (workOrder==null) return error("工单不存在，可能已经被删除");
        //#endregion

        //#region 员工信息
        String staffId=entity.getStaffId();
        if (StringUtils.isEmpty(staffId)) return error("员工信息不允许为空");
        User user= UserUtils.get(staffId);
        if (user==null) return error("员工信息不存在");
        //#endregion
        //#region 员工是否被添加
        if (!workOrderStaffService.allow2Saved(entity.getId(),woId,staffId)) return error("请勿重复添加");
        //#endregion

        //#region 进度校验
        String cateCode=workOrder.getCateCode();
        if (StringUtils.isNotEmpty(cateCode)
                && entity.getWorkProgress()!=null
                && entity.getWorkProgress()!=100
                && entity.getWorkProgress()!=0
                && StringUtils.equalsIgnoreCase(cateCode,"yes_no")) {
            return error("工单类型仅允许输入0或100");
        }
        //#endregion

        //#region 数据转换
        WorkOrderStaff workOrderStaff;
        if (StringUtils.isNotEmpty(entity.getId())) {
            workOrderStaff=workOrderStaffService.get(entity.getId());
        } else {
            workOrderStaff=new WorkOrderStaff();
            workOrderStaff.setIsNewRecord(true);
            workOrderStaff.setWoId(entity.getWoId());
        }
        if (workOrder==null) {
            workOrderStaff=new WorkOrderStaff();
            workOrderStaff.setWoId(entity.getWoId());
            workOrderStaff.setIsNewRecord(true);
            workOrderStaff.setCreateBy(userSessionRestEntity.getUserCode());
            workOrderStaff.setCreateDate(new Date());
            workOrderStaff.setStaffId(staffId);
        }
        workOrderStaff.setStaffId(entity.getStaffId());
        workOrderStaff.setWorkProgress(entity.getWorkProgress());
        workOrderStaffService.save(workOrderStaff);
        //#endregion
        return success(workOrder.getId(),"保存成功");
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/deleteStaff")
    public JsonMsg<Void> deleteStaff(HttpServletRequest request, String sessionId, String id) {
        //#region 用户信息
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId);
        if (userSessionRestEntity == null) return error("请先登陆");
        //#endregion

        //#region 获取数据
        if (StringUtils.isEmpty(id)) {
            return error("删除标识不允许为空");
        }
        WorkOrderStaff where = workOrderStaffService.get(id);
        if (where == null) return success("员工不存在，可能已经被移出工单");
        //#endregion

        workOrderStaffService.delete(where);
        return success("移除员工成功");
    }
    //#endregion

    //#region 工单执行人
    @Autowired
    private WorkOrderStaffService workOrderStaffService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/staffList")
    public JsonMsg<List<WorkOrderStaffRestEntity>> staffList(HttpServletRequest request, String sessionId, String woId) {
        //#region 用户信息
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId);
        if (userSessionRestEntity == null) return error("请先登陆");
        //#endregion

        //#region 获取数据
        if (StringUtils.isEmpty(woId)) return error("工单标识不允许为空");
        WorkOrder workOrder = workOrderService.get(woId);
        if (workOrder==null) return error("工单不存在，可能已经被删除");
        //#endregion

        //#region 返回数据
        List<WorkOrderStaff> workOrderStaffList=workOrderStaffService.getList(woId);
        getUserName(workOrderStaffList);
        List<WorkOrderStaffRestEntity> workOrderStaffRestEntityList=workOrderStaffList.stream()
                .map(p->WorkOrderStaffRestEntity.getInstance(workOrder,p))
                .collect(Collectors.toList());
        //#endregion

        //#region 补充数据
        List<DictData> dictDataListCate = DictUtils.getDictList("work_order_cate"), dictDataListLevel = DictUtils.getDictList("work_order_level");
        workOrderStaffRestEntityList.stream().forEach(item -> {
            String cateCode = item.getCateCode(), levelCode = item.getLevelCode();
            if (StringUtils.isNotEmpty(cateCode)) {
                DictData dictDataCate = dictDataListCate.stream().filter(p -> StringUtils.equalsIgnoreCase(p.getDictValue(), cateCode)).findFirst().orElse(null);
                if (dictDataCate != null) item.setCateName(dictDataCate.getDictLabel());
            }

            if (StringUtils.isNotEmpty(levelCode)) {
                DictData dictDataCode = dictDataListLevel.stream().filter(p -> StringUtils.equalsIgnoreCase(p.getDictValue(), levelCode)).findFirst().orElse(null);
                if (dictDataCode != null) item.setLevelName(dictDataCode.getDictLabel());
            }
        });
        //#endregion

        return success(workOrderStaffRestEntityList,"根据工单获取执行人成功");
    }

    private void getUserName(List<WorkOrderStaff> workOrderStaffList) {
        if (workOrderStaffList==null || workOrderStaffList.isEmpty()) return;

        List<String> userIdList=workOrderStaffList.stream().filter(p->StringUtils.isNotEmpty(p.getStaffId())).map(p->p.getStaffId()).distinct().collect(Collectors.toList());
        if (!userIdList.isEmpty()) {
            User userWhere=new User();
            userWhere.sqlMap().getWhere().and("user_code", QueryType.IN,userIdList);
            List<User> userList=userService.findList(userWhere);
            if (!userList.isEmpty()) {
                workOrderStaffList.stream().filter(p->StringUtils.isNotEmpty(p.getStaffId())).forEach(item->{
                    String staffId=item.getStaffId();
                    User user=userList.stream().filter(p->StringUtils.equalsIgnoreCase(p.getUserCode(),staffId)).findFirst().orElse(null);
                    if (user!=null) {
                        item.setStaffName(user.getUserName());
                    }
                });
            }
        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/staffPage")
    public JsonMsg<Page<WorkOrderStaffRestEntity>> staffPage(HttpServletRequest request, String sessionId, String keyword,String title,String content,String startTime,String endTime,String workStatus, int pageNo, int pageSize) {
        //#region 用户信息
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId);
        if (userSessionRestEntity == null) return error("请先登陆");
        //#endregion

        //#region 分页
        Page<WorkOrderStaffRestEntity> page=new Page<>(pageNo,pageSize);
        //#endregion

        //#region 获取数据
        WorkOrderStaff where=new WorkOrderStaff();
        where.setStaffId(userSessionRestEntity.getUserCode());
        if (StringUtils.isNotEmpty(keyword)) where.setKeyword(keyword);
        if (StringUtils.isNotEmpty(title)) where.setTitle(title);
        if (StringUtils.isNotEmpty(content)) where.setContent(content);
        if (StringUtils.isNotEmpty(startTime)) {
            try {
                Date _startTime= DateUtils.parseDate(startTime,"yyyy-MM-dd HH:mm:ss");
                where.setStartTime(_startTime);
            } catch (Exception ex) {

            }
        }

        if (StringUtils.isNotEmpty(endTime)) {
            try {
                Date _endTime= DateUtils.parseDate(endTime,"yyyy-MM-dd HH:mm:ss");
                where.setEndTime(_endTime);
            } catch (Exception ex) {

            }
        }

        if (StringUtils.isNotEmpty(workStatus)) {
            if (StringUtils.equalsIgnoreCase(workStatus,"complete")) {
                where.sqlMap().getWhere().and("work_progress", QueryType.EQ,100);
            } else {
                where.sqlMap().getWhere().and("work_progress", QueryType.NE,100);
            }
        }

        Page<WorkOrderStaff> pageWhere=new Page<>(pageNo,pageSize);
        where.setPage(pageWhere);
        pageWhere = workOrderStaffService.findMyPage(where);
        List<WorkOrderStaff> workOrderStaffList=pageWhere.getList();
        //#endregion

        //#region 返回数据
        page.setCount(pageWhere.getCount());
        List<WorkOrderStaffRestEntity> workOrderStaffRestEntityList=workOrderStaffList.stream().map(p->WorkOrderStaffRestEntity.getInstance(p)).collect(Collectors.toList());
        page.setList(workOrderStaffRestEntityList);
        //#endregion

        //#region 补充数据
        List<DictData> dictDataListCate = DictUtils.getDictList("work_order_cate"), dictDataListLevel = DictUtils.getDictList("work_order_level");

        workOrderStaffRestEntityList.stream().forEach(item -> {
            String cateCode = item.getCateCode(), levelCode = item.getLevelCode();
            if (StringUtils.isNotEmpty(cateCode)) {
                DictData dictDataCate = dictDataListCate.stream().filter(p -> StringUtils.equalsIgnoreCase(p.getDictValue(), cateCode)).findFirst().orElse(null);
                if (dictDataCate != null) item.setCateName(dictDataCate.getDictLabel());
            }

            if (StringUtils.isNotEmpty(levelCode)) {
                DictData dictDataCode = dictDataListLevel.stream().filter(p -> StringUtils.equalsIgnoreCase(p.getDictValue(), levelCode)).findFirst().orElse(null);
                if (dictDataCode != null) item.setLevelName(dictDataCode.getDictLabel());
            }
        });
        //#endregion


        return success(page,"分页获取我的工单成功");

    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/staffSave")
    public JsonMsg<Void> staffSave(HttpServletRequest request, String sessionId,String id,int progress) {
        //#region 用户信息
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId);
        if (userSessionRestEntity == null) return error("请先登陆");
        //#endregion

        if (StringUtils.isEmpty(id)) return error("系统标识不允许为空");
        if (progress<0 || progress>100) return error("进度范围必须位于0-100之间");

        //#region 获取数据
        WorkOrderStaff workOrderStaff=workOrderStaffService.get(id);
        if (workOrderStaff==null) return error("数据不存在，可能工单已经被删除或者您已经被移出工单");
        if (!StringUtils.equalsIgnoreCase(workOrderStaff.getStaffId(),userSessionRestEntity.getUserCode())) return error("无权限");

        if (progress>0 && progress<100) {
            if (StringUtils.isEmpty(workOrderStaff.getWoId())) return error("系统错误，工单标识不允许为空");
            WorkOrder workOrder=workOrderService.get(workOrderStaff.getWoId());
            if (workOrder==null) return error("工单不存在，可能已经被山翠");
            String cateCode=workOrder.getCateCode();
            if (StringUtils.equalsIgnoreCase(cateCode,"yes_no")) return error("仅允许输入0或100");
        }
        workOrderStaff.setWorkProgress(progress);
        workOrderStaff.setUpdateBy(userSessionRestEntity.getUserCode());
        workOrderStaff.setUpdateDate(new Date());
        workOrderStaffService.update(workOrderStaff);
        //#endregion

        return success("保存成功");


    }
    //#endregion
}
