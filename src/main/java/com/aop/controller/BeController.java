package com.aop.controller;

import com.aop.annotation.SysLogAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BeController {

    @SysLogAnnotation("测试")
    @GetMapping("/testb")
    public String test(@RequestParam("name") String name){

        log.info("【执行Controller】");
        return name;
    }
}