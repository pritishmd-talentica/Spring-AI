package com.pritish.springai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/advisor")
public class TechAdvisorController {

    private final ChatClient chatClient;

    public TechAdvisorController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping
    public String advise() {
        return chatClient.prompt()
                .user("Explain spring boot in simple terms")
                .call()
                .content();
    }
}
