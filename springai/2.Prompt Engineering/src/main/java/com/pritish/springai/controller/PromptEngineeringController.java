package com.pritish.springai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/advisor")
public class PromptEngineeringController {

    private final ChatClient chatClient;

    public PromptEngineeringController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping
    public String advise() {
        return chatClient.prompt()
                .user("Explain spring boot in simple terms")
                .call()
                .content();
    }

    @GetMapping("/conversation-history")
    public String getConversationHistory() {
        Message systemMessage = new SystemMessage("You are a helpful Java tutor");
        Message userMessage = new UserMessage("What is a Java Record?");
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return chatClient.prompt(prompt)
                .call()
                .content();
    }
}
