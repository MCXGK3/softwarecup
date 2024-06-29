package uof0.homework;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.engine.extitf.IWorkflowPlugin;
import kd.imc.bdm.common.constant.BotpCallBackLogConstant;
import kd.sdk.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作流插件
 */
public class HastenHomework implements Plugin, IWorkflowPlugin {
    @Override
    public List<Long> calcUserIds(AgentExecution execution) {
        String businessKey = execution.getBusinessKey();
        List<Long> ids=new ArrayList<>();
        DynamicObject entity = BusinessDataServiceHelper.loadSingle(businessKey, "uof0_tea_homework");
        if(entity==null) return ids;
        DynamicObject clas= entity.getDynamicObject("uof0_basedatafield");
        DynamicObjectCollection students = clas.getDynamicObjectCollection("uof0_joinstudents");
        for(DynamicObject student:students){
            Long id=student.getDynamicObject("fbasedataid").getLong("id");
            DynamicObject stu=BusinessDataServiceHelper.loadSingle(id,"uof0_studentinfo");
            DynamicObject userfield = stu.getDynamicObject("uof0_userfield");
            DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("uof0_st_homework", "billstatus", new QFilter[]{
                    new QFilter("uof0_textfield", QCP.equals, entity.getString("billno")),
                    new QFilter("creator.id", QCP.equals, userfield.getPkValue()),
                    new QFilter("billstatus", QCP.not_equals, "A")
            });
            if(dynamicObject==null){
                ids.add((Long)userfield.getPkValue());
            }

        }
        return ids;
    }
}