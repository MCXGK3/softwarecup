package uof0.homework;

import com.kingdee.bos.qing.core.model.analysis.square.chart.Grid;
import com.kingdee.bos.qing.modeler.imexport.utils.ListUtil;
import com.kingdee.cosmic.ctrl.common.CtrlUtil;
import kd.bos.ais.core.searcher.IThirdPartSearcher;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.devportal.checking.plugin.IntegrityError;
import kd.bos.dts.log.DateUtil;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.entity.filter.ControlFilters;
import kd.bos.form.FormShowParameter;
import kd.bos.form.IFormView;
import kd.bos.form.ShowType;
import kd.bos.form.control.AbstractGrid;
import kd.bos.form.control.Control;
import kd.bos.form.control.events.CellClickEvent;
import kd.bos.form.control.events.CellClickListener;
import kd.bos.form.events.*;
import kd.bos.form.operate.FormOperate;
import kd.bos.isc.util.script.feature.misc.data.Char;
import kd.bos.kflow.meta.activity.OperateActionAp;
import kd.bos.list.*;
import kd.bos.list.events.ListHyperLinkClickEvent;
import kd.bos.list.events.ListRowClickEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.list.plugin.ListViewPluginProxy;
import kd.bos.mvc.SessionManager;
import kd.bos.mvc.list.ListDataProvider;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

/**
 * 标准单据列表插件
 */
//传入页面参数：班级主键
public class FindHomeworkForStu extends AbstractListPlugin implements Plugin, CellClickListener {
    private Object getClassPK(){
        return this.getView().getFormShowParameter().getCustomParam("classPK");
    }

    private boolean isExam(){
        if(!this.getView().getFormShowParameter().getCustomParams().containsKey("exam")) return false;
        if(this.getView().getFormShowParameter().getCustomParam("exam") instanceof String){
            String exam=this.getView().getFormShowParameter().getCustomParam("exam");
            return exam.equals("true");
        }
        return (boolean) this.getView().getFormShowParameter().getCustomParam("exam");
    }
    @Override
    public void beforeCreateListColumns(BeforeCreateListColumnsArgs args) {
        super.beforeCreateListColumns(args);
        AbstractListColumn abstractListColumn;
        ListColumn listColumn=new ListColumn();
        listColumn.setFieldName("uof0_basedatafield");
        listColumn.setCaption(new LocaleString( "课堂"));
        listColumn.setListFieldKey("testListFieldKey");
        args.addListColumn(listColumn);

    }

    @Override
    public void initialize() {
        super.initialize();
        if(this.getView().getPageCache().get("first")==null){
            this.getView().getPageCache().put("filter","ABCD");
            this.getView().getPageCache().put("first","true");
        }
        BillList billList=this.getControl("billlistap");
        billList.getView().setVisible(isExam(),"uof0_datetimefield1");
        billList.getView().updateView("uof0_datetimefield1");
        billList.addCellClickListener(this);
    }


    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Control control;
        ListGridView listGridView=this.getControl("gridview");
        listGridView.addClickListener(this);
        BillList billList=this.getControl("billlistap");
        billList.addClickListener(this);
    }

    @Override
    public void filterContainerAfterSearchClick(FilterContainerSearchClickArgs args) {
        super.filterContainerAfterSearchClick(args);
        QFilter qFilter = args.getQFilter("uof0_billstatusfield");
        if(qFilter==null){
            this.getView().getPageCache().put("filter","ABCD");
            return;
        }
        String string = qFilter.toString();
        String newstr="";
        if(string.contains("A")){
            newstr+='A';
        }
        if(string.contains("B")){
            newstr+='B';
        }
        if(string.contains("C")){
            newstr+='C';
        }
        if(string.contains("D")){
            newstr+='D';
        }
        this.getView().getPageCache().put("filter",newstr);


    }


    @Override
    public void setFilter(SetFilterEvent e) {
        List<QFilter> qFilters = e.getQFilters();
        DynamicObject[] objects = BusinessDataServiceHelper.load("uof0_classaddreq", "uof0_class",
                new QFilter[]{new QFilter("creator.id", QCP.equals, RequestContext.get().getCurrUserId()),
                        new QFilter("billstatus", QCP.equals, "C")});
        List<Long> classids=new ArrayList<>();
        //当输入课堂主键为空时会自动获取所有参加的课堂
        if(getClassPK()==null){
            for(DynamicObject object:objects){
                classids.add((Long) object.getDynamicObject("uof0_class").getPkValue());
            }
            qFilters.add(new QFilter("uof0_basedatafield.id",QCP.in,classids));
        }
        else {
            qFilters.add(new QFilter("uof0_basedatafield.id", QCP.equals, getClassPK()));
        }
        qFilters.add(new QFilter("billstatus",QCP.equals,"B"));
        qFilters.add(new QFilter("uof0_checkboxfield1",QCP.equals,isExam()));
        for(QFilter filter:qFilters){
            if(filter.toString().contains("uof0_billstatusfield")){
                qFilters.remove(filter);
                break;
            }
        }

    }

    @Override
    public void listRowClick(ListRowClickEvent evt) {
        super.listRowClick(evt);
        this.getView().getPageCache().put("clickrow",""+evt.getRow());
        this.clearSelection();
    }

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);
        FormOperate formOperate = (FormOperate) args.getSource();
        String operateKey = formOperate.getOperateKey();
        //学生查看作业
        if (operateKey.equals("view")) {
            int row= new Integer(this.getView().getPageCache().get("clickrow"));
            ListSelectedRow listSelectedRow= this.getCurrentListAllRowCollection().get(row);
            Object pk = listSelectedRow.getPrimaryKeyValue();
            DynamicObject teacherEntity=BusinessDataServiceHelper.loadSingle(pk,"uof0_tea_homework");
            Date now=new Date();
            if(isExam()) {
                if (teacherEntity.getDate("uof0_datetimefield1").after(now)) {
                    this.getView().showErrorNotification("考试尚未开始");
                    args.setCancel(true);
                    return;
                }
            }
            QFilter[] qFilter=new QFilter[2];
            qFilter[0]=new QFilter("creator.id", QCP.equals, RequestContext.get().getCurrUserId());
            qFilter[1]=new QFilter("uof0_textfield",QCP.equals,teacherEntity.getString("billno"));
            DynamicObject studentEntity = BusinessDataServiceHelper.loadSingle("uof0_st_homework", qFilter);

            FormShowParameter formShowParameter = new FormShowParameter();
            formShowParameter.getOpenStyle().setShowType(ShowType.Modal);
            formShowParameter.getOpenStyle().setTargetKey("_submaintab_");
            formShowParameter.setFormId("uof0_stu_do_homework");
            formShowParameter.setCustomParam("review", true);
            formShowParameter.setCustomParam("check", false);
            if(studentEntity!=null){
                if(studentEntity.getString("billstatus").equals("C")){
                    formShowParameter.setCustomParam("check",true);
                }
            }

            formShowParameter.setCustomParam("deadline",false);
            if(((Date) teacherEntity.get("uof0_datetimefield")).before(now)){
                formShowParameter.setCustomParam("review",true);
                formShowParameter.setCustomParam("deadline",true);
            }
            formShowParameter.setCustomParam("exam",isExam());
            formShowParameter.setCustomParam("teacherEntity", pk);
            this.getView().showForm(formShowParameter);

            args.setCancel(true);
        }
        //学生回答作业
        else if (operateKey.equals("modify")) {
            int row= new Integer(this.getView().getPageCache().get("clickrow"));
            ListSelectedRow listSelectedRow= this.getCurrentListAllRowCollection().get(row);
            Object pk = listSelectedRow.getPrimaryKeyValue();
            DynamicObject teacherEntity=BusinessDataServiceHelper.loadSingle(pk,"uof0_tea_homework");
            Date now=new Date();
            if(isExam()) {
                if (teacherEntity.getDate("uof0_datetimefield1").after(now)) {
                    this.getView().showErrorNotification("考试尚未开始");
                    args.setCancel(true);
                    return;
                }
            }

            FormShowParameter formShowParameter = new FormShowParameter();
            formShowParameter.getOpenStyle().setShowType(ShowType.Modal);
            formShowParameter.getOpenStyle().setTargetKey("_submaintab_");
            formShowParameter.setFormId("uof0_stu_do_homework");
            formShowParameter.setCustomParam("review", false);
            formShowParameter.setCustomParam("deadline",false);
            if(teacherEntity.getBoolean("uof0_checkboxfield1")){
                formShowParameter.setCustomParam("exam",true);
            }

            if(((Date) teacherEntity.get("uof0_datetimefield")).before(now)){
                formShowParameter.setCustomParam("review",true);
                formShowParameter.setCustomParam("deadline",true);
            }
            formShowParameter.setCustomParam("exam",isExam());
            formShowParameter.setCustomParam("check", false);
            formShowParameter.setCustomParam("teacherEntity", pk);
            this.getView().showForm(formShowParameter);

            args.setCancel(true);

        }
    }


    @Override
    public void beforeCreateListDataProvider(BeforeCreateListDataProviderArgs args) {
        args.setListDataProvider(new MyListDataProvider(this.getView().getPageCache().get("filter")));
        ControlFilters controlFilters = this.getControlFilters();
    }

    @Override
    public void cellClick(CellClickEvent cellClickEvent) {
        int i=cellClickEvent.getRow();
        String key = cellClickEvent.getFieldKey();
        ListSelectedRowCollection rows = this.getCurrentListAllRowCollection();
        for(int i1 = 0;i1<rows.size();i1++){
            ListSelectedRow row = rows.get(i1);
            Object value = row.getPrimaryKeyValue();
        }

    }

    @Override
    public void cellDoubleClick(CellClickEvent cellClickEvent) {

    }

    @Override
    public void billListHyperLinkClick(HyperLinkClickArgs args) {
        int index = args.getRowIndex();
        String fieldName = args.getFieldName();
        HyperLinkClickEvent event = args.getHyperLinkClickEvent();

        super.billListHyperLinkClick(args);
    }

}

class MyListDataProvider extends ListDataProvider {
    public MyListDataProvider(String filter){
        this.filter=filter;
    }
    String filter="";
    @Override
    public DynamicObjectCollection getData(int start, int limit) {
        DynamicObjectCollection data = super.getData(start, limit);
        if(data.isEmpty()){
            return  data;
        }

        DynamicObjectCollection finaldata=new DynamicObjectCollection();
        for(DynamicObject da:data){
            QFilter[] qFilter=new QFilter[2];
            qFilter[0]=new QFilter("creator.id", QCP.equals, RequestContext.get().getCurrUserId());
            qFilter[1]=new QFilter("uof0_textfield",QCP.equals,da.getString("billno"));
            DynamicObject studentEntity = BusinessDataServiceHelper.loadSingle("uof0_st_homework", qFilter);
            if(studentEntity==null){
                da.set("uof0_billstatusfield","A");
            }
            else{
                switch ((String )studentEntity.get("billstatus")){
                    case "A":
                        da.set("uof0_billstatusfield","B");
                        break;
                    case "B":
                        da.set("uof0_billstatusfield","C");
                        break;
                    case "C":
                        da.set("uof0_billstatusfield","D");
                        break;
                }
            }
            if(filter.contains(da.getString("uof0_billstatusfield"))){
                if(da.getString("billstatus").equals("B")) {
                    finaldata.add(da);
                    Date date=da.getDate("uof0_datetimefield");
                }
            }
        }

        return finaldata;
    }

}