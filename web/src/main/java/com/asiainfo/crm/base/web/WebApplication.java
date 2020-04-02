package com.asiainfo.crm.base.web;

import com.asiainfo.bits.csf.annotation.EnableService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.asiainfo.crm.base.web", "com.asiainfo.crm.base.api", "com.asiainfo.bits", "com.wade.springboot"})
@EnableService(basePackages = {"com.asiainfo.crm.base.api"})
public class WebApplication extends SpringBootServletInitializer {

    /**
     * 开发参数配置
     *  增加JVM参数 -Dorg.apache.tapestry.disable-caching=true 禁用tapestry缓存
     *  增加JVM参数 -javaagent:library/spring/springloaded-1.2.8.RELEASE.jar -noverify 启用springloaded-热加载
     *  也可以使用Idea的JRebel插件来做热加载，效果更好
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(WebApplication.class);
    }

    @Bean("com.asiainfo.order.web.webServerFactoryCustomizer")
    public WebServerFactoryCustomizer webServerFactoryCustomizer(){
        return (WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>) factory -> {
            MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
            mappings.add("txt", "application/octet-stream; charset=utf-8");
            factory.setMimeMappings(mappings);
        };
    }

}
