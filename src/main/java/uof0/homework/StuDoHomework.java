package uof0.homework;

import com.kingdee.cosmic.ctrl.common.CtrlUtil;
import com.kingdee.cosmic.ctrl.ext.ui.wizards.chart.chartpanelImpl.typechooser.qing.QingWaterFallLabel;
import kd.bos.coderule.api.CodeRuleInfo;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.designer.property.WizardPlugin;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.operate.Save;
import kd.bos.entity.property.PictureProp;
import kd.bos.form.CloseCallBack;
import kd.bos.form.FormShowParameter;
import kd.bos.form.IFormView;
import kd.bos.form.ShowType;
import kd.bos.form.container.Tab;
import kd.bos.form.container.TabPage;
import kd.bos.form.container.Wizard;
import kd.bos.form.control.*;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.events.BeforeClosedEvent;
import kd.bos.form.events.ClosedCallBackEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.form.plugin.CodeRulePlugin;
import kd.bos.isc.util.script.feature.control.loop.For;
import kd.bos.openapi.kcf.utils.OauthTokenUtil;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.AttachmentServiceHelper;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.coderule.CodeRuleServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.workflow.design.plugin.UserTreeListF7PartPlugin;
import kd.sdk.plugin.Plugin;

import java.util.*;
import java.util.List;

/**
 * 动态表单插件
 */
public class StuDoHomework extends AbstractFormPlugin implements Plugin {
    private  DynamicObject entity=null;
    private  DynamicObject studententity=null;
    private  DynamicObjectCollection problems=null;
    private  List<FormShowParameter> formshows=new ArrayList<>();
    private  List<String> answers=new ArrayList<>();
    private  long attachment=0;
    private static String savebutton="uof0_save";
    private static String submitbutton="uof0_submit";

    @Override
    public void closedCallBack(ClosedCallBackEvent closedCallBackEvent) {
        super.closedCallBack(closedCallBackEvent);

    }

    @Override
    public void initialize() {
        super.initialize();
        this.getPageCache().put("pagecount","0");
    }

    private DynamicObject getTeacherHomework(){
        Object pk=this.getView().getFormShowParameter().getCustomParam("teacherEntity");
        if(pk!=null){
            entity=BusinessDataServiceHelper.loadSingle(pk,"uof0_tea_homework");
        }
        else {
            entity = BusinessDataServiceHelper.loadSingle("uof0_tea_homework", new QFilter[0]);
        }
        return entity;
    }

    private DynamicObject getStudentHomework(DynamicObject teacherEntity){
        QFilter[] stfilters=new QFilter[2];
        stfilters[0]=(new QFilter("creator.id", QCP.equals, RequestContext.get().getCurrUserId()));
        stfilters[1]=(new QFilter("uof0_textfield",QCP.equals,teacherEntity.get("billno")));
        studententity=BusinessDataServiceHelper.loadSingle("uof0_st_homework",  stfilters);
        return studententity;
    }



    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Button save=this.getControl(savebutton);
        Button submit=this.getControl(submitbutton);
        save.addClickListener(this);
        submit.addClickListener(this);
    }
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        entity=getTeacherHomework();
        studententity=getStudentHomework(entity);
        Tab tab = this.getControl("guidecontent");
        List<Control> pages= tab.getItems();
        switch (((Control)evt.getSource()).getKey()) {
            case "uof0_save":
                for(int i=0;i<entity.getDynamicObjectCollection("uof0_entryentity").size();i++){
                    String id=this.getPageCache().get("page"+i);
                    IFormView iFormView=this.getView().getView(id);
                    DynamicObject dynamicObject=studententity.getDynamicObjectCollection("entryentity").get(i);
                    RichTextEditor richTextEditor=iFormView.getControl("uof0_richtexteditorap");
                    dynamicObject.set("uof0_largetextfield_tag",richTextEditor.getText());
                }
                SaveServiceHelper.update(studententity);
                this.getView().showSuccessNotification("已保存");
                break;
            default:break;
        }
    }

    @Override
    public void afterDoOperation(AfterDoOperationEventArgs afterDoOperationEventArgs) {
        super.afterDoOperation(afterDoOperationEventArgs);
        String key = afterDoOperationEventArgs.getOperateKey();
        if(key.equals("submit")){
            entity=getTeacherHomework();
            studententity=getStudentHomework(entity);
            studententity.set("billstatus",'B');
            SaveServiceHelper.update(studententity);
            this.getView().showSuccessNotification("已提交");
        }

    }
    @Override
    public void beforeClosed(BeforeClosedEvent e) {
        super.beforeClosed(e);



//        SaveServiceHelper.update(studententity);
    }

    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        AttachmentPanel attachmentPanel = this.getControl("uof0_attachmentpanelap");
//        this.getModel().setValue("uof0_attachmentpanelap",attachment);
        List<Map<String,Object>> attach=AttachmentServiceHelper.getAttachments("uof0_tea_homework",entity.get("id"),"attachmentpanel");
        attachmentPanel.upload(attach);
        attachmentPanel.setLock("new,edit,submit,audit");
        this.getView().updateView("uof0_attachmentpanelap");
        if(attach.isEmpty()){
//            this.getView().setVisible(false,"uof0_attachmentpanelap");
            this.getView().updateView("uof0_attachmentpanelap");
        }
        if((boolean) this.getView().getFormShowParameter().getCustomParam("deadline")){
            this.getView().setVisible(false,"uof0_save");
            this.getView().setVisible(false,"uof0_submit");
            this.getView().updateView("uof0_save");
            this.getView().updateView("uof0_submit");
        }
        else{
            this.getView().setVisible(false,"uof0_deadline");
            this.getView().updateView("uof0_deadline");
        }
    }

    @Override
    public void afterCreateNewData(EventObject e) {
        //获取作业内容
        List<QFilter> filters=new ArrayList<>();
//        DynamicObject entity=QueryServiceHelper.queryOne("uof0_tea_homework","uof0_textfield,uof0_largetextfield,uof0_checkboxfield,uof0_entryentity",new QFilter[0]);
        entity= getTeacherHomework();
        super.afterCreateNewData(e);
        String title="作业";
        String detail="";
        boolean havedetail;
        problems=new DynamicObjectCollection();
        if(entity!=null){
            title=entity.getString("uof0_textfield");
            detail=entity.getString("uof0_largetextfield");
            havedetail=entity.getBoolean("uof0_checkboxfield");
            problems=entity.getDynamicObjectCollection("uof0_entryentity");
        }

        //获取学生单据
        QFilter[] stfilters=new QFilter[2];
        stfilters[0]=(new QFilter("creator.id", QCP.equals, RequestContext.get().getCurrUserId()));
        stfilters[1]=(new QFilter("uof0_textfield",QCP.equals,entity.get("billno")));
        studententity=BusinessDataServiceHelper.loadSingle("uof0_st_homework",  stfilters);
        if(studententity==null){
            studententity=BusinessDataServiceHelper.newDynamicObject("uof0_st_homework");
            CodeRuleInfo codeRuleInfo= CodeRuleServiceHelper.getCodeRule(studententity.getDataEntityType().getName(),studententity,null);
            studententity.set("billno",CodeRuleServiceHelper.getNumber(codeRuleInfo,studententity));
            studententity.set("uof0_textfield",entity.get("billno"));
            DynamicObject self=BusinessDataServiceHelper.loadSingle(RequestContext.get().getCurrUserId(),"bos_user");
            studententity.set("creator",self);
            studententity.set("uof0_textfield1",title);
            studententity.set("billstatus",'A');
            DynamicObjectCollection answers=studententity.getDynamicObjectCollection("entryentity");
            DynamicObjectType dynamicObjectType=answers.getDynamicObjectType();
            for(int i=0;i<problems.size();i++){
                answers.add(new DynamicObject(dynamicObjectType));
            }

            SaveServiceHelper.save(new DynamicObject[]{studententity});
        }
        for(int i=0;i<problems.size();i++){
            answers.add(studententity.getDynamicObjectCollection("entryentity").get(i).getString("uof0_largetextfield_tag"));

        }
        //设置第一页内容
        Tab t=this.getControl("guidecontent");
        Label label = this.getControl("uof0_labelap");
        label.setText(detail);
        //根据题目数量添加页面
        IFormView formView=getView();
        formView.setFormTitle(new LocaleString(title));
        for(int i=0;i<problems.size();i++) {
            FormShowParameter formShowParameter = new FormShowParameter();
            formShowParameter.getOpenStyle().setShowType(ShowType.NewTabPage);
            formShowParameter.getOpenStyle().setTargetKey("guidecontent");
            formShowParameter.setFormId("uof0_test");
            Map<String,Object> map = new HashMap<>();
            map.put("title",i);
            map.put("topic",problems.get(i).get("uof0_textfield1"));
            map.put("image",problems.get(i).get("uof0_picturefield"));
            map.put("score",problems.get(i).get("uof0_integerfield"));
            map.put("answer",answers.get(i));
            map.put("standardAnswer",problems.get(i).getString("uof0_textfield2"));
            map.put("stuentity",studententity.getPkValue());
            map.put("check",this.getView().getFormShowParameter().getCustomParam("check"));
            map.put("review",this.getView().getFormShowParameter().getCustomParam("review"));
            formShowParameter.setCustomParams(map);
            formShowParameter.setCaption("第"+(i+1)+"题");
            formshows.add(formShowParameter);
            formView.showForm(formShowParameter);
            this.getPageCache().put("page"+i,formShowParameter.getPageId());
        }
        this.getView().updateView("guidecontent");
        int si=t.getItems().size();
        t.activeTab("uof0_tabpageap");
    }
}