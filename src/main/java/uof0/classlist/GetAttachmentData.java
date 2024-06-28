package uof0.classlist;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.devportal.plugin.BizAppMenuTreePlugin;
import kd.bos.form.plugin.TemplateBillEdit;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 单据界面插件
 */
public class GetAttachmentData extends AbstractBillPlugIn implements Plugin {
    @Override
    public void beforeBindData(EventObject e) {
        super.beforeBindData(e);
        Object isTeacher = this.getView().getFormShowParameter().getCustomParam("isTeacher");
        if(isTeacher!=null && (boolean) isTeacher){

        }
        else{
            this.getView().setEnable(false,"attachmentpanel");
            this.getView().updateView("attachmentpanel");
            this.getView().setVisible(false,"bar_save");
        }
    }
}