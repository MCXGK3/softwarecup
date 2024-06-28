package uof0.classlist;

import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.list.events.ListRowClickEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.sdk.plugin.Plugin;

/**
 * 标准单据列表插件
 */
public class GetClasslistForTea extends AbstractListPlugin implements Plugin {
    private void OpenHomePage(Object classPK){
        FormShowParameter homepage=new FormShowParameter();
        homepage.setCaption("课堂主页");
        homepage.getOpenStyle().setShowType(ShowType.MainNewTabPage);
        homepage.setFormId("uof0_class_homepage1");
        homepage.setCustomParam("isTeacher",true);
        homepage.setCustomParam("classPK",classPK);
        this.getView().showForm(homepage);
    }

    @Override
    public void listRowClick(ListRowClickEvent evt) {
        super.listRowClick(evt);
        int row=evt.getRow();
        if(row>=0) {
            Object classPK = this.getCurrentListAllRowCollection().get(row).getPrimaryKeyValue();
            this.OpenHomePage(classPK);
        }
    }
}