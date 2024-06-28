package uof0.classlist;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Label;
import kd.bos.form.control.RichTextEditor;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.awt.*;
import java.util.EventObject;

/**
 * 动态表单插件
 */
public class GetLastAnnouncement extends AbstractFormPlugin implements Plugin {
    private Object getClassPK() {
        return this.getView().getFormShowParameter().getCustomParam("classPK");
    }

    @Override
    public void beforeBindData(EventObject e) {
        super.beforeBindData(e);
        Label title = this.getView().getControl("uof0_labelap");
        RichTextEditor richTextEditor = this.getView().getControl("uof0_richtexteditorap");
        DynamicObject classentity;
        if(getClassPK()!=null)
         classentity= BusinessDataServiceHelper.loadSingle(getClassPK(), "uof0_course_class");
        else {
            classentity=null;
        }
        DynamicObject[] anns;
        if (classentity == null) {
            anns = BusinessDataServiceHelper.load("uof0_class_announcement", "uof0_textfield2,uof0_largetextfield_tag,uof0_datefield", new QFilter[0], "uof0_datefield");
        } else {
            QFilter[] qFilters = {new QFilter("uof0_textfield", QCP.equals, classentity.getString("number"))};
            anns = BusinessDataServiceHelper.load("uof0_class_announcement", "uof0_textfield2,uof0_largetextfield_tag,uof0_datefield", qFilters, "uof0_datefield");
        }
        if (anns.length == 0) {
            title.setText("没有最新的公告");
            this.getView().setVisible(false,"uof0_richtexteditorap");
        }
        else {
            DynamicObject res = anns[0];
            title.setText(res.getString("uof0_textfield2"));
            richTextEditor.setText(res.getString("uof0_largetextfield_tag"));

        }
    }
}