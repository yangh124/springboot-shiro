package com.yh.shiro.controller;

import com.yh.shiro.bean.UserInfo;
import com.yh.shiro.bean.UserToken;
import com.yh.shiro.enums.LoginTypeEnum;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author : yh
 * @date : 2021/8/21 10:49
 */
@RestController
public class DemoController {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录demo
     */
    @GetMapping("/login")
    public String login(String username) {
        if ("yh".equals(username)) {
            Subject subject = SecurityUtils.getSubject();
            UserToken userToken = new UserToken(LoginTypeEnum.NO_AUTH, username, "123");
            try {
                subject.login(userToken);
            } catch (Exception e) {
                return "fail";
            }
            return "success";
        } else {
            return "fail";
        }

    }


    @GetMapping("/info")
    public UserInfo info() {
        Subject subject = SecurityUtils.getSubject();
        UserInfo userInfo = (UserInfo) subject.getPrincipal();
        redisTemplate.opsForValue().set(userInfo.getUserName(), userInfo.getUserName(), 5, TimeUnit.MINUTES);
        return userInfo;
    }

    /**
     * 登录demo
     */
    @GetMapping("/unauth")
    public String unauth() {
        return "unauth";
    }
}
