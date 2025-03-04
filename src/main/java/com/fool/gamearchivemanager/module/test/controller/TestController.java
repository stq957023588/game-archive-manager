package com.fool.gamearchivemanager.module.test.controller;

import com.fool.gamearchivemanager.config.message.queue.MessageQueueTemplate;
import com.fool.gamearchivemanager.entity.constant.MessageQueueConstant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final MessageQueueTemplate messageQueueTemplate;

    public TestController(MessageQueueTemplate messageQueueTemplate) {
        this.messageQueueTemplate = messageQueueTemplate;
    }

    @GetMapping("/test/send/message")
    public void sendMessage(@RequestParam(name = "aaaa") String aaaa) {
        for (int i = 0; i < 20; i++) {
            if (i == 5) {
                messageQueueTemplate.send(MessageQueueConstant.EXCHANGE_TEST,"re");
            } else {
                messageQueueTemplate.send(MessageQueueConstant.EXCHANGE_TEST,String.valueOf(i));
            }
        }
    }


    @GetMapping("/test/auth")
    public String testAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "Auth test success";
    }


    @GetMapping("/test/ignore")
    public String testIgnore() {
        return "Auth test ignore";
    }

    @GetMapping("/test/idempotent")
    public String testI() {
        return "Auth test idempotent";
    }




    public static void main(String[] args) throws InterruptedException {

    }


}
