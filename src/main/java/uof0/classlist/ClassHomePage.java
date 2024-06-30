package uof0.classlist;

import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.container.Tab;
import kd.bos.form.control.Label;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.list.ListShowParameter;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态表单插件
 */
public class ClassHomePage extends AbstractFormPlugin implements Plugin {

    private boolean isteacher(){
        Object isTeacher = this.getView().getFormShowParameter().getCustomParam("isTeacher");
        if(isTeacher==null) return false;
        else{
            return (boolean) isTeacher;
        }
    }
    private Object getClassPK(){
        Object classPK = this.getView().getFormShowParameter().getCustomParam("classPK");
        return classPK;
    }

    private DynamicObject getClassEntity(){
        Object classPK=getClassPK();
        if(classPK==null) return null;
        else{
            return BusinessDataServiceHelper.loadSingle(classPK,"uof0_course_class");
        }
    }

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
    }

    private void openHomeworkPage(boolean exam){
        ListShowParameter homework=new ListShowParameter();
        homework.getOpenStyle().setShowType(ShowType.NewTabPage);
        homework.getOpenStyle().setTargetKey("uof0__submaintab_");
        homework.setFormId("bos_list");
        if(isteacher()) homework.setBillFormId("uof0_hw_view_for_tea");
        else homework.setBillFormId("uof0_hw_view_for_stu");
        homework.setCustomParam("classPK", getClassPK());
        homework.setCustomParam("exam",exam);
        homework.setCustomParam("isTeacher",isteacher());
        homework.setCaption("作业列表");
        if(exam) homework.setCaption("考试列表");

        this.getView().showForm(homework);
        this.getView().updateView("uof0__submaintab_");
        this.getPageCache().put("homeworkPage",homework.getPageId());
    }

    private void openDataPage(){
        BillShowParameter data=new BillShowParameter();
        data.getOpenStyle().setShowType(ShowType.NewTabPage);
        data.getOpenStyle().setTargetKey("uof0__submaintab_");
        data.setFormId("uof0_class_data_atta");
        DynamicObject classEntity=getClassEntity();
        DynamicObject dataEntity;
        if(classEntity!=null){
            dataEntity=BusinessDataServiceHelper.loadSingle("uof0_class_data_atta",new QFilter[]{new QFilter("uof0_textfield", QCP.equals,classEntity.getString("number"))});
        }
        else {
            dataEntity=BusinessDataServiceHelper.loadSingle("uof0_class_data_atta",new QFilter[0]);
        }
        if(dataEntity==null){
            dataEntity=BusinessDataServiceHelper.newDynamicObject("uof0_class_data_atta");
            dataEntity.set("uof0_textfield",classEntity==null?"":classEntity.getString("number"));
            SaveServiceHelper.save(new DynamicObject[]{dataEntity});
        }
        data.setPkId(dataEntity.getPkValue());
        data.setCustomParam("isTeacher",isteacher());
        data.setCustomParam("classPK", getClassPK());
        data.setCaption("资料");
        data.setStatus(OperationStatus.VIEW);
        this.getView().showForm(data);
        this.getView().updateView("uof0__submaintab_");
        this.getPageCache().put("dataPage",data.getPageId());

    }
    private void openSignin(){
        BillShowParameter signin=new BillShowParameter();
        signin.getOpenStyle().setShowType(ShowType.NewTabPage);
        signin.getOpenStyle().setTargetKey("uof0__submaintab_");
        signin.setFormId("uof0_newsignin");
        DynamicObject classEntity=getClassEntity();
        DynamicObject dataEntity;
        signin.setCustomParam("isTeacher",isteacher());
        signin.setCustomParam("classPK", getClassPK());
        signin.setCaption("发起签到");
        signin.setStatus(OperationStatus.ADDNEW);
        this.getView().showForm(signin);
        this.getView().updateView("uof0__submaintab_");
        this.getPageCache().put("signinPage",signin.getPageId());
    }
    private void openSchedule(){
        FormShowParameter schedule=new FormShowParameter();
        schedule.getOpenStyle().setShowType(ShowType.NewTabPage);
        schedule.getOpenStyle().setTargetKey("uof0__submaintab_");
        schedule.setFormId("uof0_classtbl");
        schedule.setCustomParam("single",true);
        schedule.setCustomParam("classPK",getClassPK());
        schedule.setCaption("课程表");
        this.getView().showForm(schedule);
        this.getView().updateView("uof0__submaintab_");
        this.getPageCache().put("schedulePage",schedule.getPageId());


    }
    private void openDiscuss(){
        FormShowParameter discuss=new FormShowParameter();
        discuss.getOpenStyle().setShowType(ShowType.NewTabPage);
        discuss.getOpenStyle().setTargetKey("uof0__submaintab_");
        discuss.setFormId("uof0_commentplug");
        discuss.setCustomParam("classPK",getClassPK());
        discuss.setCustomParam("isTeacher",isteacher());
        discuss.setCaption("课堂评论");
        this.getView().showForm(discuss);
        this.getView().updateView("uof0__submaintab_");
        this.getPageCache().put("discussPage",discuss.getPageId());
    }

    private  void openStuList(){
        ListShowParameter stulist=new ListShowParameter();
        stulist.getOpenStyle().setShowType(ShowType.NewTabPage);
        stulist.getOpenStyle().setTargetKey("uof0__submaintab_");
        stulist.setFormId("bos_list");
        stulist.setBillFormId("uof0_class_stulist");
        stulist.setCustomParam("classPK", getClassPK());
        stulist.setCustomParam("isTeacher",isteacher());
        stulist.setCaption("学生列表");
        this.getView().showForm(stulist);
        this.getView().updateView("uof0__submaintab_");
        this.getPageCache().put("stulistPage",stulist.getPageId());
    }
    private void openSigninList(){
        ListShowParameter signinlist=new ListShowParameter();
        signinlist.getOpenStyle().setShowType(ShowType.NewTabPage);
        signinlist.getOpenStyle().setTargetKey("uof0__submaintab_");
        signinlist.setFormId("bos_list");
        signinlist.setBillFormId("uof0_newsignin");
        signinlist.setCustomParam("classPK", getClassPK());
        signinlist.setCustomParam("isTeacher",isteacher());
        signinlist.setCaption("签到列表");
        this.getView().showForm(signinlist);
        this.getView().updateView("uof0__submaintab_");
        this.getPageCache().put("signinlistPage",signinlist.getPageId());
    }
    private  void openAnnouncement(){
        ListShowParameter annlist=new ListShowParameter();
        annlist.getOpenStyle().setShowType(ShowType.NewTabPage);
        annlist.getOpenStyle().setTargetKey("uof0__submaintab_");
        annlist.setFormId("bos_list");
        annlist.setBillFormId("uof0_class_announcement");
        annlist.setCustomParam("classPK", getClassPK());
        annlist.setCustomParam("isTeacher",isteacher());
        annlist.setCaption("公告列表");
        this.getView().showForm(annlist);
        this.getView().updateView("uof0__submaintab_");
        this.getPageCache().put("announcePage",annlist.getPageId());
    }


    private void prepareText(){
        Label name=this.getControl("uof0_labelap");
        Label teacher=this.getControl("uof0_labelap1");
        Object coursepk = this.getClassPK();
        if(coursepk!=null){
            DynamicObject entity= BusinessDataServiceHelper.loadSingle(coursepk,"uof0_course_class");
            name.setText(entity.getString("name"));
            teacher.setText(entity.getString("uof0_teacher.name"));
        }
    }

    private void prepareCard(){
        FormShowParameter ann = new FormShowParameter();
        ann.setFormId("uof0_class_last_ann");
        ann.getOpenStyle().setShowType(ShowType.InContainer);
        ann.getOpenStyle().setTargetKey("uof0_gridcontainerap");
        ann.setCustomParam("classPK",this.getView().getFormShowParameter().getCustomParam("classPK"));
        ann.setCustomParam("isTeacher",isteacher());
        Map<String,String> map = new HashMap<>();
        map.put("cardId","ltzVUk7uonvYargZTN");
        ann.getOpenStyle().setCustParam(map);
        this.getView().showForm(ann);
    }
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        prepareText();
        prepareCard();



        Tab tab=this.getView().getControl("uof0__submaintab_");
        tab.activeTab("uof0_appmiantab");

    }

    @Override
    public void afterDoOperation(AfterDoOperationEventArgs afterDoOperationEventArgs) {
        super.afterDoOperation(afterDoOperationEventArgs);
        String key = afterDoOperationEventArgs.getOperateKey();
        switch(key){
            case "homework":
                openHomeworkPage(false);
                break;
            case "examlist":
                openHomeworkPage(true);
                break;
            case "data":
                openDataPage();
                break;
            case "discuss":
                openDiscuss();
                break;
            case "schedule":
                openSchedule();
                break;
            case "studentlist":
                openStuList();;
                break;
            case "signin":
                openSignin();
                break;
            case "signinlist":
                openSigninList();
                break;
            case "announcement":
                openAnnouncement();
                break;
            case "scores":
//                openScores();
                break;
            default:
                break;
        }

    }

}