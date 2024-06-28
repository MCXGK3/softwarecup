package uof0.workflow;

import kd.bos.ais.core.searcher.IThirdPartSearcher;
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
public class FindAllStu implements Plugin, IWorkflowPlugin {
    @Override
    public List<Long> calcUserIds(AgentExecution execution, DynamicObject pme, DynamicObject businessModel) {
        String businessKey = execution.getBusinessKey();
        DynamicObject entity= BusinessDataServiceHelper.loadSingle(businessKey,"uof0_tea_homework");
        DynamicObject clas= entity.getDynamicObject("uof0_basedatafield");
        DynamicObjectCollection students = clas.getDynamicObjectCollection("uof0_joinstudents");
        List<Long> ids=new ArrayList<>();
        for(DynamicObject student:students){
            ids.add(student.getDynamicObject("fbasedataid").getLong("uof0_userfield.id"));
        }
        String name = execution.getEventName();
        return ids;
    }

    @Override
    public List<Long> calcUserIds(AgentExecution execution) {
        String businessKey = execution.getBusinessKey();
        DynamicObject entity= BusinessDataServiceHelper.loadSingle(businessKey,"uof0_tea_homework");
        DynamicObject clas= entity.getDynamicObject("uof0_basedatafield");
        DynamicObjectCollection students = clas.getDynamicObjectCollection("uof0_joinstudents");
        List<Long> ids=new ArrayList<>();
        for(DynamicObject student:students){
            ids.add(student.getDynamicObject("fbasedataid").getLong("id"));
        }
        String name = execution.getEventName();
        return ids;
    }
}