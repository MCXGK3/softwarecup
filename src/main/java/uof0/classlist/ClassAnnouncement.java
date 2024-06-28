package uof0.classlist;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.RichTextEditor;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 单据界面插件
 */
public class ClassAnnouncement extends AbstractBillPlugIn implements Plugin {
    private Object getClassPK(){
        Object classPK = this.getView().getFormShowParameter().getCustomParam("classPK");
        return classPK;
    }

    @Override
    public void beforeBindData(EventObject e) {
        super.beforeBindData(e);
        Object coursePk= getClassPK();
        if(coursePk!=null){
            DynamicObject entity= BusinessDataServiceHelper.loadSingle(coursePk,"uof0_course_class");
            if(entity!=null){
                this.getModel().setValue("uof0_textfield",entity.get("number"));
                this.getModel().setValue("uof0_textfield1",entity.getString("name"));
            }
        }
        String text=(String)this.getModel().getValue("uof0_largetextfield_tag");
        RichTextEditor richTextEditor=this.getView().getControl("uof0_richtexteditorap");
        richTextEditor.setText(text);
        this.getView().updateView("uof0_richtexteditorap");
    }

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);
        RichTextEditor richTextEditor=this.getView().getControl("uof0_richtexteditorap");
        this.getModel().setValue("uof0_largetextfield_tag",richTextEditor.getText());
    }
}