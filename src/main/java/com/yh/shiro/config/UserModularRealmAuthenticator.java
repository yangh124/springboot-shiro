package com.yh.shiro.config;

import com.yh.shiro.bean.UserToken;
import com.yh.shiro.enums.LoginTypeEnum;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import java.util.Collection;
import java.util.HashMap;

/**
 * 多realm配置
 *
 * @author : yh
 * @date : 2021/8/20 13:36
 */
public class UserModularRealmAuthenticator extends ModularRealmAuthenticator {


    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 判断getRealms()是否返回为空
        assertRealmsConfigured();
        // 所有Realm
        Collection<Realm> realms = getRealms();
        // 登录类型对应的所有Realm
        HashMap<String, Realm> realmHashMap = new HashMap<>(realms.size());
        for (Realm realm : realms) {
            realmHashMap.put(realm.getName(), realm);
        }
        UserToken token = (UserToken) authenticationToken;
        // 登录类型
        LoginTypeEnum loginType = token.getLoginType();
        //如果有LoginTypeEnum，则返回该登录类型的realm,否则调用所有realm认证
        if (realmHashMap.get(loginType.getName()) != null) {
            return doSingleRealmAuthentication(realmHashMap.get(loginType.getName()), token);
        } else {
            return doMultiRealmAuthentication(realms, token);
        }
    }
}
