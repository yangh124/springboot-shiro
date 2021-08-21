package com.yh.shiro.bean;


import com.yh.shiro.enums.LoginTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 继承UsernamePasswordToken
 * 添加登陆方式字段
 *
 * @author : yh
 * @date : 2021/8/20 10:19
 */
@Getter
@Setter
public class UserToken extends UsernamePasswordToken {

    /**
     * 登陆方式
     */
    private LoginTypeEnum loginType;

    public UserToken(LoginTypeEnum loginType, String username, String password) {
        super(username, password);
        this.loginType = loginType;
    }

}
