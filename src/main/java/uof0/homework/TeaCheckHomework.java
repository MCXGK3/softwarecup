package uof0.homework;

import com.kingdee.bos.qing.common.grammar.funcimpl.MathematicFunctions;
import com.kingdee.cosmic.ctrl.kds.model.struct.embed.image.DynamicAccessImageModel;
import com.nbcb.sdk.aes.service.BussinessAdapterService;
import kd.bos.coderule.api.CodeRuleInfo;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.form.BindingContext;
import kd.bos.form.FormShowParameter;
import kd.bos.form.IFormView;
import kd.bos.form.ShowType;
import kd.bos.form.container.Tab;
import kd.bos.form.control.*;
import kd.bos.form.field.DecimalEdit;
import kd.bos.form.field.IntegerEdit;
import kd.bos.form.field.TextEdit;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.AttachmentServiceHelper;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.coderule.CodeRuleServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.*;
import java.util.List;

/**
 * 动态表单插件
 */
public class TeaCheckHomework extends AbstractFormPlugin implements Plugin {

    static String savebutton="uof0_save";
    static String submitbutton="uof0_submit";
    private DynamicObject getStudentEntity(){
        Object pk=this.getView().getFormShowParameter().getCustomParam("studentHomeworkPK");
        DynamicObject entity;
        if(pk==null) {
            QFilter[] stfilters = new QFilter[1];
            stfilters[0] = (new QFilter("creator.id", QCP.equals, RequestContext.get().getCurrUserId()));
//        stfilters[1]=(new QFilter("uof0_textfield",QCP.equals,entity.get("billno")));
            entity=BusinessDataServiceHelper.loadSingle("uof0_st_homework",stfilters);
        }
        else {
            entity= BusinessDataServiceHelper.loadSingle(pk,"uof0_st_homework");
        }
        return entity;
    }
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Button save=this.getControl(savebutton);
        Button submit=this.getControl(submitbutton);
        save.addClickListener(this);
        submit.addClickListener(this);
    }

    public void click(EventObject evt) {
        super.click(evt);
        DynamicObject studentEntity = getStudentEntity();
        DynamicObject teacherEntity = getTeacherEntity(studentEntity);
        Tab tab = this.getControl("guidecontent");
        List<Control> pages= tab.getItems();
        switch (((Control)evt.getSource()).getKey()) {
            case "uof0_save":
                int flag=-1;
                for(int i=0;i<teacherEntity.getDynamicObjectCollection("uof0_entryentity").size();i++){
                    String id=this.getPageCache().get("page"+i);
                    IFormView iFormView=this.getView().getView(id);
                    DynamicObject dataEntity = this.getModel().getDataEntity();
                    DynamicObject dynamicObject=studentEntity.getDynamicObjectCollection("entryentity").get(i);
                    String pingyu =(String) iFormView.getModel().getValue("uof0_textfield");
                    int score=(int) iFormView.getModel().getValue("uof0_integerfield");
                    dynamicObject.set("uof0_largetextfield1",pingyu);
                    int max=teacherEntity.getDynamicObjectCollection("uof0_entryentity").get(i).getInt("uof0_integerfield");
                    if(score<0||score>max){
                        flag=i;
                        break;
                    }
                    else{
                        dynamicObject.set("uof0_integerfield",score);
                    }
                }
                SaveServiceHelper.update(studentEntity);
                if(flag!=-1){
                    this.getView().showErrorNotification("题目"+(flag+1)+"分值设置错误");
                }
                else {
                    this.getView().showMessage("已保存");
                }
                break;
            case "uof0_submit":
                studentEntity.set("billstatus",'C');
                SaveServiceHelper.update(studentEntity);
                this.getView().showMessage("已批改完成");

        }
    }
    private DynamicObject getTeacherEntity(DynamicObject studentEntity){
        QFilter[] qFilters=new QFilter[1];
        qFilters[0]=new QFilter("billno",QCP.equals,studentEntity.getString("uof0_textfield"));
        return BusinessDataServiceHelper.loadSingle("uof0_tea_homework",qFilters);
    }
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        AttachmentPanel attachmentPanel = this.getControl("uof0_attachmentpanelap");
//        this.getModel().setValue("uof0_attachmentpanelap",attachment);
        List<Map<String,Object>> attach= AttachmentServiceHelper.getAttachments("uof0_tea_homework",getTeacherEntity(getStudentEntity()).get("id"),"attachmentpanel");
        attachmentPanel.upload(attach);
        this.getView().setEnable(false,"uof0_attachmentpanelap");
        this.getView().updateView("uof0_attachmentpanelap");
        if(attach.isEmpty()){
            this.getView().setVisible(false,"uof0_attachmentpanelap");
            this.getView().updateView("uof0_attachmentpanelap");
        }

    }

    @Override
    public void afterCreateNewData(EventObject e) {
        //获取作业内容
        DynamicObject studentEntity = getStudentEntity();
        DynamicObject teacherEntity = getTeacherEntity(studentEntity);
        super.afterCreateNewData(e);
        String title=teacherEntity.getString("uof0_textfield");
        String detail=teacherEntity.getString("uof0_largetextfield");
        boolean havedetail;
        DynamicObjectCollection problems =teacherEntity.getDynamicObjectCollection("uof0_entryentity");
        List<String> answers=new ArrayList<>();

        for(int i=0;i<problems.size();i++){
            answers.add(studentEntity.getDynamicObjectCollection("entryentity").get(i).getString("uof0_largetextfield_tag"));
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
            map.put("check",true);
            map.put("review",false);
            map.put("stuentity",studentEntity.getPkValue());
            formShowParameter.setCustomParams(map);
            formShowParameter.setCaption("第"+(i+1)+"题");
            formView.showForm(formShowParameter);
            this.getPageCache().put("page"+i,formShowParameter.getPageId());
        }
        this.getView().updateView("guidecontent");
        int si=t.getItems().size();
        t.activeTab("uof0_tabpageap");
    }

}