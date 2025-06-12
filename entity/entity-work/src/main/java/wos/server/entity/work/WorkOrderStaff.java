package wos.server.entity.work;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;

/**
 * 工单执行人Entity
 * @author Tyr Tao
 * @version 2025-06-11
 */
@Table(name="tb_work_order_staff", alias="a", label="工单执行人信息", columns={
		@Column(name="id", attrName="id", label="编号", isPK=true),
		@Column(name="wo_id", attrName="woId", label="工单标识"),
		@Column(name="staff_id", attrName="staffId", label="执行人"),
		@Column(name="work_progress", attrName="workProgress", label="进度", isUpdateForce=true),
		@Column(includeEntity=DataEntity.class),
	}, orderBy="a.update_date DESC"
)
public class WorkOrderStaff extends DataEntity<WorkOrderStaff> {
	
	private static final long serialVersionUID = 1L;
	private String woId;		// 工单标识
	private String staffId;		// 执行人
	private Long workProgress;		// 进度

	public WorkOrderStaff() {
		this(null);
	}
	
	public WorkOrderStaff(String id){
		super(id);
	}
	
	@NotBlank(message="工单标识不能为空")
	@Size(min=0, max=64, message="工单标识长度不能超过 64 个字符")
	public String getWoId() {
		return woId;
	}

	public void setWoId(String woId) {
		this.woId = woId;
	}
	
	@Size(min=0, max=64, message="执行人长度不能超过 64 个字符")
	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	
	public Long getWorkProgress() {
		return workProgress;
	}

	public void setWorkProgress(Long workProgress) {
		this.workProgress = workProgress;
	}

	private String staffName;

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
}