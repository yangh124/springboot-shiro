package com.yh.shiro.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : yh
 * @date : 2021/3/23 11:11
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "shiro")
public class ShiroPerUrlConfig {

    /**
     * 无需登录可访问url
     */
    private List<String> anon = new ArrayList<>();

    /**
     * 是否开启集群模式
     */
    private Boolean cluster;

    /**
     * session超时时间（秒）
     */
    private Long sessionTimeout;
}
