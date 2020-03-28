package com.asiainfo.crm.sec.web.action.impl;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.RSAUtil;
import com.ailk.common.util.Utility;
import com.ailk.sna.SessionFactory;
import com.ailk.sna.config.SNACfg;
import com.ailk.web.util.BaseUtil;
import com.ailk.web.util.CookieUtil;
import com.ailk.web.view.VisitManager;
import com.asiainfo.bits.web.framework.Constants;
import com.asiainfo.bits.web.framework.Visit;
import com.asiainfo.bits.web.touchframe.action.ILoginAction;
import com.asiainfo.crm.sec.web.config.LoginProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("com.asiainfo.crm.sec.web.action.impl.LoginAction")
public class LoginAction implements ILoginAction {

    private static transient final Logger log = LoggerFactory.getLogger(LoginAction.class);
    private static LoginAction loginAction;

    @Autowired
    public LoginProperties loginProps;

    @PostConstruct
    public void postConstruct() {
        loginAction = this;
        loginAction.loginProps = this.loginProps;
    }

    @Override
    public void setLocale(HttpSession session, String locale) throws Exception {
        session.setAttribute(Constants.X_LOCALE, locale);
    }

    @Override
    public IData login(HttpServletRequest request, HttpServletResponse response, String username, String password, boolean sso) throws Exception {
        if(username != null) username = username.trim();
        if(password != null) password = password.trim();

        if(username == null || "".equals(username.trim())){
            Utility.abort("Username cannot be empty!");
            return null;
        }

        //安全校验码
        String securPIN = request.getParameter("SECUR_PIN");

        /*
        客户端网卡IP可能和x-forward-for获取的IP不一致，会导致请求校验不通过的问题，因此取IP统一使用BaseUtil.getClientIP
        String loginIp = request.getParameter("LOGIN_IP");
        if(loginIp == null || "".equals(loginIp.trim())){
            loginIp = BaseUtil.getClientIP(request);
        }
        */
        String loginIp = BaseUtil.getClientIP(request);
        String loginMac = request.getParameter("LOGIN_MAC");

        Map<String, Object> input = new HashMap();
        Map<String, Object> output = null;
        List<Map<String, Object>> resultList = null;
        Map<String, Object> result = null;

        input.put("LOGIN_CODE", username);

        output = null; //CenterClient.call("sec.ISecQueryIntfCSV.getOperatorSecurityLimit", input);
        String authType = (String)output.get("SECOND_AUTH");

        if(authType == null || "".equals(authType.trim())){
            Utility.abort("auth type cannot be empty!");
            return null;
        }

        //0 - 密码登录, 1 - 短信码登录, 2 - 指纹登录, 3 - 安全PIN码登录
        if( ("0".equals(authType) || "1".equals(authType) || "3".equals(authType)) && !sso ){
           if( StringUtils.isBlank(password) ) {
               Utility.abort("Password cannot be empty!");
               return null;
           }else{
               //解密 password
               password = RSAUtil.decryptByPrivateKey(password);
           }
        }

        if("0".equals(authType)) {
            if(loginAction.loginProps.isEnableVerifycode()) {
                String verifyCode = request.getParameter("VERIFY_CODE");
                if (StringUtils.isBlank(verifyCode)) {
                    Utility.abort("Verifycode cannot be empty!");
                    return null;
                }

                HttpSession session = request.getSession(false);
                Object sessionVerifyCode = null != session ? session.getAttribute(GlobalCfg.SESSION_VERIFY_CODE) : null;
                if (sessionVerifyCode == null) {
                    Utility.error("Illegal login!");
                    return null;
                }

                if (null != session) {
                    //删除验证码
                    session.removeAttribute(GlobalCfg.SESSION_VERIFY_CODE);
                    //删除图片验证码,session立即回存
                    if (SNACfg.REQUEST_TAG_VALUE.equals(request.getAttribute(SNACfg.REQUEST_TAG_NAME))) {
                        SessionFactory.getInstance().setSessionCache(session.getId(), (com.ailk.sna.HttpSession) session);
                        if (log.isDebugEnabled()) {
                            log.debug("Delete Session Verifycode, Store Session To SNA SessionCache");
                        }
                    }
                }

                if (!verifyCode.toUpperCase().equals(((String) sessionVerifyCode).toUpperCase())) {
                    Utility.error("Verifycode incorrect!");
                    return null;
                }

                //校验密码中的图片验证码
                if( !password.endsWith(verifyCode) ){
                    Utility.abort("Password incorrect!");
                    return null;
                }

                //移除密码中的图片验证码
                password = password.substring(0, password.length() - verifyCode.length());
            }
        }else if( "1".equals(authType) ){
            String authCode = request.getParameter("RANDOM_CODE");
            if( StringUtils.isBlank(authCode) ){
                Utility.abort("Randomcode cannot be empty!");
                return null;
            }

            HttpSession session = request.getSession(false);
            Object sesionAuthCode = null != session ? session.getAttribute("AUTH_CODE") : null;
            if(sesionAuthCode == null){
                Utility.error("Illegal login!");
                return null;
            }

            if(null != session){
                //删除短信验证码
                session.removeAttribute("AUTH_CODE");
                //删除短信验证码,session立即回存
                if(SNACfg.REQUEST_TAG_VALUE.equals(request.getAttribute(SNACfg.REQUEST_TAG_NAME))){
                    SessionFactory.getInstance().setSessionCache(session.getId(), (com.ailk.sna.HttpSession)session);
                    if(log.isDebugEnabled()){
                        log.debug("Delete Session AuthCode, Store Session To SNA SessionCache");
                    }
                }
            }

            if(!authCode.toUpperCase().equals(((String)sesionAuthCode).toUpperCase())){
                Utility.error("Randomcode incorrect!");
                return null;
            }

        }else if( "2".equals(authType) ){

            /*
            指纹验证接口：
            sec.ISecOperatorIntfCSV.bvsVerifyLogin
            入参：
            FINGER_INDEX ：手指序号
            LOGIN_CODE：登录编码
            NIC_INFO：指纹字符串
            出参：
            FINGER_INDEX=1,LOGIN_CODE=TESTPAK04,NIC_INFO=11
            X_RESULT_CODE 0表示成功，非0表示失败
            */
            /* 2019/11/26 不需要实现指纹登录，先注释

            String fingerIndex = request.getParameter("FINGER_INDEX");
            String nicInfo = request.getParameter("NIC_INFO");

            if(fingerIndex == null || "".equals(fingerIndex.trim())){
                Utility.error("Please select the finger to scan.");
            }

            if(nicInfo == null || "".equals(nicInfo.trim())){
                Utility.error("NIC info cannot be empty!");
            }

            input = new HashMap();
            input.put("LOGIN_CODE", username);
            input.put("FINGER_INDEX", fingerIndex);
            input.put("NIC_INFO", nicInfo);
            output = CenterClient.call("sec.ISecOperatorIntfCSV.bvsVerifyLogin", input);

            if( !"0".equals(output.get("X_RESULT_CODE")) ){
                Utility.error("Failure of Fingerprint Verification");
            }
            */

            Utility.error("Fingerprint login disabled");
        }else if("3".equals(authType)){
            if(null == securPIN || "".equals(securPIN.trim())){
                Utility.abort("SecurPIN cannot be empty!");
            }
        }

        HttpSession session = request.getSession(true);

        output = null; //CenterClient.call("sec.ISecQueryIntfCSV.getOperatorLoginInfo", input);
        resultList = output != null ? (List<Map<String, Object>>) output.get("X_RESULT_LIST") : null;
        result = resultList != null ? resultList.get(0) : null;

        //sec.ISecQueryIntfCSV.queryOperatorAllLoginOrgs
        String operatorId = result != null ? (String)result.get("OPERATOR_ID") : null;
        String organizId = result != null ? (String)result.get("ORGANIZE_ID") : null;
        String chgPasswdFlag = result != null ? (String)result.get("CHG_PASSWD_FLAG") : null;
        if("1".equals(chgPasswdFlag)){
            IData ret = new DataMap();
            ret.put("CHG_PASSWD_LOCATION", "/web-sec/?service=page/loginmgnt.ModifyPassword&listener=init&GO_URL=Y&OPERATOR_ID=" + operatorId);
            ret.put("OPERATOR_ID", operatorId);
            return ret;
        }

        input = new HashMap();
        input.put("OPERATOR_ID", operatorId);
        input.put("ORGANIZE_ID", organizId);
        input.put("PASSWORD", password);
        input.put("IP", loginIp);
        input.put("MAC", loginMac);
        input.put("SESSION_ID", session.getId());

        loginBefore(request, operatorId);

        if("2".equals(authType)){
            output = null; //CenterClient.call("sec.ISecOperatorIntfCSV.verificationOperatorNoPassword", input);
        } else {
            if("3".equals(authType)){
                input.put("IS_PORTAL", 1);
                input.put("SECUR_PIN", securPIN);
            }
            output = null; //CenterClient.call("sec.ISecOperatorIntfCSV.verificationOperatorPassword", input);
        }

        resultList = (List<Map<String, Object>>) output.get("X_RESULT_LIST");
        if(resultList == null || resultList.size() <= 0){
            Utility.abort("Failed to verify login information!");
        }

        result = resultList != null ? resultList.get(0) : null;

        if(log.isDebugEnabled()) {
            log.debug(">>>>result>>" + result);
        }

        String opCode = (String)result.get("CODE");

        Visit visit = new Visit();
        visit.setOperatorId(operatorId);
        visit.setOperatorCode(opCode);
        visit.setOperatorName((String)result.get("OPERATOR_NAME"));
        visit.setOrganizeId((String)result.get("ORGANIZE_ID"));
        visit.setOrganizeName((String)result.get("ORGANIZE_NAME"));

        //staffId
        visit.setStaffId((String)result.get("STAFF_ID"));
        //visit.setStaffType((String)result.get("STAFF_TYPE"));

        // 记录归属信息
        visit.setOperDistrictId((String)result.get("DISTRICT_ID"));
        visit.setOperDistrictName((String)result.get("DISTRICT_NAME"));
        visit.setOperCountyId((String)result.get("COUNTY_ID"));
        visit.setOperCountyName((String)result.get("COUNTY_NAME"));

        // 记录登录信息，切换部门时需要切换
        visit.setDistrictId((String)result.get("DISTRICT_ID"));
        visit.setDistrictName((String)result.get("DISTRICT_NAME"));
        visit.setCountyId((String)result.get("COUNTY_ID"));
        visit.setCountyName((String)result.get("COUNTY_NAME"));

        visit.setBillId((String)result.get("BILL_ID"));
        visit.setLoginChannel((String)result.get("LOGIN_CHANNEL"));
        visit.setValidate(true);
        visit.setInModeCode(Constants.CHANNEL_TYPE.CRM_WEB.getType());
        visit.setLoginIP(loginIp);

        VisitManager.setVisit(visit);
        VisitManager.createVisit(request);


        String wsid = session.getId();

        visit.setSessionId(wsid);

        if(SNACfg.REQUEST_TAG_VALUE.equals(request.getAttribute(SNACfg.REQUEST_TAG_NAME)))
            SessionFactory.getInstance().addOnLine(operatorId, wsid);

        //保存登录用户名信息到Cookie
        String cookieUserName = null;
        CookieUtil cookie = new CookieUtil(request, response, "SEC_LOGIN_COOKIE", 24 * 7);
        if( cookie.load() ){
            cookieUserName = cookie.get("USER_NAME");
        }
        if( null != username && !username.equals(cookieUserName) ){
            cookie.clear();
        }
        if ( !"".equals(username) ) {
            cookie.put("USER_NAME", username);
        }
        cookie.store();

        IData ret = new DataMap();
        ret.put("SESSIONID", session.getId());
        return ret;
    }

    private void loginBefore(HttpServletRequest request, String operatorId) throws Exception{
        if(operatorId  == null || "".equals(operatorId))
            Utility.error("OperatorId cannot be empty!");

        //SESSION会话校验,已经存在Session会话，则不允许重复登录
        boolean sessionCheck = loginAction.loginProps.isSessionLimit();
        if( sessionCheck ){ //&& SNACfg.REQUEST_TAG_VALUE.equals(getRequest().getAttribute(SNACfg.REQUEST_TAG_NAME))
            Visit visit = (Visit)VisitManager.getVisit();
            if(visit != null && visit.isValidate() && !"".equals(visit.getStaffId())){
                Utility.error("The current session already has a operatorId ["+ visit.getOperatorId() + "] to log in. It is not allowed to use this session to log in repeatedly, please open a separate browser window for login!");
                return;
            }
        }

        //重复登录校验
        boolean reloginCheck = loginAction.loginProps.isReloginLimit();
        Map info = null; //StaticUtil.getStaticData("RELOGIN_OPERATOR", operatorId);
        if(null != info && !info.isEmpty()){
            return;
        }

        if( reloginCheck && SNACfg.REQUEST_TAG_VALUE.equals(request.getAttribute(SNACfg.REQUEST_TAG_NAME)) ){
            Set<String> wsids= SessionFactory.getInstance().getOnLineWSIDSet(operatorId);
            if(wsids != null && wsids.size() > 0){
                for(String wsid : wsids){ //xiedx 2016/9/18  之前登陆的会话全部移除
                    HttpSession session = SessionFactory.getInstance().getSessionCache(wsid);
                    if(session != null){
                        Object obj = session.getAttribute(GlobalCfg.getVisitName());
                        if(obj != null){
                            Visit visit = (Visit)obj;
                            if(visit.isValidate()){
                                SessionFactory.getInstance().removeSessionCache(wsid);
                                SessionFactory.getInstance().removeSessionDataCache(wsid);
                                SessionFactory.getInstance().removeOnLine(operatorId, wsid);

                                //xiedx web重启后大量并发登录时会有性能问题，暂时注释该代码 xiedx 2014/01/02
                                //forceLogout(wsid, staffId, "您的工号 " + staffId + " 由于在IP: " + getLoginIP() + "进行登录，您已经被强制下线！");
                                //Utility.error("您的工号 " + staffId + " 已经在IP: " + bizvisit.getLoginIP() + " 登录，不允许重复登录！");
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    public IData logout(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(false);
        if (session != null) {

            String wsid = session.getId();

            IVisit visit = VisitManager.getVisit();
            String opId = visit.get(Constants.USER_INFO.OP_ID.name());

            visit.setValidate(false);

            /*
            visit.clear();
            session.invalidate();
            */

            if (SNACfg.REQUEST_TAG_VALUE.equals(request.getAttribute(SNACfg.REQUEST_TAG_NAME))) {
                SessionFactory.getInstance().removeOnLine(opId, wsid);
            }
        }

        return null;
    }

}
