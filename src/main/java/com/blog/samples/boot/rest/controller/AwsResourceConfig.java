package com.blog.samples.boot.rest.controller;

import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.util.StringUtils;

@Configuration
@ImportResource("classpath:/aws-config.xml")
public class AwsResourceConfig {
	    
    /**
     * Elastic Beanstalk exposes a system variable PORT which allows us to set the 
     * value on startup. Check for PORT sys property and use it to set container port 
     * if available. This is required as Elastic Beanstalk exposes app on port 5000 by default. 
     * This port override can be ignored on local environments and 
     * the app will accept traffic on 8080 by default. 
     */
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return (container -> { 	
        	String portSysProperty = System.getProperty("PORT");
            if(!StringUtils.isEmpty(portSysProperty)){
            	container.setPort(Integer.parseInt(portSysProperty));
            }        	
        });
    }
}