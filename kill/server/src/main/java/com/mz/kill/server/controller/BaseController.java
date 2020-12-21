package com.mz.kill.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class BaseController {
    @GetMapping("welcome")
    public String welcome(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
        System.out.println("welcome");
        return "welcome";
    }
}
