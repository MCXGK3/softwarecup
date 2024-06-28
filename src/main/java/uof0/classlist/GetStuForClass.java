package uof0.classlist;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.datamodel.events.BeforePackageDataEvent;
import kd.bos.entity.list.IListDataProvider;
import kd.bos.form.events.BeforeCreateListDataProviderArgs;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.mvc.list.ListDataProvider;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 标准单据列表插件
 */
public class GetStuForClass extends AbstractListPlugin implements Plugin {

    private Object getClassPK(){
        return this.getView().getFormShowParameter().getCustomParam("classPK");
    }

    @Override
    public void beforeCreateListDataProvider(BeforeCreateListDataProviderArgs args) {
        args.setListDataProvider(new MyDataProvider(getClassPK()));
        super.beforeCreateListDataProvider(args);
    }

    @Override
    public void beforePackageData(BeforePackageDataEvent e) {
        super.beforePackageData(e);

    }

    @Override
    public void setFilter(SetFilterEvent e) {
        List<QFilter> filters = e.getQFilters();
        DynamicObject aClass = BusinessDataServiceHelper.loadSingle(getClassPK(), "uof0_course_class");
        DynamicObjectCollection students = aClass.getDynamicObjectCollection("uof0_joinstudents");
        List<String> ids=new ArrayList<>();
        for(DynamicObject student:students){
            ids.add(student.getDynamicObject("fbasedataid").getString("uof0_userfield.number"));
        }
        filters.add(new QFilter("uof0_userfield.number", QCP.in,ids));
        super.setFilter(e);
    }
}

class MyDataProvider extends ListDataProvider {
    Object classPK;
    public MyDataProvider(Object classPK){this.classPK=classPK;}
    @Override
    public DynamicObjectCollection getData(int start, int limit) {
        DynamicObjectCollection objects = super.getData(start, limit);
        return objects;
    }
}
