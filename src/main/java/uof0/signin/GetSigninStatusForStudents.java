package uof0.signin;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.datamodel.events.BeforePackageDataEvent;
import kd.bos.entity.datamodel.events.PackageDataEvent;
import kd.bos.entity.list.column.DynamicTextColumnDesc;
import kd.bos.entity.list.events.BeforePackageDataListener;
import kd.bos.form.operatecol.OperationColItem;
import kd.bos.list.BillList;
import kd.bos.list.ListOperationColumn;
import kd.bos.list.column.ListOperationColumnDesc;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;

/**
 * 标准单据列表插件
 */
public class GetSigninStatusForStudents extends AbstractListPlugin implements Plugin, BeforePackageDataListener {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);

    }

    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        BillList billList = this.getControl("billlistap");
        billList.addBeforePackageDataListener(this);
        billList.addPackageDataListener(this::packageData);
//        Object value = this.getModel().getValue("uof0_status");
    }

    @Override
    public void beforePackageData(BeforePackageDataEvent e) {
        super.beforePackageData(e);
        DynamicObjectCollection pageData = e.getPageData();

    }

    @Override
    public void packageData(PackageDataEvent e) {
        super.packageData(e);
        DynamicObject rowData = e.getRowData();
        if (e.getSource() instanceof DynamicTextColumnDesc) {
            if (((DynamicTextColumnDesc) e.getSource()).getKey().equals("uof0_status")) {
                String billno = rowData.getString("billno");
                DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("uof0_signin", new QFilter[]{
                        new QFilter("uof0_textfield", QCP.equals, billno),
                        new QFilter("creator.id", QCP.equals, RequestContext.get().getCurrUserId())
                });
                if (dynamicObject == null || !dynamicObject.getString("billstatus").equals("B")) {
                    e.setFormatValue("未签到");
                } else {
                    e.setFormatValue("已签到");
                }
            }
        } else if (e.getSource() instanceof ListOperationColumnDesc) {
            Date now = new Date();
            if (e.getRowData().getDate("uof0_endtime").before(now)) {
                ArrayList<OperationColItem> formatValue = (ArrayList<OperationColItem>) e.getFormatValue();
                formatValue.get(0).setVisible(false);

            } else {
                    String billno = rowData.getString("billno");
                    DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("uof0_signin", new QFilter[]{
                            new QFilter("uof0_textfield", QCP.equals, billno),
                            new QFilter("creator.id", QCP.equals, RequestContext.get().getCurrUserId())
                    });
                    if (dynamicObject == null || !dynamicObject.getString("billstatus").equals("B")) {
                    } else {
                        ArrayList<OperationColItem> formatValue = (ArrayList<OperationColItem>) e.getFormatValue();
                        formatValue.get(0).setVisible(false);
                    }



            }
        }
    }
}