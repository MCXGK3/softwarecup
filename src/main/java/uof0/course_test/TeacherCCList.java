package uof0.course_test;

import kd.bos.context.RequestContext;
import kd.bos.filter.FilterColumn;
import kd.bos.form.events.FilterContainerInitArgs;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.ksql.util.StringUtil;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.sdk.plugin.Plugin;

import java.util.List;

/**
 * 标准单据列表插件
 */
public class TeacherCCList extends AbstractListPlugin implements Plugin {
    @Override
    public void setFilter(SetFilterEvent e) {
        super.setFilter(e);
        String userId= RequestContext.get().getUserName();
        e.getQFilters().add(new QFilter("uof0_teacher", QCP.equals,userId));

    }

    public void filterContainerInit(FilterContainerInitArgs args) {
        super.filterContainerInit(args);
        List<FilterColumn> temp=args.getCommonFilterColumns();
        for(FilterColumn t:temp){
            if(StringUtil.equals("billstatus",t.getFieldName()))  {
                t.setDefaultValue("C");
            }
        }

    }

}