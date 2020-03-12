package com.asiainfo.crm.sec.web.config;

import com.wade.springboot.factory.PropertyLoaderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration("com.asiainfo.crm.sec.web.config.LoginProperties")
@PropertySource(name = "areca", value={"classpath:areca.yaml", "classpath:areca.properties"}, ignoreResourceNotFound=true, factory = PropertyLoaderFactory.class)
@ConfigurationProperties(prefix = "sec.login")
public class LoginProperties {

    private boolean enableVerifycode = false;

    private boolean sessionLimit = false;
    private boolean reloginLimit = false;

    public boolean isEnableVerifycode(){
        return enableVerifycode;
    }

    public void setEnableVerifycode(boolean enable){
        enableVerifycode = enable;
    }

    public boolean isSessionLimit(){
        return sessionLimit;
    }

    public void setSessionLimit(boolean sessionLimit){
        this.sessionLimit = sessionLimit;
    }

    public boolean isReloginLimit(){
        return reloginLimit;
    }

    public void setReloginLimit(boolean reloginLimit){
        this.reloginLimit = reloginLimit;
    }
}
