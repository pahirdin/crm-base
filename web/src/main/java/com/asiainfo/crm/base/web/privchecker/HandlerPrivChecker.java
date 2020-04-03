package com.asiainfo.crm.base.web.privchecker;

import com.ailk.web.view.VisitManager;
import com.ailk.web.view.safe.IPrivChecker;
import com.asiainfo.bits.web.framework.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * HttpHandler反向权限校验接口实现类
 */
public class HandlerPrivChecker implements IPrivChecker {
    private static final Logger log = LoggerFactory.getLogger(HandlerPrivChecker.class);

    @Override
    public boolean check(String className, Map<String, String[]> parameterMap) {
        String operatorId = null;
        String rightCode = null;
        String methodName = null;

        boolean check = true;
        try {

            Visit visit = (Visit) VisitManager.getVisit();
            if (null == visit)
                return check;

            operatorId = visit.getOperatorId();
            if (null == operatorId || operatorId.length() <= 0) {
                return true;
            }

            String[] method = parameterMap.get("method");
            if(method != null && method.length > 0){
                methodName = method[0];
            }

            if(null == method || "".equals(method))
                return check;

            /*
            IReadOnlyCache cache = null; //CacheFactory.getReadOnlyCache(AjaxPrivCache.class);
            if (null == cache) {
                return true;
            }
            rightCode = (String) cache.get(className + "@" + methodName);
            */

            if (null == rightCode || rightCode.length() <= 0)
                return check;

            check = true; //CheckPriv.checkFuncPermission(staffId, rightCode);
            return check;
        } catch (Exception e) {
            return true;
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(">>>AjaxHandler权限验证[key=" + className + "@" + methodName + "][operatorId=" + operatorId + "][rightcode=" + rightCode + "][result=" + check + "]");
            }
        }
    }
}
