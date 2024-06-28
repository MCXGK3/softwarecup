package uof0.homework;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.form.operate.FormOperate;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.List;

/**
 * 标准单据列表插件
 */
public class GetStuHomeWorkForTea extends AbstractListPlugin implements Plugin {
    private String getNo(){
        return this.getView().getFormShowParameter().getCustomParam("homeworkNO");
    }
    @Override
    public void setFilter(SetFilterEvent e) {
        super.setFilter(e);
        String s = getNo();
        if(s!=null){
            List<QFilter> qFilters = e.getQFilters();
            qFilters.add(new QFilter("uof0_textfield", QCP.equals,s));
        }
    }


    @Override
    public void afterDoOperation(AfterDoOperationEventArgs afterDoOperationEventArgs) {
        super.afterDoOperation(afterDoOperationEventArgs);
        FormOperate source = (FormOperate) afterDoOperationEventArgs.getSource();
        source.getOption();
        String studentHomeworkNO = source.getOption().getVariableValue("studentHomeworkNO");
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("uof0_st_homework", new QFilter[]{
                new QFilter("billno", QCP.equals, studentHomeworkNO)
        });
        FormShowParameter check=new FormShowParameter();
        check.setFormId("uof0_tea_check_homework");
        check.getOpenStyle().setShowType(ShowType.Modal);
        check.setCustomParam("studentHomeworkPK",dynamicObject==null?null:dynamicObject.getPkValue());
        this.getView().showForm(check);
    }
}