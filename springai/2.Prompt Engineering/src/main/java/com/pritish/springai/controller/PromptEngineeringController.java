package com.pritish.springai.controller;

import io.netty.handler.codec.MessageAggregationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        log.info("Endpoint : /conversation-history, Prompt: {}", prompt);
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    // AssistantMessage Use Case : Prior Response of model for reference in follow-up question
    @GetMapping("/conversation-history/follow-up")
    public String getConversationHistoryWithFollowUp(@RequestParam String followUpQuestion) {
        Message systemMessage = new SystemMessage("You are a helpful Java tutor");
        Message userMessage = new UserMessage("What is a Java Record?");
        Message priorReply = new AssistantMessage("A Java Record is a special type of class introduced in Java 16 that provides a concise way to create immutable data-carrying objects by automatically generating constructors, getters (accessors), equals(), hashCode(), and toString() methods.");
        Message followUpUserMessage = new UserMessage(followUpQuestion);
        log.info("Endpoint : /conversation-history/follow-up, Follow-up question: {}", followUpQuestion);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage, priorReply, followUpUserMessage));
        log.info("Endpoint : /conversation-history/follow-up, Prompt: {}", prompt);
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    // AssistantMessage Use Case : Set an example of the response expected from the model for follow-up question
    @GetMapping("/conversation-history/example")
    public String getConversationHistoryWithExampleInPrompt(@RequestParam String followUpQuestion) {
        Message systemMessage = new SystemMessage("You are a helpful Java tutor");
        Message userMessage = new UserMessage("What is a Java Record?");
        Message priorReply = new AssistantMessage("Example Response: A Java Record is a special type of class introduced in Java 16 that provides a concise way to create immutable data-carrying objects by automatically generating constructors, getters (accessors), equals(), hashCode(), and toString() methods.");
        Message followUpUserMessage = new UserMessage(followUpQuestion);
        log.info("Follow-up question: {}", followUpQuestion);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage, priorReply, followUpUserMessage));
        log.info("Prompt: {}", prompt);
        return chatClient.prompt(prompt)
                .call()
                .content();
    }



}
