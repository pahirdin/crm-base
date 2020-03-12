package com.asiainfo.crm.sec.web.handler;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.util.Utility;
import com.ailk.web.util.BaseUtil;
import com.ailk.web.util.CookieUtil;
import com.asiainfo.bits.web.framework.HttpHandler;
import com.asiainfo.crm.sec.web.config.LoginProperties;
import com.wade.web.bind.annotation.PostRequest;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component("com.asiainfo.crm.sec.web.handler.LoginHandler")
public class LoginHandler extends HttpHandler {

    private static transient final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private static LoginHandler loginHandler;

    @Autowired
    public LoginProperties props;

    @PostConstruct
    public void postConstruct() {
        loginHandler = this;
        loginHandler.props = this.props;
    }

    @PostRequest
    public void loadConfig() throws Exception{
        //加载cookie中的用户名
        String cookieUserName = null, cookieLoginMode = null;
        CookieUtil cookie = new CookieUtil(getRequest(), getResponse(), "SEC_LOGIN_COOKIE", 24 * 7);
        if( cookie.load() ){
            cookieUserName = cookie.get("USER_NAME");
            //cookieLoginMode = cookie.get("LOGIN_MODE");
        }

        Map<String, Object> ret = new HashMap();
        if(cookieUserName != null && !"".equals(cookieUserName.trim())) {
            ret.put("USER_NAME", cookieUserName);
        }

        ret.put("ENABLE_VERIFYCODE", loginHandler.props.isEnableVerifycode());
        setAjax(JSONObject.fromObject(ret));
    }

    /**
     * 获取操作员登录信息
     * @throws Exception
     */
    @PostRequest
    public void getOperatorSecurityLimit() throws Exception{
        String loginCode = getParameter("LOGIN_CODE");
        if(loginCode == null | "".equals(loginCode.trim()))
            return;

        String loginIp = BaseUtil.getClientIP(getRequest());

        Map<String, Object> input = new HashMap();
        input.put("LOGIN_CODE", loginCode.trim());
        //input.put("LOGIN_IP", loginIp);
        //input.put("LOGIN_MAC", loginIp);

        //SECOND_AUTH: 0 - 密码登录, 1 - 短信码登录(仍然需要校验密码), 2 - 指纹识别
        Map<String, Object> output = null; //CenterClient.call("sec.ISecQueryIntfCSV.getOperatorSecurityLimit", input);
        String authType = (String)output.get("SECOND_AUTH");

        Map<String, Object> ret = new HashMap();
        ret.put("SECOND_AUTH", authType);
        if("2".equals(authType)) {
            List<Map<String, Object>> fingerIndexList = null;
            try{
                fingerIndexList = null; //StaticUtil.getStaticList("FINGER_INDEX");
            }catch(Exception ex){
                log.error("get fingerindex error:",  ex);
            }
            ret.put("FINGER_INDEX", fingerIndexList);
        }

        setAjax(JSONObject.fromObject(ret));

    }

    @PostRequest
    public void sendloginVerificationCode() throws Exception{
        String loginCode = getParameter("LOGIN_CODE");
        if(loginCode == null | "".equals(loginCode.trim()))
            return;

        HttpServletRequest request = getRequest();
        HttpSession session = request.getSession(false);
        if(null == session){
            session = request.getSession(true);
        }

        List<Map<String, Object>> resultList = null;
        Map<String, Object> result = null;

        Map<String, Object> input = new HashMap();
        input.put("LOGIN_CODE", loginCode.trim());
        Map<String, Object> output = null; //CenterClient.call("sec.ISecQueryIntfCSV.getOperatorLoginInfo", input);
        resultList = output != null ? (List<Map<String, Object>>) output.get("X_RESULT_LIST") : null;
        result = resultList != null ? resultList.get(0) : null;

        String operatorId = result != null ? (String)result.get("OPERATOR_ID") : null;
        if(null == operatorId || "".equals(operatorId.trim())){
            Utility.error("Faild to send login verification code");
            return;
        }

        if(loginHandler.props.isReloginLimit()) {
            Object obj = null;
            try {
                obj = null; //SharedCache.get(operatorId + "_AUTH_CODE_TIMESTAMP");
            } catch (Exception ex) {
                log.error("Faild to send login verification code", ex);
            }
            long timestamp = null != obj ? Long.parseLong(ObjectUtils.toString(obj)) : -1;
            long now = System.currentTimeMillis();
            if (timestamp > 0 && (now - timestamp) < 30 * 1000) {
                Utility.error("Operation is too frequent, please wait 30 seconds and try again");
            } else {
                try {
                    //SharedCache.set(operatorId + "_AUTH_CODE_TIMESTAMP", "" + now, 30);
                } catch (Exception ex) {
                    log.error("Faild to send login verification code", ex);
                }
            }
        }else {
            Object obj = session.getAttribute("AUTH_CODE_TIMESTAMP");
            long timestamp = null != obj ? Long.parseLong(ObjectUtils.toString(obj)) : -1;
            long now = System.currentTimeMillis();
            if (timestamp > 0 && (now - timestamp) < 30 * 1000) {
                Utility.error("Operation is too frequent, please wait 30 seconds and try again");
            } else {
                session.setAttribute("AUTH_CODE_TIMESTAMP", "" + now);
            }
        }

        String code = createValue();
        input.put("AUTH_CODE", code);

        //CenterClient.call("sec.ISecOperatorIntfCSV.sendloginVerificationCode", input);

        session.setAttribute("AUTH_CODE", code);
        //测试时返回值
        Map<String, Object> ret = new HashMap<>();
        setAjax(JSONObject.fromObject(ret));
    }


    private static final int IMAGE_WIDTH = 80;
    private static final int IMAGE_HEIGHT = 21;
    private static final int IMAGE_FONT_NUMBER = 4;

    private String createValue() {
        Random random = new Random();
        StringBuffer strbuf = new StringBuffer() ;
        String temp = "" ;
        int itmp = 0 ;
        for(int i=0 ; i < IMAGE_FONT_NUMBER ; i++){
            switch(random.nextInt(5)){
                case 1:     //生成A～Z的字母
                    itmp = random.nextInt(26) + 65 ;
                    if(itmp == 79) itmp = 48; //字母O替换为数字0
                    if (itmp == 73) itmp = 49; //字母I替换为数字1
                    temp = String.valueOf((char)itmp);
                    break;
                case 2:
                    //itmp = random.nextInt(26) + 97 ;
                    itmp = random.nextInt(26) + 65 ;
                    if(itmp == 79) itmp = 48; //字母O替换为数字0
                    if (itmp == 73) itmp = 49; //字母I替换为数字1
                    temp = String.valueOf((char)itmp);
                    break;
	            /*case 3:     //生成汉字
	                String[] rBase = {"0" , "1" , "2" , "3" , "4" , "5" , "6" , "7" ,
	                        "8" , "9" , "a" , "b" , "c" , "d" , "e" , "f" };
	                int r1 = random.nextInt(3)+11 ;     //生成第1位的区码
	                String strR1 = rBase[r1] ;      //生成11～14的随机数
	                int r2 ;        //生成第2位的区码
	                if(r1 == 13)
	                    r2 = random.nextInt(7) ;    //生成0～7的随机数
	                else
	                    r2 = random.nextInt(16) ;   //生成0～16的随机数
	                String strR2 = rBase[r2] ;
	                int r3 = random.nextInt(6) + 10 ;   //生成第1位的位码
	                String strR3 = rBase[r3] ;
	                int r4 ;        //生成第2位的位码
	                if(r3 == 10)
	                    r4 = random.nextInt(15) + 1;    //生成1～16的随机数
	                else if(r3 == 15)
	                    r4 = random.nextInt(15) ;       //生成0～15的随机数
	                else
	                    r4 = random.nextInt(16) ;       //生成0～16的随机数
	                String strR4 = rBase[r4] ;
	                //将生成的机内码转换成数字
	                byte[] bytes = new byte[2]   ;
	                String strR12 = strR1 + strR2 ;     //将生成的区码保存到字节数组的第1个元素中
	                int tempLow = Integer.parseInt(strR12, 16) ;
	                bytes[0] = (byte)tempLow;
	                String strR34 = strR3 + strR4 ;     //将生成的区码保存到字节数组的第2个元素中
	                int tempHigh = Integer.parseInt(strR34, 16) ;
	                bytes[1] = (byte)tempHigh;
	                temp = new String(bytes);           //根据字节数组生成汉字
	                break; */
                default:
                    itmp = random.nextInt(10) + 48 ;
                    temp = String.valueOf((char)itmp) ;
                    break;
            }

            strbuf.append(temp) ;
        }
        return strbuf.toString();
    }

    /**
     * 生成随机颜色
     * @param fc    前景色
     * @param bc    背景色
     * @return  Color对象，此Color对象是RGB形式的。
     */
    private Color getRandomColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 200;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 绘制干扰线
     * @param g Graphics2D对象，用来绘制图像
     * @param nums  干扰线的条数
     */
    private void drawRandomLines(Graphics2D g ,int lineCount){
        Random random = new Random();
        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(IMAGE_WIDTH);
            int ys = random.nextInt(IMAGE_HEIGHT);
            int xe = xs + random.nextInt(IMAGE_WIDTH/8);
            int ye = ys + random.nextInt(IMAGE_HEIGHT/8);
            g.drawLine(xs, ys, xe, ye);
        }
    }

    /**
     * 获取随机字符串，
     *      此函数可以产生由大小写字母，汉字，数字组成的字符串
     * @param length    随机字符串的长度
     * @return  随机字符串
     */
    private void drawRandomString(String value, Graphics2D g){
        for(int i = 0 ; i < value.length() ; i++){
            //Color color = new Color(20+random.nextInt(20) , 20+random.nextInt(20) ,20+random.nextInt(20) );
            g.setColor(Color.DARK_GRAY) ;

            //想文字旋转一定的角度
            // AffineTransform trans = new AffineTransform();
            // trans.rotate(random.nextInt(30)*3.14/180,15*i+8, 7) ;
            //缩放文字
            /*
            float scaleSize = random.nextFloat() + 0.8f ;
            if(scaleSize>1f)
              scaleSize = 1f ;
            trans.scale(scaleSize, scaleSize) ;
            */
            //g.setTransform(trans) ;
            //drawString(str,x,y)

            g.drawString((""+value.charAt(i)), 18*i+5, 16) ;
        }
    }

    private void writeImage(OutputStream stream, String value, String type) throws IOException {

        BufferedImage image =new BufferedImage(IMAGE_WIDTH , IMAGE_HEIGHT, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = image.createGraphics() ;


        //定义字体样式
        //Font myFont = new Font("宋体" , Font.PLAIN, 18) ;
        Font myFont = new Font("Arial" , Font.BOLD, 18) ;

        //设置字体
        g.setFont(myFont);
        //设置颜色
        //g.setColor(Color.BLACK);
        g.setColor(Color.WHITE);
        //g.setColor(getRandomColor(200 , 250)) ;
        //绘制背景
        g.fillRect(0, 0, IMAGE_WIDTH , IMAGE_HEIGHT);

        //g.setColor(getRandomColor(220 , 250)) ;
        g.setColor(Color.GRAY);

        drawRandomLines(g, 20) ;

        drawRandomString(value, g) ;

        g.dispose() ;

        ImageIO.write(image, "JPEG", stream) ;
    }


    public void verifyImage() throws Exception{
        HttpServletRequest hreq = getRequest();
        HttpServletResponse hres = getResponse();

        String value = createValue();

        HttpSession session = hreq.getSession(false);
        if (session == null) {
            session = hreq.getSession(true);
        }

        session.setAttribute(GlobalCfg.SESSION_VERIFY_CODE, value);
        /*
            hres.setHeader("Cache-Control", "no-cache");
            hres.setHeader("Pragma", "no-cache");
            hres.setHeader("Expires", "-1");
        */

        hres.setContentType("image/jpeg");

        writeImage(hres.getOutputStream(), value, "jpeg");
    }
}
