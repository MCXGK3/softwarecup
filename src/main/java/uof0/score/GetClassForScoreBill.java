package uof0.score;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.entity.property.BasedataProp;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 单据界面插件
 */
public class GetClassForScoreBill extends AbstractBillPlugIn implements Plugin {
    private Object getClassPK(){
        return this.getView().getFormShowParameter().getCustomParam("classPK");
    }


    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        Object classPK = getClassPK();
        if(classPK==null) return;
        DynamicObject courseClass = BusinessDataServiceHelper.loadSingle(classPK, "uof0_course_class");
        this.getModel().setValue("uof0_classinfo",courseClass);
        return;

    }

    @Override
    public void propertyChanged(PropertyChangedArgs e) {
        //调整逻辑使获取成绩成为可能
        super.propertyChanged(e);
        if (! (e.getProperty() instanceof BasedataProp)) return;
        if(this.getView().getPageCache().get("push")!=null) {
            if (this.getView().getPageCache().get("push").equals("true")) return;
        }
        if(((String)this.getModel().getValue("uof0_textfield")).isEmpty()) return;
        String examno = (String)this.getModel().getValue("uof0_textfield");
        DynamicObjectCollection entryentity = (DynamicObjectCollection) this.getModel().getValue("entryentity");
        for(DynamicObject studentscore:entryentity) {
            int finalscore = 0;

            long studentinfo = studentscore.getDynamicObject("uof0_studentinfo").getLong("uof0_userfield.id");
            DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("uof0_st_homework", "creator,entryentity", new QFilter[]{
                            new QFilter("uof0_textfield", QCP.equals, examno),
                            new QFilter("creator.id", QCP.equals, studentinfo),
                            new QFilter("billstatus", QCP.not_equals, "A")
                    }
            );
            if (dynamicObject == null) {
                studentscore.set("uof0_score", finalscore);
                continue;
            }
            DynamicObjectCollection topics = dynamicObject.getDynamicObjectCollection("entryentity");
            for(DynamicObject topic:topics){
                finalscore+=topic.getInt("uof0_integerfield");
            }
            studentscore.set("uof0_score",finalscore);
        }
        this.getPageCache().put("push","true");
        return;
    }
}