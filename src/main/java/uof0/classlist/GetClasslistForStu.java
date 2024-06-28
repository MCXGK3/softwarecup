package uof0.classlist;

import com.kingdee.cosmic.ctrl.common.CtrlUtil;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.container.Container;
import kd.bos.form.control.events.RowClickEvent;
import kd.bos.form.control.events.RowClickEventListener;
import kd.bos.form.events.EntryHyperLinkClickEvent;
import kd.bos.list.CardListColumn;
import kd.bos.list.ListCardView;
import kd.bos.list.events.ListRowClickEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.list.plugin.ListViewPluginProxy;
import kd.bos.mvc.card.CardView;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;
import  kd.bos.form.control.Control;

import java.util.EventObject;

/**
 * 标准单据列表插件
 */
public class GetClasslistForStu extends AbstractListPlugin implements Plugin, RowClickEventListener {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Container cardTitle=this.getView().getControl("uof0_cardtitle");
        Container cardData=this.getView().getControl("uof0_carddata");
        Container cardLabel=this.getView().getControl("uof0_cardlabel");
        cardTitle.addClickListener(this);
        cardData.addClickListener(this);
        cardLabel.addClickListener(this);
        ListCardView  listCardView=this.getControl("cardview");
        this.getControl("uof0_cardviewrow");
        listCardView.addClickListener(this);

    }



    private void openHomePage(Object classPK){
        FormShowParameter homepage=new FormShowParameter();
        homepage.setCaption("课堂主页");
        homepage.getOpenStyle().setShowType(ShowType.MainNewTabPage);
        homepage.setFormId("uof0_class_homepage1");
        homepage.setCustomParam("isTeacher",false);
        homepage.setCustomParam("classPK",classPK);
        homepage.getOpenStyle().setTargetKey("_submaintab_");
        this.getView().showForm(homepage);
    }
    @Override
    public void listRowClick(ListRowClickEvent evt) {
        super.listRowClick(evt);
        int row = evt.getRow();
        if(row>=0) {
            Object xkdValue = this.getCurrentListAllRowCollection().get(row).getPrimaryKeyValue();
            DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle(xkdValue, "uof0_classaddreq");
            Object pkValue = dynamicObject.getDynamicObject("uof0_class").getPkValue();
            openHomePage(pkValue);
        }
    }
}