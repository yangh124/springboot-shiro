package com.yh.shiro.enums;

/**
 * @author yh
 */
public enum LoginTypeEnum {

    /**
     * 账号密码登录
     */
    PASSWORD("password_realm", "账号密码登录"),
    /**
     * 无密码登录
     */
    NO_PASSWORD("no_password_realm", "无密码登录"),
    /**
     * 无权限登录
     */
    NO_AUTH("no_auth_realm", "无权限登录");

    public final String name;
    public final String desc;

    LoginTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
