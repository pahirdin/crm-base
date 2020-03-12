package com.asiainfo.crm.sec.web.cache;

import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.asiainfo.crm.sec.web.privchecker.data.ParamMap;
import com.asiainfo.crm.sec.web.privchecker.data.ParamRightMap;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PagePrivCache extends AbstractReadOnlyCache {

    private transient static final Logger log = LoggerFactory.getLogger(PagePrivCache.class);

    @Override
    public Map<String, Object> loadData() throws Exception {
        Map<String, Object> ret = new HashMap<>();

        List<Map> list = null;
        try {
            Map<String, String> sp = new HashMap<String, String>();
            sp.put("HAS_ADDR", "TRUE");
            //SearchResponse result = SearchClient.search("SEC_FUNCTION", null, sp, 0, 20000);
            list = null; //result.getDatas();
        }catch(Exception ex){
            log.error("PagePrivCache.loadData", ex);
        }

        if( null == list )
            return ret;

        Map<String, Set<String>> pageRights = new HashMap<String, Set<String>>();
        Map< String, Map< Map<String, String>, Set<String>> > pageParamRights = new HashMap< String, Map<Map<String, String>, Set<String>> >();

        String funcCode, menuPath;
        for(Map<String, Object> item : list){
            funcCode = ObjectUtils.toString(item.get("FUNC_CODE"));
            menuPath = ObjectUtils.toString(item.get("DLL_PATH"));

            if( null == menuPath || "".equals(menuPath.trim()) )
                continue;
            if( null == funcCode || "".equals(funcCode.trim()) )
                continue;

            int idx = menuPath.lastIndexOf("service=page/");
            if(idx < 0) continue;

            menuPath = menuPath.substring(idx, menuPath.length());
            Map<String, String> params = parseParams(menuPath);
            String service = params.get("service");
            if( service != null && !"".equals(service.trim()) ){
                params.remove("service");

                String pageName = service.substring(5, service.length());
                if( null == pageName || "".equals(pageName.trim()) )
                    continue;

                //if has other params add to pageParamRights
                if(params.size() > 0){
                    Map< Map<String, String>, Set<String> > rights = pageParamRights.get(pageName);
                    if(null == rights){
                        //使用TreeMap按键排序
                        rights = new ParamRightMap();
                        pageParamRights.put(pageName, rights);
                    }

                    //可能存在一个菜单地址对应多个权限编码的情况，需要使用HashSet
                    Set<String> paramsRightSet = rights.get(params);
                    if(null == paramsRightSet){
                        paramsRightSet = new HashSet<String>();
                        rights.put(params, paramsRightSet);
                    }
                    paramsRightSet.add(funcCode);
                }

                //add to pageRights
                Set<String> rightSet = pageRights.get(pageName);
                if(null == rightSet){
                    rightSet = new HashSet<String>();
                    pageRights.put(pageName, rightSet);
                }
                rightSet.add(funcCode);
            }
        }

        if(log.isDebugEnabled()){
            log.debug(">>>解析界面权限完毕，共解析" + (list != null ? list.size() : 0) + "条菜单数据, 解析结果：pageRights[" + pageRights.size() + "]条,pageParamRights[" + pageParamRights.size() + "]条");
        }

        ret.put("PAGE_RIGHTS", pageRights);
        ret.put("PAGE_PARAM_RIGHTS", pageParamRights);

        return ret;
    }

    private static Map<String,String> parseParams(String queryString){
        String[] queryStringSplit = queryString.split("&");
        Map<String, String> params = new ParamMap();

        for (String qs : queryStringSplit) {
            if(qs != null && !"".equals(qs)){
                int idx = qs.indexOf("=");
                if(idx > -1){
                    String name = qs.substring(0, idx);
                    String value = qs.substring(idx + 1, qs.length());

                    params.put(name, value);
                }
            }
        }
        return params;
    }
}
