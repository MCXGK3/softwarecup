package uof0.signin;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.ExtendedDataEntitySet;
import kd.bos.entity.botp.plugin.AbstractConvertPlugIn;
import kd.bos.entity.botp.plugin.args.AfterCreateTargetEventArgs;
import kd.bos.entity.botp.plugin.args.BeforeBuildRowConditionEventArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.sdk.plugin.Plugin;

import java.util.Date;
import java.util.List;

/**
 * 单据转换插件
 */
public class SignInConvert extends AbstractConvertPlugIn implements Plugin {
    @Override
    public void beforeBuildRowCondition(BeforeBuildRowConditionEventArgs e) {
        super.beforeBuildRowCondition(e);
        List<QFilter> qFilters = e.getCustQFilters();
        Date now=new Date();
        qFilters.add(new QFilter("uof0_endtime", QCP.less_than,now));
    }


    @Override
    public void afterCreateTarget(AfterCreateTargetEventArgs e) {
        super.afterCreateTarget(e);
        ExtendedDataEntitySet entitySet = e.getTargetExtDataEntitySet();
        List<ExtendedDataEntity> signins = entitySet.getExtDataEntityMap().get("uof0_signin");
        for(ExtendedDataEntity signin:signins) {
            DynamicObject entity = signin.getDataEntity();
//            entity.set("uof0_curposition","123");
            entity.set("uof0_studentname",entity.getDynamicObject("creator").getString("name"));
            }
        }

    }
