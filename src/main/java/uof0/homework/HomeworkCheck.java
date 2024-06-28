package uof0.homework;

import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.AfterOperationArgs;
import kd.bos.entity.plugin.args.BeforeOperationArgs;
import kd.bos.form.FormShowParameter;
import kd.bos.form.OpenStyle;
import kd.bos.form.ShowType;
import kd.bos.mvc.SessionManager;
import kd.mmc.pdm.common.util.FormViewUtil;
import kd.sdk.plugin.Plugin;

/**
 * 单据操作插件
 */
public class HomeworkCheck extends AbstractOperationServicePlugIn implements Plugin {


    @Override
    public void afterExecuteOperationTransaction(AfterOperationArgs e) {
        super.afterExecuteOperationTransaction(e);
        DynamicObject[] dataEntities1 = e.getDataEntities();
        this.getOption().setVariableValue("studentHomeworkNO",dataEntities1[0].getString("billno"));
    }
}