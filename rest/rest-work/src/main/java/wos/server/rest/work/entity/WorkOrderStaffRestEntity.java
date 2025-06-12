package wos.server.rest.work.entity;

import wos.server.entity.work.WorkOrder;
import wos.server.entity.work.WorkOrderStaff;
import wos.server.rest.BasicRestEntity;

import java.util.Date;

public class WorkOrderStaffRestEntity extends BasicRestEntity {
    private String woId;        // 工单标识
    private String staffId;        // 执行人标识
    private String staffName;        // 执行人姓名
    private Integer workProgress;        // 进度

    private String title;
    private String content;
    private Date startTime;
    private Date endTime;
    private String cateCode;        // 类别
    private String cateName;        // 类别
    private String levelCode;        // 级别
    private String levelName;        // 级别

    //#region get/set
    public String getWoId() {
        return woId;
    }

    public void setWoId(String woId) {
        this.woId = woId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Integer getWorkProgress() {
        return workProgress;
    }

    public void setWorkProgress(Integer workProgress) {
        this.workProgress = workProgress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCateCode() {
        return cateCode;
    }

    public void setCateCode(String cateCode) {
        this.cateCode = cateCode;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    //#endregion

    public static WorkOrderStaffRestEntity getInstance(WorkOrderStaff entity) {
        if (entity == null) return null;
        WorkOrderStaffRestEntity instance = new WorkOrderStaffRestEntity();
        instance.setId(entity.getId());
        instance.setWoId(entity.getWoId());
        instance.setStaffId(entity.getStaffId());
        instance.setStaffName(entity.getStaffName());
        instance.setWorkProgress(entity.getWorkProgress());
        instance.setTitle(entity.getTitle());
        instance.setContent(entity.getContent());
        instance.setStartTime(entity.getStartTime());
        instance.setEndTime(entity.getEndTime());
        instance.setCateCode(entity.getCateCode());
        instance.setLevelCode(entity.getLevelCode());
        return instance;
    }

    public static WorkOrderStaffRestEntity getInstance(WorkOrder workOrder, WorkOrderStaff entity) {
        if (entity == null || workOrder == null) return null;
        WorkOrderStaffRestEntity instance = new WorkOrderStaffRestEntity();
        instance.setId(entity.getId());
        instance.setWoId(entity.getWoId());
        instance.setStaffId(entity.getStaffId());
        instance.setStaffName(entity.getStaffName());
        instance.setWorkProgress(entity.getWorkProgress());
        instance.setTitle(workOrder.getTitle());
        instance.setContent(workOrder.getContent());
        instance.setStartTime(workOrder.getStartTime());
        instance.setEndTime(workOrder.getEndTime());
        instance.setCateCode(workOrder.getCateCode());
        instance.setLevelCode(workOrder.getLevelCode());
        return instance;
    }
}
