package com.yh.shiro.realm;

import com.yh.shiro.bean.UserInfo;
import com.yh.shiro.bean.UserToken;
import com.yh.shiro.enums.LoginTypeEnum;
import com.yh.shiro.service.ShiroUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

/**
 * 无密码登录
 *
 * @author : yh
 * @date : 2021/8/20 10:56
 */
@Slf4j
public class UserNoPasswordRealm extends AuthorizingRealm {

    @Lazy
    @Autowired
    private ShiroUserService userService;

    @Override
    public String getName() {
        return LoginTypeEnum.NO_PASSWORD.getName();
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof UserToken) {
            return ((UserToken) token).getLoginType() == LoginTypeEnum.NO_PASSWORD;
        } else {
            return false;
        }
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("================" + LoginTypeEnum.NO_PASSWORD.getDesc() + "================");
        UserToken userToken = (UserToken) authenticationToken;
        String username = userToken.getUsername();
        String code = String.valueOf(userToken.getPassword());
        UserInfo user = userService.getUserByUserName(username);
        if (user == null) {
            throw new UnknownAccountException(" [" + username + "] 登录失败");
        }
        if (user.getEnabled() == 0) {
            throw new LockedAccountException(" [" + username + "] 账号被禁用");
        }
        user.setPassword(null);
        return new SimpleAuthenticationInfo(user, code, getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        UserInfo userInfo = (UserInfo) principalCollection.getPrimaryPrincipal();
        Long id = userInfo.getId();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //角色
        List<String> roles = userService.getRoles(id);
        authorizationInfo.addRoles(roles);
        //权限
        List<String> permissions = userService.getPermissions(id);
        authorizationInfo.addStringPermissions(permissions);
        return authorizationInfo;
    }
}
