package uof0.homework;

import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.entity.datamodel.events.PackageDataEvent;
import kd.bos.entity.list.column.DynamicTextColumnDesc;
import kd.bos.form.ShowType;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.form.operate.FormOperate;
import kd.bos.list.BillList;
import kd.bos.list.ListShowParameter;
import kd.bos.list.events.ListRowClickEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.openapi.kcf.spi.OperationAction;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 标准单据列表插件
 */
public class FindHomeworkForTea extends AbstractListPlugin implements Plugin {
    private Object getClassPK(){
        return this.getView().getFormShowParameter().getCustomParam("classPK");
    }
    private boolean isExam(){
        if(!this.getView().getFormShowParameter().getCustomParams().containsKey("exam")) return false;
        if(this.getView().getFormShowParameter().getCustomParam("exam") instanceof String){
            String exam=this.getView().getFormShowParameter().getCustomParam("exam");
            if(exam.equals("true")) return true;
            else return  false;
        }
        return (boolean) this.getView().getFormShowParameter().getCustomParam("exam");
    }
    @Override
    public void setFilter(SetFilterEvent e) {
        List<QFilter> filters = e.getQFilters();
        DynamicObject[] objects = BusinessDataServiceHelper.load("uof0_course_class", "number,uof0_teacher", new QFilter[]{
                new QFilter("uof0_teacher.id", QCP.equals, RequestContext.get().getCurrUserId()),
                new QFilter("status", QCP.equals, "C"),
                new QFilter("enable", QCP.equals, '1')
        });
        List<Long> classIds=new ArrayList<>();
        if(getClassPK()==null){
            for(DynamicObject object:objects){
                classIds.add((Long) object.getPkValue());
            }
            filters.add(new QFilter("uof0_basedatafield.id",QCP.in,classIds));
        }
        else {
            filters.add(new QFilter("uof0_basedatafield.id", QCP.equals, getClassPK()));
        }
        filters.add(new QFilter("uof0_checkboxfield1",QCP.equals,isExam()));
        super.setFilter(e);
    }

    @Override
    public void initialize() {
        super.initialize();
        BillList billList=this.getControl("billlistap");
        billList.getView().setVisible(isExam(),"uof0_datetimefield1");
        billList.getView().updateView("uof0_datetimefield1");
        billList.addPackageDataListener(this::packageData);
    }

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);
        FormOperate formOperate= (FormOperate) args.getSource();
        String key = formOperate.getOperateKey();
        if(key.equals("new")){
            args.setCancel(true);
            BillShowParameter newBill=new BillShowParameter();
            newBill.setStatus(OperationStatus.ADDNEW);
            newBill.setCustomParam("classPK",getClassPK());
            newBill.setFormId("uof0_tea_homework");
            newBill.setCaption("新增作业");
            newBill.getOpenStyle().setShowType(ShowType.Modal);
            newBill.getOpenStyle().setTargetKey("uof0__submaintab_");
            this.getView().showForm(newBill);
        }
    }

    @Override
    public void packageData(PackageDataEvent e) {
        super.packageData(e);
        if(e.getSource() instanceof DynamicTextColumnDesc){
            String key = ((DynamicTextColumnDesc) e.getSource()).getKey();
            if(key.equals("uof0_dynamictextlistcolum")){
                int anInt = e.getRowData().getDynamicObject("uof0_basedatafield").getInt("uof0_selectednum");
                e.setFormatValue(anInt);
            } else if (key.equals("uof0_dynamictextlistcolu1")) {
                String billno = e.getRowData().getString("billno");
                DynamicObject[] objects = BusinessDataServiceHelper.load("uof0_st_homework", "billno", new QFilter[]{
                        new QFilter("uof0_textfield", QCP.equals, billno),
                        new QFilter("billstatus",QCP.equals,"A")
                });
                e.setFormatValue(objects.length);
                
            }
        }

    }

    @Override
    public void listRowClick(ListRowClickEvent evt) {
        super.listRowClick(evt);
        int row = evt.getRow();
        if(row<0) return;
        ListSelectedRow listSelectedRow = this.getCurrentListAllRowCollection().get(row);
        Object value = listSelectedRow.getPrimaryKeyValue();
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle(value, "uof0_tea_homework");
        String billno = dynamicObject.getString("billno");

        ListShowParameter listShowParameter=new ListShowParameter();
        listShowParameter.setCaption(dynamicObject.getString("uof0_textfield"));
        listShowParameter.setFormId("bos_list");
        listShowParameter.setBillFormId("uof0_stuhw_view_for_tea");
        listShowParameter.getOpenStyle().setShowType(ShowType.Modal);
        listShowParameter.setCustomParam("homeworkNO",billno);
        this.clearSelection();
        this.getView().showForm(listShowParameter);

    }
}