package com.nageoffer.speraph.demo.controller;

import com.nageoffer.speraph.api.annotation.Idempotent;
import com.nageoffer.speraph.api.enmus.IdempotentSceneEnum;
import com.nageoffer.speraph.api.enmus.IdempotentTypeEnum;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpIdempotentController {

    @SneakyThrows
    @GetMapping("/idempotent/http/test")
    @Idempotent(
            scene = IdempotentSceneEnum.HTTP,
            type = IdempotentTypeEnum.PARAM,
            message = "用户触发幂等行为")
    public String idempotentHttpTest(String orderSn) {
        Thread.sleep(10000);
        System.out.println(String.format("[%s] 当前线程执行幂等测试行为...", Thread.currentThread().getName()));
        return "success";
    }

}
