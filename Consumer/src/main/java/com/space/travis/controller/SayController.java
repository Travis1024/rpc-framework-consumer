package com.space.travis.controller;

import com.space.travis.client.annotation.RpcAutowired;
import com.space.travis.api.FirstSayService;
import com.space.travis.api.SecondSayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName SayController
 * @Description SayController类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/21
 */
@Slf4j
@RestController
public class SayController {

    @RpcAutowired(version = "1.0")
    private FirstSayService firstSayService;
    @RpcAutowired(version = "1.0")
    private SecondSayService secondSayService;

    @RequestMapping("/say/hello")
    public String sayHello(){
        return "hello";
    }

    @RequestMapping("/say/first")
    public String sayFirst(@RequestParam("name") String name) {
        String result = "SayController(First) receive infomation ---> " + firstSayService.sayFirst(name);
        log.info(result);
        return result;
    }

    @RequestMapping("/say/firsttwice")
    public String sayFirstTwice(@RequestParam("name") String name) {
        String result = "SayController(FirstTwice) receive infomation ---> " + firstSayService.sayFirstTwice(name);
        log.info(result);
        return result;
    }

    @RequestMapping("/say/second")
    public String saySecond(@RequestParam("name") String name) {
        String result = "SayController(Second) receive infomation ---> " + secondSayService.saySecond(name);
        log.info(result);
        return result;
    }
    @RequestMapping("/say/secondtwice")
    public String saySecondTwice(@RequestParam("name") String name) {
        String result = "SayController(SecondTwice) receive infomation ---> " + secondSayService.saySecondTwice(name);
        log.info(result);
        return result;
    }
}
