package com.asiainfo.crm.base.web.handler;

import com.ailk.common.util.Utility;
import com.asiainfo.bits.web.framework.HttpHandler;
import com.asiainfo.bits.web.framework.Visit;
import com.asiainfo.crm.base.web.config.MenuProperties;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component("com.asiainfo.crm.sec.web.handler.MenuHandler")
public class MenuHandler extends HttpHandler {

    private static transient final Logger log = LoggerFactory.getLogger(MenuHandler.class);

    private static transient final String DOMAIN_SEARCH_CODE = "SEC_DOMAIN";
    private static transient final String FUNCTION_SEARCH_CODE = "SEC_FUNCTION";
    private static transient final String HIGHT_LIGHT_STYLE_HTML = "<span class=\"e_red\">{title}</span>";

    private static MenuHandler menuHandler;

    @Autowired
    public MenuProperties props;

    @PostConstruct
    public void init() {
        menuHandler = this;
        menuHandler.props = this.props;
    }

    /**
     * 菜单排序Comparetor
     */
    class MenuComparetor implements Comparator<Map>{
        @Override
        public int compare(Map map, Map t1) {
            String seq = ObjectUtils.toString(map.get("FUN_SEQ"));
            String seq1 = ObjectUtils.toString(t1.get("FUN_SEQ"));

            if(StringUtils.isNotBlank(seq) && StringUtils.isNotBlank(seq1)
                    && StringUtils.isNumeric(seq) && StringUtils.isNumeric(seq1)){

                int intSeq = Integer.parseInt(seq);
                int intSeq1 = Integer.parseInt(seq1);

                if(intSeq > intSeq1)
                    return 1;
                else if(intSeq < intSeq1)
                    return -1;
                else
                    return 0;
            }

            return 0;
        }
    }

    /**
     *  搜索菜单排序Comparetor
     */
    class SearchMenuComparetor implements  Comparator<Map>{
        @Override
        public int compare(Map map, Map t1) {
            String idx = ObjectUtils.toString(map.get("KEY_IDX"));
            String idx1 = ObjectUtils.toString(t1.get("KEY_IDX"));

            if (StringUtils.isNotBlank(idx) && StringUtils.isNotBlank(idx1)
                    && StringUtils.isNumeric(idx) && StringUtils.isNumeric(idx1)){

                int intIdx = Integer.parseInt(idx);
                int intIdx1 = Integer.parseInt(idx1);

                if(intIdx > intIdx1)
                    return 1;
                else if(intIdx < intIdx1)
                    return -1;
                else
                    return 0;
            }

            return 0;
        }
    }

    private List<Map> processDomains(List<Map> domains) throws Exception{
        if(null == domains)
            return null;

        Visit visit = getVisit();
        if( null == visit || !visit.isValidate() ) {
            if(log.isDebugEnabled()){
                log.debug("visit is null or not validate");
            }
            return null;
        }

        if(domains != null && domains.size() > 0) {
            Map domain;
            String domainId = null;

            /**
             * 遍历并获取domainId集合
             */
            List<String> domainIds = new ArrayList<String>();
            for(int i = domains.size() - 1;i >= 0; i--){
                domain = domains.get(i);
                domainId = ObjectUtils.toString(domain.get("DOMAIN_ID"));
                if( null != domainId && !"".equals(domainId.trim()) ) {
                    domainIds.add(domainId);
                }
            }

            Set<String> domainRighs = null; //PrivCheck.hasPrivList(visit.getOperatorId(), visit.getOrganizeId(), "D", domainIds);
            if( null == domainRighs || domainRighs.size() <= 0)
                return null;

            /**
             * 遍历并过滤domain集合
             */
            for(int i = domains.size() - 1;i >= 0; i--){
                domain = domains.get(i);
                domainId = ObjectUtils.toString(domain.get("DOMAIN_ID"));
                if( null != domainId && !"".equals(domainId.trim())
                        &&  !domainRighs.contains(domainId)) {
                    domains.remove(i);
                }
            }
        }

        return domains;
    }

    private List<Map> processMenus(List<Map> menus) throws Exception{
        if(null == menus)
            return null;

        Visit visit = getVisit();
        if( null == visit || !visit.isValidate() ) {
            if(log.isDebugEnabled()){
                log.debug("visit is null or not validate");
            }
            return null;
        }


        if(menus != null && menus.size() > 0) {
            Map menu;
            String funcCode = null, funcLevel = null, dllPath = null;

            /**
             * 遍历并获取funcCode集合
             */
            List<String> funcCodes = new ArrayList<String>();
            for(int i = menus.size() - 1;i >= 0; i--){
                menu = menus.get(i);
                funcCode = ObjectUtils.toString(menu.get("FUNC_CODE"));
                if( null != funcCode && !"".equals(funcCode.trim())) {
                    funcCodes.add(funcCode);
                }
            }

            Set<String> menuRights = null; //PrivCheck.hasPrivList(visit.getOperatorId(), visit.getOrganizeId(), "F", funcCodes);
            if( null == menuRights || menuRights.size() <= 0 )
                return null;

            /**
             * 遍历并鉴权
             */
            for(int i = menus.size() - 1;i >= 0; i--){
                menu = menus.get(i);
                funcCode = ObjectUtils.toString(menu.get("FUNC_CODE"));
                funcLevel = ObjectUtils.toString(menu.get("FUNC_LEVEL"));
                dllPath = ObjectUtils.toString(menu.get("DLL_PATH"));

                if( null != funcCode && !"".equals(funcCode.trim())
                        //&& null != dllPath && !"".equals(dllPath.trim())
                        //&& StringUtils.isNumeric(funcLevel) && Integer.parseInt(funcLevel) > 3
                        && !menuRights.contains(funcCode)) {
                    menus.remove(i);
                }
            }
        }

        return menus;
    }

    /**
     * 加载Domain
     * @throws Exception
     */
    public void loadL1Menu() throws Exception {
        Map<String,String> sp = new HashMap<String,String>();
        sp.put("DOMAIN_LEVEL", "1");

        /*
        SearchResponse result = SearchClient.search("SEC_DOMAIN", null, sp, 0, 1000);
        List<Map> list = result != null ? result.getDatas() : null;
        list = processDomains(list);
        setAjax(JSONArray.fromObject(list));
        */
    }

    /*
     * 第一级菜单
     */
    public void loadL2Menu() throws Exception{

        String domainId = getParameter("DOMAIN_ID");
        if(domainId == null || "".equals(domainId)){
            Utility.error("DOMAIN_ID cannot be empty");
            return;
        }

        Map<String,String> sp = new HashMap<String,String>();
        sp.put("DOMAIN_ID", domainId);
        sp.put("PARENT_ID", "-1");

        /*
        SearchResponse result = SearchClient.search("SEC_FUNCTION", null, sp, 0, 1000);
        List<Map> list = result != null ? result.getDatas() : null;
        list = processMenus(list);   //过滤2级菜单
        if(list != null) {
            list.sort(new MenuComparetor());
        }

        setAjax(JSONArray.fromObject(list));
        */

    }

    /*
     * 第二级菜单
     */
    public void loadL3Menu() throws Exception{
        String parentId = getParameter("PARENT_ID");
        if(parentId == null || "".equals(parentId)){
            Utility.error("PARENT_ID cannot be empty");
            return;
        }

        Map<String,String> sp = new HashMap<String,String>();
        sp.put("PARENT_ID", parentId);

        /*
        SearchResponse result = SearchClient.search("SEC_FUNCTION", null, sp, 0, 1000);
        List<Map> list = result != null ? result.getDatas() : null;
        list = processMenus(list); //过滤3级菜单
        if(list != null) {
            list.sort(new MenuComparetor());

            //三级四级一起查询出来
            int size = list.size();
            String funcId;
            List<Map> subList = null;
            for(int i = 0; i < size; i ++){
                Map<String, Object> item = list.get(i);
                funcId = (String)item.get("FUNC_CODE");

                sp = new HashMap<String,String>();
                sp.put("PARENT_ID", funcId);

                result = SearchClient.search("SEC_FUNCTION", null, sp, 0, 1000);
                subList = result != null ? result.getDatas() : null;
                subList = processMenus(subList); //过滤叶子菜单
                if(subList != null) {
                    subList.sort(new MenuComparetor());
                    item.put("CHILD_FUNC_LIST", subList);
                }
            }

        }

        setAjax(JSONArray.fromObject(list));
        */
    }


    /**
     * 对搜索结果按关键字的位置排序并对标题进行关键字高亮处理
     * @param menus
     * @param q
     * @return
     * @throws Exception
     */
    private List<Map> processSearchMenus(List<Map> menus, String q) throws Exception{
        if(null == menus)
            return null;

        String[] keys = q.split("\\s{1,}");
        Map<String, Object> menu = null;
        String menuTitle, hightlightTitle;
        int keyIndex = 0, keyLength = 0, n = 0;
        int fixLength = HIGHT_LIGHT_STYLE_HTML.length() - "{title}".length();
        for(int i = 0, size = menus.size(); i < size; i++){
            keyIndex = 0;
            n = 0;
            menu = menus.get(i);
            hightlightTitle = menuTitle = (String)menu.get("VIEWNAME");
            if(!StringUtils.isEmpty(menuTitle)) {
                for (String key : keys) {
                    keyLength = key.length();
                    int hitIndex = menuTitle.toUpperCase().indexOf(key.toUpperCase());
                    if(hitIndex > -1) {
                        keyIndex = hitIndex;
                        String highLightText = StringUtils.replace(HIGHT_LIGHT_STYLE_HTML, "{title}", menuTitle.substring(keyIndex, keyIndex + keyLength));
                        hightlightTitle = new StringBuffer(hightlightTitle).replace(keyIndex + n * fixLength, keyIndex + keyLength +  n * fixLength, highLightText).toString();
                        n ++;
                    }
                }
            }
            menu.put("KEY_IDX", keyIndex);
            menu.put("HIGHLIGHT_TITLE", hightlightTitle);
        }

        //排序
        menus.sort(new SearchMenuComparetor());

        return menus;
    }

    public void searchMenu() throws Exception{

        List<Map> list = null;

        String q = getParameter("q");
        if(q != null && !"".equals(q)){

            //进行菜单搜索
            Map<String, String> sp = new HashMap<String,String>();
            sp.put("HAS_ADDR", "TRUE");

            if( log.isDebugEnabled() ){
                log.debug("q-----" + q);
                log.debug("sp-----" + sp);
            }

            /*
            SearchResponse result = SearchClient.search(FUNCTION_SEARCH_CODE, q, sp, 0, 1000);
            list = result != null ? result.getDatas() : null;
            list = processMenus(list);
            if(list != null) {
                list = processSearchMenus(list, q);
            }
            */
        }

        setAjax(JSONArray.fromObject(list));

    }


    /**
     * 加载快捷菜单配置
     * @throws Exception
     */
    public void loadShortcuts() throws Exception{

        Set<Map<String, Object>> shortcutSet = (null != menuHandler && null != menuHandler.props ? menuHandler.props.getShortcuts() : null);
        if(shortcutSet == null || shortcutSet.size() <= 0 )
            return;

        Visit visit = getVisit();
        if( null == visit || !visit.isValidate() ) {
            if(log.isDebugEnabled()){
                log.debug("visit is null or not validate");
            }
            return;
        }

        List<Map<String, Object>> ret = new ArrayList<>();

        List<String> funcCodes = new ArrayList<>();
        for(Map<String, Object> shortcut : shortcutSet){
            Map<String, Object> data = new HashMap<>();

            String rightCode = (String)shortcut.get("rightcode");
            if( null != rightCode && !"".equals(rightCode.trim()))
                funcCodes.add(rightCode);
        }

        Set<String> menuRights = null; //PrivCheck.hasPrivList(visit.getOperatorId(), visit.getOrganizeId(), "F", funcCodes);
        if( null == menuRights || menuRights.size() <= 0 )
            return;

        int i = 0;
        for(Map<String, Object> shortcut : shortcutSet){
            Map<String, Object> data = new HashMap<>();

            String name = (String)shortcut.get("name");
            String url = (String)shortcut.get("url");
            String className = (String)shortcut.get("class");
            String menuPath = (String)shortcut.get("path");
            String menuId = (String)shortcut.get("menuid");
            String rightCode = (String)shortcut.get("rightcode");

            data.put("NAME", name);
            data.put("URL", url);
            data.put("CLASS", className);

            if(null != menuRights && menuRights.contains(rightCode))
                ret.add(data);

            i ++;
        }

        setAjax(JSONArray.fromObject(ret));
    }

}
