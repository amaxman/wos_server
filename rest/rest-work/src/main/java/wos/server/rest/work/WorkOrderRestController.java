package wos.server.rest.work;

import com.jeesite.common.entity.Page;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.modules.sys.entity.DictData;
import com.jeesite.modules.sys.utils.DictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wos.server.entity.work.WorkOrder;
import wos.server.rest.BasicRestController;
import wos.server.rest.JsonMsg;
import wos.server.rest.auth.Caches;
import wos.server.rest.auth.entity.UserSessionRestEntity;
import wos.server.rest.work.entity.WorkOrderRestEntity;
import wos.server.service.work.WorkOrderService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "${adminPath}/rest/workOrder")
public class WorkOrderRestController extends BasicRestController {

    @Autowired
    private WorkOrderService workOrderService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/page")
    public JsonMsg<Page<WorkOrderRestEntity>> page(HttpServletRequest request, String sessionId, String keyword, int pageNo, int pageSize) {
        //#region 用户信息
        UserSessionRestEntity userSessionRestEntity = Caches.getUserSession(sessionId);
        if (userSessionRestEntity ==null) return error("请先登陆");
        //#endregion

        //#region 获取数据
        WorkOrder where=new WorkOrder();
        where.setKeyword(keyword);
        Page<WorkOrder> pageWhere=new Page<>(pageNo,pageSize);
        where.setPage(pageWhere);
        pageWhere=workOrderService.findPage(where);
        List<WorkOrder> whereList=pageWhere.getList();
        //#endregion

        //#region 补充数据
        List<DictData> dictDataListCate=DictUtils.getDictList("work_order_cate"),dictDataListLevel=DictUtils.getDictList("work_order_level");
        //#endregion

        //#region 返回数据
        Page<WorkOrderRestEntity> page=new Page<>(pageNo,pageSize);
        page.setCount(pageWhere.getCount());
        List<WorkOrderRestEntity> list=whereList.stream().map(p->WorkOrderRestEntity.getInstance(p)).collect(Collectors.toList());
        list.stream().forEach(item->{
            String cateCode=item.getCateCode(),levelCode=item.getLevelCode();
            if (StringUtils.isNotEmpty(cateCode)) {
                DictData dictDataCate=dictDataListCate.stream().filter(p->StringUtils.equalsIgnoreCase(p.getDictValue(),cateCode)).findFirst().orElse(null);
                if (dictDataCate!=null) item.setCateName(dictDataCate.getDictLabel());
            }

            if (StringUtils.isNotEmpty(levelCode)) {
                DictData dictDataCode=dictDataListLevel.stream().filter(p->StringUtils.equalsIgnoreCase(p.getDictValue(),levelCode)).findFirst().orElse(null);
                if (dictDataCode!=null) item.setLevelName(dictDataCode.getDictLabel());
            }
        });
        page.setList(list);
        return success(page,"分页获取工单成功");
        //#endregion
    }
}
