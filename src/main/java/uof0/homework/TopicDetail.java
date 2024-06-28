package uof0.homework;

import com.kingdee.cosmic.ctrl.common.CtrlUtil;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.designer.property.WizardPlugin;
import kd.bos.form.events.BeforeClosedEvent;
import kd.bos.form.events.CustomEventArgs;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.lock.curator.CuratorLocker;
import kd.bos.metadata.form.control.ImageAp;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.url.UrlService;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import java.util.Map;
import kd.bos.form.control.*;

/**
 * 动态表单插件
 */

public class TopicDetail extends AbstractFormPlugin implements Plugin {
    DynamicObject entity=null;


    @Override
    public void customEvent(CustomEventArgs e) {
        super.customEvent(e);
        Map<String, Object>map = this.getView().getFormShowParameter().getCustomParams();
        Object pk= map.get("stuentity");
        entity= BusinessDataServiceHelper.loadSingle(pk,"uof0_st_homework");
        int tit= (int)map.get("title");
        DynamicObject curtit=entity.getDynamicObjectCollection("entryentity").get(tit);
        RichTextEditor richTextEditor=this.getControl("uof0_richtexteditorap");
        curtit.set("uof0_largetextfield",richTextEditor.getText());
        SaveServiceHelper.update(entity);
    }

    @Override
    public void beforeClosed(BeforeClosedEvent e) {

        super.beforeClosed(e);
    }

    //reveiw check
    //00 学生回答作业
    //01 教师批改作业
    //10 学生查看作业
    //11 批改后学生查看作业
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        Map<String, Object>map = this.getView().getFormShowParameter().getCustomParams();
        Label topic=(Label) this.getControl("uof0_labelap");
        Image image=this.getControl("uof0_imageap");
        RichTextEditor richTextEditor=this.getControl("uof0_richtexteditorap");
        if(map!=null){
            String top="";
            if(map.containsKey("topic")){
                top+=map.get("topic");
            }
            if(map.containsKey("score")){
                top+=("("+map.get("score")+"分)");
            }
            String fullurl=UrlService.getImageFullUrl((String) map.getOrDefault("image",""));
            image.setUrl(fullurl);
            if(map.getOrDefault("image","").equals("")){
                this.getView().setVisible(false,"uof0_imageap");
                this.getView().updateView("uof0_imageap");
            }
            topic.setText(top);
            richTextEditor.setText((String) map.getOrDefault("answer",""));
            if((boolean) map.get("check")){
                this.getView().setEnable(false,"uof0_richtexteditorap");
            }
            if((boolean) map.get("review")&&(!(boolean) map.get("check"))){
                this.getView().setEnable(false,"uof0_richtexteditorap");
            }
            this.getView().updateView("uof0_richtexteditorap");


            if(!(boolean) map.get("check")){
                this.getView().setVisible(false,"uof0_textfield");
                this.getView().setVisible(false,"uof0_integerfield");
                this.getView().updateView("uof0_textfield");
                this.getView().updateView("uof0_integerfield");
            }
            else{
                DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle(map.get("stuentity"), "uof0_st_homework");
                DynamicObject dynamicObject1 = dynamicObject.getDynamicObjectCollection("entryentity").get((int) map.get("title"));
                this.getModel().setValue("uof0_textfield",dynamicObject1.getString("uof0_largetextfield1"));
                this.getModel().setValue("uof0_integerfield",dynamicObject1.getInt("uof0_integerfield"));
                this.getModel().updateCache();
                if((boolean) map.get("review")){
                    this.getView().setEnable(false,"uof0_textfield");
                    this.getView().setEnable(false,"uof0_integerfield");
                }
                this.getView().updateView("uof0_integerfield");
                this.getView().updateView("uof0_textfield");
            }
        }

    }
}