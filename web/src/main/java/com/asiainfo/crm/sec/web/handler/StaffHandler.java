package com.asiainfo.crm.sec.web.handler;

import com.asiainfo.bits.web.framework.HttpHandler;
import com.asiainfo.bits.web.framework.Visit;
import net.sf.json.JSONArray;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffHandler extends HttpHandler {

    private static transient final Logger log = LoggerFactory.getLogger(StaffHandler.class);

    /**
     * 根据操作员标识，获取全部登录部门列表
     * @throws Exception
     */
    public void getOperatorAllLoginOrgs() throws Exception{
        Visit visit = getVisit();
        if( null == visit || !visit.isValidate() )
            return;

        String operatorId = visit.getOperatorId();
        if(operatorId == null || "".equals(operatorId))
            return;

        Map<String, Object> input = new HashMap();
        input.put("OPERATOR_ID", operatorId);

        Map<String, Object> output = null; //CenterClient.call("sec.ISecQueryIntfCSV.queryOperatorAllLoginOrgs", input);

        List<Map<String, Object>> resultList = (List<Map<String, Object>>) output.get("X_RESULT_LIST");

        List<Map<String, Object>> ret = new ArrayList<>();
        if(null != resultList && resultList.size() > 0 ){
            for(Map<String, Object> resultItem : resultList){
                Map<String, Object> item = new HashMap<>();
                item.put("CHANNEL_CODE", ObjectUtils.toString(resultItem.get("CHANNEL_CODE")));
                item.put("ORGANIZE_ID", ObjectUtils.toString(resultItem.get("ORGANIZE_ID")));
                item.put("ORGANIZE_NAME", ObjectUtils.toString(resultItem.get("ORGANIZE_NAME")));
                ret.add(item);
            }
        }

        setAjax(JSONArray.fromObject(ret));
    }

    public void changeLoginOrganize() throws Exception{
        Visit visit = getVisit();
        if( null == visit || !visit.isValidate() )
            return;

        String operatorId = visit.getOperatorId();
        if(operatorId == null || "".equals(operatorId))
            return;

        String organizeId = getParameter("ORGANIZE_ID");
        if(organizeId == null || "".equals(organizeId))
            return;

        Map<String, Object> input = new HashMap();
        input.put("OPERATOR_ID", operatorId);
        input.put("ORGANIZE_ID", organizeId);

        Map<String, Object> output = null; //CenterClient.call( "sec.ISecOperatorIntfCSV.verificationOperatorNoPassword", input);

        List<Map<String, Object>> resultList = (List<Map<String, Object>>) output.get("X_RESULT_LIST");
        Map<String, Object> result  = resultList != null ? resultList.get(0) : null;

        // 切换组织
        visit.setOrganizeId((String)result.get("ORGANIZE_ID"));
        visit.setOrganizeName((String)result.get("ORGANIZE_NAME"));

        // 切换登录地州和登录县市
        visit.setDistrictId((String)result.get("DISTRICT_ID"));
        visit.setDistrictName((String)result.get("DISTRICT_NAME"));
        visit.setCountyId((String)result.get("COUNTY_ID"));
        visit.setCountyName((String)result.get("COUNTY_NAME"));
    }

}
