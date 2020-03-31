package com.asiainfo.crm.base.web.privchecker;

import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.web.view.VisitManager;
import com.ailk.web.view.safe.IPrivChecker;
import com.asiainfo.bits.web.framework.Visit;
import com.asiainfo.crm.base.web.cache.PagePrivCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 页面反向权限校验接口实现类
 */
public class PagePrivChecker implements IPrivChecker {

    private static final Logger log = LoggerFactory.getLogger(PagePrivChecker.class);

    @Override
    public boolean check(String pageName, Map<String, String[]> parameterMap) {
        String operatorId = null;
        Set<String> rightSet = null;
        String rightCodes = null;
        boolean check = true;

        try {
            if(pageName == null || "".equals(pageName))
                return check;

            if(parameterMap == null || parameterMap.size() == 0)
                return check;

            Visit visit = (Visit) VisitManager.getVisit();
            if (null == visit)
                return check;

            operatorId = visit.getOperatorId();
            if (null == operatorId || operatorId.length() <= 0) {
                return true;
            }

            IReadOnlyCache cache = null;
            try {
                cache = CacheFactory.getReadOnlyCache(PagePrivCache.class);
            } catch (Exception e) {
                log.error("加载页面权限缓存异常", e);
                cache = null;
            }

            if (null == cache) {
                return true;
            }

			Map<String, Map<Map<String, String>, Set<String>>> pageParamRights = (Map<String, Map<Map<String, String>, Set<String>>>) cache.get("PAGE_PARAM_RIGHTS");
			Map<String, Set<String>> pageRights  = (Map<String, Set<String>>) cache.get("PAGE_RIGHTS");

			if(pageParamRights != null){
				Map<Map<String, String>, Set<String>> rights = pageParamRights.get(pageName);
				if(rights != null){
					for(Map<String, String> modParams: rights.keySet()){
						boolean isCompara = true;
						for(String key : modParams.keySet()){
							if(!parameterMap.containsKey(key)
									|| !modParams.get(key).equals(getParameter(key, parameterMap))){
								isCompara = false;
							}
						}
						if(isCompara){
							rightSet = rights.get(modParams);
							break;
						}
					}
				}
			}

			if( null == rightSet){
				rightSet = pageRights.get(pageName);
			}

			if( null != rightSet){
				rightCodes = StringUtils.join(rightSet.toArray(), ",");

				List<String> rightList = new ArrayList<String>();
				rightList.addAll(rightSet);
                Set<String> result = null; //PrivCheck.hasFuncList(operatorId, visit.getOrganizeId(), rightList);
				check = result != null && result.size() > 0;
			}

            return check;
        } catch (Exception e) {
            return true;
        } finally {
            //DEBUG模式或check为false时输出信息
            if (log.isDebugEnabled() || false == check) {
                log.info(">>>页面权限验证[key=" + pageName + "][operatorId=" + operatorId + "][rightcode=" + rightCodes + "][result=" + check + "]");
            }
        }

    }

    private static String getParameter(String key, Map<String, String[]> parameterMap) {
        if (key == null || "".equals(key))
            return null;

        String[] vals = parameterMap.get(key);
        if (vals != null && vals.length > 0)
            return vals[0];

        return null;
    }
}
