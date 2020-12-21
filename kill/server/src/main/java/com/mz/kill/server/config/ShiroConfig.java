package com.mz.kill.server.config;

import com.mz.kill.server.dto.CustomRealm;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Bean
    public CustomRealm customRealm(){
        return new CustomRealm();
    }
    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm());
        return securityManager;
    }
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setLoginUrl("/to/login");
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauth");
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap();
        filterChainDefinitionMap.put("/to/login","anon");
        filterChainDefinitionMap.put("/item/detail/*","authc");
        filterChainDefinitionMap.put("/**","anon");
        filterChainDefinitionMap.put("/kill/execute","authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;

    }

}
