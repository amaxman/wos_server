package wos.server.entity.mobile;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;
import com.jeesite.common.mybatis.mapper.query.QueryType;

import javax.validation.constraints.Size;

/**
 * 移动端功能码Entity
 * @author Tyr Tao
 * @version 2023-03-07
 */
@Table(name="tb_mobile_func", alias="a", label="移动端功能码信息", columns={
		@Column(name="id", attrName="id", label="id", isPK=true),
		@Column(name="order_num", attrName="orderNum", label="功能码"),
		@Column(name="func_title", attrName="funcTitle", label="标题", queryType=QueryType.LIKE),
		@Column(name="func_code", attrName="funcCode", label="功能码"),
		@Column(name="func_cate", attrName="funcCate", label="功能类别"),
		@Column(includeEntity= DataEntity.class),
	}, orderBy="a.func_cate,a.order_num"
)
public class MobileFunc extends DataEntity<MobileFunc> {
	
	private static final long serialVersionUID = 1L;
	private Integer orderNum;		// 排序号
	private String funcTitle;		// 标题
	private String funcCode;		// 功能码

	public MobileFunc() {
		this(null);
	}
	
	public MobileFunc(String id){
		super(id);
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	@Size(min=0, max=20, message="标题长度不能超过 20 个字符")
	public String getFuncTitle() {
		return funcTitle;
	}

	public void setFuncTitle(String funcTitle) {
		this.funcTitle = funcTitle;
	}
	
	@Size(min=0, max=64, message="功能码长度不能超过 64 个字符")
	public String getFuncCode() {
		return funcCode;
	}

	public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}

	/**
	 * 图片路径
	 */
	private String imagePath;

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}