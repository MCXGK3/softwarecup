package uof0.course_class;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.bill.OperationStatus;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 单据界面插件
 */
public class GetClassForBill extends AbstractBillPlugIn implements Plugin {
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("uof0_course_class", new QFilter[]{new QFilter("name", QCP.equals, this.getModel().getValue("uof0_clsname"))});
        this.getModel().setValue("uof0_class", dynamicObject);
    }
}