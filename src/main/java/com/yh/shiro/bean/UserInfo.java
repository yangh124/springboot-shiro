package com.yh.shiro.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : yh
 * @date : 2021/8/21 09:38
 */
@Data
public class UserInfo implements Serializable {


    private static final long serialVersionUID = -8031725339494699347L;

    /**
     * id主键
     */
    private Long id;

    /**
     * 账户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 0-删除
     * 1-正常
     */
    private Byte active;

    /**
     * 0-禁用
     * 1-启动
     */
    private Byte enabled;
}
