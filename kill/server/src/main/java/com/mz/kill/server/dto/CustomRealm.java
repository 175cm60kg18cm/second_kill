package com.mz.kill.server.dto;

import com.mz.kill.model.entity.User;
import com.mz.kill.model.mapper.UserMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomRealm extends AuthorizingRealm {
    Logger logger = LoggerFactory.getLogger(CustomRealm.class);
    @Autowired
    UserMapper userMapper;
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken authenticationToken1 = (UsernamePasswordToken) authenticationToken;
        String username = authenticationToken1.getUsername();
        String password = String.valueOf(authenticationToken1.getPassword());
        logger.info("user login ->username:{},password: {} ",username,password);
        User user = userMapper.selectByUserName(username);
        if(user==null) {
            throw new UnknownAccountException("user not exists");
        }
        if(!user.getPassword().equals(password)){
            throw new IncorrectCredentialsException("user password not right");
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user.getUserName()
                ,user.getPassword(),getName());
        setSession("uid",user.getId());
        return simpleAuthenticationInfo;
    }
    /**
     * 将key对应的value放入shiro的session中管理，该session最终会被httpsession管理，
     * 如果是分布式session最终会被redis管理
     * */
    public void setSession(String key,Object value){
        Session session = SecurityUtils.getSubject().getSession();
        if(session!=null) {
            session.setAttribute(key, value);
            session.setTimeout(30000L);
        }
    }
}
