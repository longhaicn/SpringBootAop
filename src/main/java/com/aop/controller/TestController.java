package com.aop.controller;


import com.aop.annotation.SysLogAnnotation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    @SysLogAnnotation("测试")
    @GetMapping("/test")
    public String test(@RequestParam("name") String name){

        System.out.println(name);
        return name;
    }
}

