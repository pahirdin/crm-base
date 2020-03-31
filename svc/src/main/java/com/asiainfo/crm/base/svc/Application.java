package com.asiainfo.crm.base.svc;

import com.ai.aif.csf.boot.CsfBooter;
import com.ai.aif.csf.configure.Configure;
import com.ai.aif.csf.configure.boot.EntryApplication;
import com.ai.aif.csf.module.context.spring.SpringBootContextHelper;
import com.asiainfo.bits.csf.annotation.EnableService;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import lombok.SneakyThrows;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * @author
 */
@SpringBootApplication(
        exclude = {MybatisPlusAutoConfiguration.class},
        scanBasePackages = {"com.asiainfo.crm.base", "com.asiainfo.bits"}
)
@EnableService(basePackages = "com.asiainfo.crm.base.api", api = "/api/base", enabledController = true, enabledProxy = false)
public class Application {

    private static transient final String APRP_ROTOCOL = "org.apache.coyote.http11.Http11AprProtocol";

    @Autowired
    Environment env;

    private boolean isAprMode(){
        return "true".equals(System.setProperty("server.tomcat.apr", "false"));
    }

    public static void main(String[] args) throws Throwable {
        String jvmServerName = System.getProperty("csf.server.name");
        String jvmServerIp = System.getProperty("server.ip");
        /**
         * JVM变量获取不到serverName和serverIp属性时，设置为本机开发固定值
         */
        if(StringUtils.isBlank(jvmServerName) && StringUtils.isBlank(jvmServerIp)) {
            System.setProperty("csf.server.name", "app-node01-srv01");
            System.setProperty("server.ip", "127.0.0.1");
        }
        ApplicationContext context = SpringApplication.run(Application.class, args);
        SpringBootContextHelper.applicationContext = context;
        CsfBooter.main(args);

    }

    /**
     * 设置APR模式
     * @return
     */
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        if(isAprMode()) {
            tomcat.setProtocol(APRP_ROTOCOL);  //org.apache.coyote.http11.Http11NioProtocol
            tomcat.addContextLifecycleListeners(new AprLifecycleListener());
            //tomcat.addAdditionalTomcatConnectors(aprConnector());
        }
        return tomcat;
    }

}
