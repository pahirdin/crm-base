package com.asiainfo.crm.sec.web.handler;

import com.ailk.common.util.Utility;
import com.asiainfo.bits.web.framework.HttpHandler;
import com.asiainfo.bits.web.framework.Visit;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FavoHandler extends HttpHandler {
    private static transient final Logger log = LoggerFactory.getLogger(FavoHandler.class);
    public void addFavoriteMenu() throws Exception {

        // 获取参数
        Visit visit = getVisit();
        String operatorId = visit.getOperatorId();
        String menuId = getParameter("MENU_ID");
        String favType = "1";

        // 拼接参数
        Map<String, Object> input = new HashMap();
        input.put("OPERATOR_ID", operatorId);
        input.put("FAV_TYPE", favType);
        input.put("FUNC_ID", menuId);

        // 调用接口
        Map<String, Object> output = null; //CenterClient.call( "sec.ISecOperatorIntfCSV.addSecMenuFav", input);

        if (log.isDebugEnabled()) {
            log.debug("★Add★");
            log.debug(output.toString());
            log.debug("☆");
        }

        // 拼接输出内容
//        List ret = new ArrayList();
//        List outputList = (List)output.get("X_RESULT_LIST");
//        for(int i=0; i<outputList.size(); i=i+1){
//            Map<String, Object> item = (Map)outputList.get(i);
//            Map<String, Object> itemObject = new HashMap();
//            itemObject.put("FUNC_ID", item.get("FUNC_ID"));
//            itemObject.put("MENU_FAV_ID", item.get("MENU_FAV_ID"));
//            ret.add(0, itemObject);
//        }
        setAjax(JSONObject.fromObject(output));
    }
    public void queryFavoriteMenu() throws Exception {

        // 获取参数
        Visit visit = getVisit();
        String operatorId = visit.getOperatorId();
        String favType = "1";
        Number state = 1;

        // 拼接参数
        Map<String, Object> input = new HashMap();
        input.put("OPERATOR_ID", operatorId);
        input.put("FAV_TYPE", favType);
        input.put("STATE", state);

        // 调用接口
        Map<String, Object> output = null; //CenterClient.call( "sec.ISecQueryIntfCSV.qrySecMenuFav", input);
        if (log.isDebugEnabled()) {
            log.debug("★Query★");
            log.debug(output.toString());
            log.debug("☆");
        }

        // 拼接输出内容
        List ret = new ArrayList();
        List outputList = (List)output.get("X_RESULT_LIST");
        for(int i=0; i<outputList.size(); i=i+1){
            Map<String, Object> item = (Map)outputList.get(i);
            Map<String, Object> itemObject = new HashMap();
            itemObject.put("FUNC_ID", item.get("FUNC_ID"));
            itemObject.put("NAME", item.get("NAME"));
            itemObject.put("DLL_PATH", item.get("DLL_PATH"));
            itemObject.put("MENU_FAV_ID", item.get("MENU_FAV_ID"));
            itemObject.put("FUNC_PATH", item.get("FUNC_PATH"));
            ret.add(0, itemObject);
        }
        setAjax(JSONArray.fromObject(ret));
    }
    public void removeFavoriteMenu() throws Exception {

        // 获取参数
        Visit visit = getVisit();
        String favId = getParameter("MENU_FAV_ID");
        String operatorId = visit.getOperatorId();

        // 拼接参数
        Map<String, Object> input = new HashMap();
        input.put("OPERATOR_ID", operatorId);
        input.put("MENU_FAV_ID", favId);

        // 调用接口
        Map<String, Object> output = null; //CenterClient.call( "sec.ISecOperatorIntfCSV.delSecMenuFav", input);
    }
}
