package com.yh.shiro.realm;

import com.yh.shiro.bean.UserInfo;
import com.yh.shiro.bean.UserToken;
import com.yh.shiro.enums.LoginTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;


/**
 * 无权限登录realm
 *
 * @author : yh
 * @date : 2021/8/20 11:21
 */
@Slf4j
public class UserNoAuthRealm extends AuthorizingRealm {

    @Override
    public String getName() {
        return LoginTypeEnum.NO_AUTH.getName();
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof UserToken) {
            return ((UserToken) token).getLoginType() == LoginTypeEnum.NO_AUTH;
        } else {
            return false;
        }
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("================" + LoginTypeEnum.NO_AUTH.getDesc() + "================");
        UserToken userToken = (UserToken) authenticationToken;
        String phone = userToken.getUsername();
        String openId = String.valueOf(userToken.getPassword());
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(phone);
        return new SimpleAuthenticationInfo(userInfo, openId, getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

}
