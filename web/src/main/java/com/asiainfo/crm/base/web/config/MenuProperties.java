package com.asiainfo.crm.base.web.config;

import com.wade.springboot.factory.PropertyLoaderFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Configuration("com.asiainfo.crm.sec.web.config.MenuProperties")
@PropertySource(name = "areca", value={"classpath:areca.yaml", "classpath:areca.properties"}, ignoreResourceNotFound=true, factory = PropertyLoaderFactory.class)
@ConfigurationProperties(prefix = "sec.menu")
public class MenuProperties {

    private Set<Map<String, Object>> shortcuts;

    public Set<Map<String, Object>> getShortcuts(){
        return this.shortcuts;
    }

    public void setShortcuts(Set<Map<String, Object>> shortcuts){
        this.shortcuts = shortcuts;
    }

}
