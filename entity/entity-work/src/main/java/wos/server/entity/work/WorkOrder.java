package wos.server.entity.work;

import javax.validation.constraints.Size;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.lang.DateUtils;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;
import com.jeesite.common.mybatis.mapper.query.QueryType;

/**
 * 工单Entity
 * @author Tyr Tao
 * @version 2025-06-11
 */
@Table(name="tb_work_order", alias="a", label="工单信息", columns={
		@Column(name="id", attrName="id", label="编号", isPK=true),
		@Column(name="title", attrName="title", label="标题", queryType=QueryType.LIKE),
		@Column(name="content", attrName="content", label="内容", queryType=QueryType.LIKE),
		@Column(name="start_time", attrName="startTime", label="开始时间", isUpdateForce=true),
		@Column(name="end_time", attrName="endTime", label="结束时间", isUpdateForce=true),
		@Column(name="cate_code", attrName="cateCode", label="类别"),
		@Column(name="level_code", attrName="levelCode", label="级别"),
		@Column(includeEntity=DataEntity.class),
	}, orderBy="a.update_date DESC"
)
public class WorkOrder extends DataEntity<WorkOrder> {
	
	private static final long serialVersionUID = 1L;
	private String title;		// 标题
	private String content;		// 内容
	private Date startTime;		// 开始时间
	private Date endTime;		// 结束时间
	private String cateCode;		// 类别
	private String levelCode;		// 级别

	public WorkOrder() {
		this(null);
	}
	
	public WorkOrder(String id){
		super(id);
	}
	
	@Size(min=0, max=50, message="标题长度不能超过 50 个字符")
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
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	@Size(min=0, max=20, message="类别长度不能超过 20 个字符")
	public String getCateCode() {
		return cateCode;
	}

	public void setCateCode(String cateCode) {
		this.cateCode = cateCode;
	}
	
	@Size(min=0, max=20, message="级别长度不能超过 20 个字符")
	public String getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}

	//#region 查询

	public Date getStartTime_gte() {
		return sqlMap.getWhere().getValue("start_time", QueryType.GTE);
	}

	public void setStartTime_gte(Date startTime) {
		sqlMap.getWhere().and("start_time", QueryType.GTE, DateUtils.getOfDayFirst(startTime));
	}

	public Date geStartTime_lte() {
		return sqlMap.getWhere().getValue("start_time", QueryType.LTE);
	}

	public void setStartTime_lte(Date startTime) {
		sqlMap.getWhere().and("start_time", QueryType.LTE, DateUtils.getOfDayLast(startTime));
	}

	//#endregion
	
}