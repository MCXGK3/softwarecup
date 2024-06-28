package uof0.homework;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.bill.OperationStatus;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 单据界面插件
 */
public class GetClassForHomework extends AbstractBillPlugIn implements Plugin {
    private Object getClassPK(){
        return  this.getView().getFormShowParameter().getCustomParam("classPK");
    }

    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        if(this.getView().getFormShowParameter().getStatus()== OperationStatus.ADDNEW){
            Object classPK=getClassPK();
            if(classPK!=null) {
                this.getModel().setValue("uof0_basedatafield", BusinessDataServiceHelper.loadSingle(classPK, "uof0_course_class"));
            }
        }
    }

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);
        FormOperate formOperate=(FormOperate) args.getSource();
        if(formOperate.getOperateKey().equals("submit")){
            DynamicObjectCollection entities=this.getModel().getEntryEntity("uof0_entryentity");
            if(entities.size()==0){
                args.setCancel(true);
                this.getView().showErrorNotification("请至少布置一道题目");
            }
            else{
                for(DynamicObject entity:entities){
                    if(entity.getString("uof0_textfield1")==null||entity.getString("uof0_textfield1").isEmpty()){
                    args.setCancel(true);
                    this.getView().showErrorNotification("题目不能为空");
                    break;
                    }
                }
            }
        }
    }
}