package uof0.signin;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.IFormView;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 单据界面插件
 */
public class GetClassForSignin extends AbstractBillPlugIn implements Plugin {

    private Object getClassPK(){
        return  this.getView().getFormShowParameter().getCustomParam("classPK");
    }

    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        IFormView parentView = this.getView().getParentView();
        Object classPK = getClassPK();
        if(classPK!=null){
            DynamicObject clas= BusinessDataServiceHelper.loadSingle(classPK,"uof0_course_class");
            this.getModel().setValue("uof0_class",clas);
        }
    }
}