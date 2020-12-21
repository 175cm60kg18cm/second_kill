package com.mz.kill.server.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    Environment environment;

    private Logger logger = LoggerFactory.getLogger(UserController.class);
    @GetMapping(value = {"/to/login","/unauth"})
    public String toLogin(){
        return "login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String userName, @RequestParam String password, ModelMap modelMap){
        String errorMsg="";
        try {
            if(!SecurityUtils.getSubject().isAuthenticated()){
                String newPass=new Md5Hash(password,environment.getProperty("shiro.encrypt.password.salt")).toString();
                UsernamePasswordToken token = new UsernamePasswordToken(userName, newPass);
                SecurityUtils.getSubject().login(token);
            }
        }catch (Exception e){
           errorMsg="用户登录异常，请联系管理员";
           logger.error("error on shiro login....@getmapping(\"/login\")");
        }
        if(errorMsg!=""){
            modelMap.put("errorMsg",errorMsg);
            return "login";
        }
        else {
            return "redirect:/index";
        }
    }
    @RequestMapping("/logout")
    public String logOut(){
        SecurityUtils.getSubject().logout();
        return "login";
    }
}
