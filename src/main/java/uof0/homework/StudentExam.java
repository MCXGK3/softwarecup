package uof0.homework;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.ext.form.control.CountDown;
import kd.bos.ext.form.control.events.CountDownEvent;
import kd.bos.ext.form.control.events.CountDownListener;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.Date;
import java.util.EventObject;

/**
 * 动态表单插件
 */
public class StudentExam extends AbstractFormPlugin implements Plugin, CountDownListener {

    private boolean isExam(){
        if(!this.getView().getFormShowParameter().getCustomParams().containsKey("exam"))
            return false;
        return (boolean) this.getView().getFormShowParameter().getCustomParam("exam");
    }
    private DynamicObject getTeacherHomework(){
        DynamicObject entity;
        Object pk=this.getView().getFormShowParameter().getCustomParam("teacherEntity");
        if(pk!=null){
            entity= BusinessDataServiceHelper.loadSingle(pk,"uof0_tea_homework");
        }
        else {
            entity = BusinessDataServiceHelper.loadSingle("uof0_tea_homework", new QFilter[0]);
        }
        return entity;
    }
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        CountDown countDown=this.getView().getControl("uof0_countdownap");
        if(!isExam()||(boolean) this.getView().getFormShowParameter().getCustomParam("check")) this.getView().setVisible(false,"uof0_countdownap");
        else{
            DynamicObject teacherExam=getTeacherHomework();
            Date deadline=teacherExam.getDate("uof0_datetimefield");
            Date now=new Date();
            long time=deadline.getTime()- now.getTime();
            countDown.addCountDownListener(this);
            countDown.setDuration((int)(time/1000));
            countDown.start();
        }
    }

    @Override
    public void onCountDownEnd(CountDownEvent evt) {
        CountDownListener.super.onCountDownEnd(evt);
        Object source = evt.getSource();
        this.getView().invokeOperation("save");
        this.getView().invokeOperation("submit");
        this.getView().showMessage("考试已结束");

    }
}