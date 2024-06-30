package uof0.announcement;

import kd.bos.list.plugin.AbstractListPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 标准单据列表插件
 */
public class commonAnnList extends AbstractListPlugin implements Plugin {

    private boolean isTeacher(){
        if(!this.getView().getFormShowParameter().getCustomParams().containsKey("isTeacher")) return false;
        return (boolean) this.getView().getFormShowParameter().getCustomParam("isTeacher");
    }
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        if(!isTeacher()){
            this.getView().setVisible(false,"tblnew");
            this.getView().setVisible(false,"tbldel");
            this.getView().setVisible(false,"tblsubmit");

            this.getView().updateView("tblnew");
            this.getView().updateView("tbldel");
            this.getView().updateView("tblsubmit");
        }

    }
}