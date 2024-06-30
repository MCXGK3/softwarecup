package uof0.score;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.List;
import java.util.Map;

/**
 * 标准单据列表插件
 */
public class GetScoresInClass extends AbstractListPlugin implements Plugin {
    private Object getClassPK(){
        return this.getView().getFormShowParameter().getCustomParam("classPK");
    }
    private boolean isTeacher(){
        Map<String, Object> customParams = this.getView().getFormShowParameter().getCustomParams();
        if(!customParams.containsKey("isTeacher")) return false;
        if(customParams.get("isTeacher") instanceof String){
            return ((String) customParams.get("isTeacher")).equals("true");
        }
        return (boolean) customParams.get("isTeacher");
    }

    @Override
    public void setFilter(SetFilterEvent e) {
        super.setFilter(e);
        List<QFilter> qFilters = e.getQFilters();
        if(!isTeacher()){
            qFilters.add(new QFilter("uof0_userfield.id", QCP.equals, RequestContext.get().getCurrUserId()));
        }
        Object classPK = getClassPK();
        if(classPK!=null) {
            DynamicObject course_class = BusinessDataServiceHelper.loadSingle(classPK, "uof0_course_class");
            qFilters.add(new QFilter("uof0_clsname",QCP.equals,course_class.getString("name")));
        }
        return;

    }
}