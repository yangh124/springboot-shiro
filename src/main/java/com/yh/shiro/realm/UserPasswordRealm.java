package com.yh.shiro.realm;

import com.yh.shiro.bean.UserInfo;
import com.yh.shiro.bean.UserToken;
import com.yh.shiro.enums.LoginTypeEnum;
import com.yh.shiro.service.ShiroUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

/**
 * 账号密码登录
 *
 * @author yh
 */
@Slf4j
public class UserPasswordRealm extends JdbcRealm {

    @Lazy
    @Autowired
    private ShiroUserService userService;

    @Override
    public String getName() {
        return LoginTypeEnum.PASSWORD.getName();
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof UserToken) {
            return ((UserToken) token).getLoginType() == LoginTypeEnum.PASSWORD;
        } else {
            return false;
        }
    }

    /**
     * 登录成功后授权
     *
     * @param principals 用户信息
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        UserInfo userInfo = (UserInfo) principals.getPrimaryPrincipal();
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

    /**
     * 登录认证
     *
     * @param authenticationToken UserToken
     * @return
     * @throws AuthenticationException 需业务模块catch异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("================" + LoginTypeEnum.PASSWORD.getDesc() + "================");
        UserToken token = (UserToken) authenticationToken;
        String username = token.getUsername();
        UserInfo user = userService.getUserByUserName(username);
        if (user == null) {
            throw new UnknownAccountException(" [" + username + "] 登录失败");
        }
        if (user.getEnabled() == 0) {
            throw new LockedAccountException(" [" + username + "] 账号被禁用");
        }
        String passwd = user.getPassword();
        user.setPassword(null);
        return new SimpleAuthenticationInfo(user, passwd, getName());
    }
}
