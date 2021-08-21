package com.yh.shiro.service;

import com.yh.shiro.bean.UserInfo;

import java.util.List;

/**
 * 需业务模块实现接口
 * @author : yh
 * @date : 2021/8/21 09:47
 */
public interface ShiroUserService {

    /**
     * 获取角色
     *
     * @param id
     * @return
     */
    List<String> getRoles(Long id);

    /**
     * 获取权限
     *
     * @param id
     * @return
     */
    List<String> getPermissions(Long id);

    /**
     * 根据账户名查询用户
     *
     * @param userName 账号
     * @return
     */
    UserInfo getUserByUserName(String userName);
}
