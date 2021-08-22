package com.yh.shiro.config;

import com.yh.shiro.dao.RedisSessionDao;
import com.yh.shiro.enums.LoginTypeEnum;
import com.yh.shiro.realm.UserNoAuthRealm;
import com.yh.shiro.realm.UserNoPasswordRealm;
import com.yh.shiro.realm.UserPasswordRealm;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yh
 */
@Configuration
public class ShiroConfig {

    /**
     * 密码登录realm
     */
    @Bean
    public UserPasswordRealm userPasswordRealm() {
        UserPasswordRealm userPasswordRealm = new UserPasswordRealm();
        userPasswordRealm.setName(LoginTypeEnum.PASSWORD.getName());
        return userPasswordRealm;
    }

    /**
     * 无密码登录realm
     */
    @Bean
    public UserNoPasswordRealm userNoPasswordRealm() {
        UserNoPasswordRealm userNoPasswordRealm = new UserNoPasswordRealm();
        userNoPasswordRealm.setName(LoginTypeEnum.NO_PASSWORD.getName());
        return userNoPasswordRealm;
    }

    /**
     * 无权限登录realm
     */
    @Bean
    public UserNoAuthRealm userNoAuthRealm() {
        UserNoAuthRealm userNoAuthRealm = new UserNoAuthRealm();
        userNoAuthRealm.setName(LoginTypeEnum.NO_AUTH.getName());
        return userNoAuthRealm;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // Shiro的核心安全接口,这个属性是必须的
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //身份认证失败，则跳转到登录页面的配置
        //shiroFilterFactoryBean.setLoginUrl("/login");
        //没有权限跳转的url
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauth");
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        //无需登录拦截url
        List<String> anon = shiroPerUrlConfig().getAnon();
        if (null != anon && anon.size() > 0) {
            for (String anonUrl : anon) {
                filterChainDefinitionMap.put(anonUrl, "anon");
            }
        }
        /** 需登录拦截url */
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "shiro", name = "cluster", havingValue = "true")
    public SessionManager sessionManager(RedisSessionDao redisSessionDao) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDao);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setDeleteInvalidSessions(true);
        SimpleCookie cookie = new SimpleCookie("sr_se_id");
        cookie.setHttpOnly(true);
        sessionManager.setSessionIdCookie(cookie);
        return sessionManager;
    }

    @Bean
    public SecurityManager securityManager(SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager);
        List<Realm> realms = new ArrayList<>(3);
        realms.add(userPasswordRealm());
        realms.add(userNoPasswordRealm());
        realms.add(userNoAuthRealm());
        securityManager.setRealms(realms);
        return securityManager;
    }

    /**
     * Shiro生命周期处理器
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 自定义的Realm管理，主要针对多realm
     */
    @Bean
    public UserModularRealmAuthenticator modularRealmAuthenticator() {
        UserModularRealmAuthenticator userModularRealmAuthenticator = new UserModularRealmAuthenticator();
        // 设置realm验证策略
        userModularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        return userModularRealmAuthenticator;
    }

    /**
     * 无需登录就能访问的url
     *
     * @return List
     */
    @Bean
    public ShiroPerUrlConfig shiroPerUrlConfig() {
        return new ShiroPerUrlConfig();
    }
}
