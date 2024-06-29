package uof0.workflow;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.engine.extitf.IWorkflowPlugin;
import kd.sdk.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作流插件
 */
public class FindStuForSignin implements Plugin, IWorkflowPlugin {

    @Override
    public List<Long> calcUserIds(AgentExecution execution) {
        String businessKey = execution.getBusinessKey();
        List<Long> ids=new ArrayList<>();
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle(businessKey, "uof0_newsignin");
        if(dynamicObject==null) return ids;
        DynamicObject uof0Class = dynamicObject.getDynamicObject("uof0_class");
        DynamicObjectCollection students = uof0Class.getDynamicObjectCollection("uof0_joinstudents");
        for(DynamicObject student:students){
            Long id=student.getDynamicObject("fbasedataid").getLong("id");
            DynamicObject stu=BusinessDataServiceHelper.loadSingle(id,"uof0_studentinfo");
            DynamicObject userfield = stu.getDynamicObject("uof0_userfield");
            ids.add((Long)userfield.getPkValue());
        }

        return ids;
    }
}