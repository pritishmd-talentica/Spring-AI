package com.pritish.springai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/advisor")
public class PromptEngineeringController {

    private final ChatClient chatClient;
    private final ChatClient chatClientWithDefaultSystemPrompt;

    public PromptEngineeringController(ChatClient.Builder builder) {

        this.chatClient = builder.build();
        this.chatClientWithDefaultSystemPrompt = builder.
                defaultSystem("You are a strict technical interviewer conducting a Java backend interview " + "Ask probing follow up questions in your response")
                .build();

    }

    @GetMapping
    public String advise() {
        return chatClient.prompt()
                .user("Explain spring boot in simple terms")
                .call()
                .content();
    }

    @GetMapping("/system-prompt")
    public String adviseWithSystemPrompt() {
        log.info("Endpoint : /system-prompt invoked");
        return chatClient.prompt()
                .system("You are a helpful Java tutor")
                .user("Explain spring boot in simple terms")
                .call()
                .content();
    }

    @GetMapping("/system-prompt/dynamic")
    public String adviseWithDynamicSystemPrompt(
            @RequestParam String persona,
            @RequestParam String question
    ) {
        log.info("Endpoint : /system-prompt/dynamic invoked, Persona: {}, Question: {}", persona, question);
        return chatClient.prompt()
                .system("You are a helpful " + persona + " tutor")
                .user(question)
                .call()
                .content();
    }

    @GetMapping("/system-prompt/default")
    public String adviseWithDefaultSystemPrompt(@RequestParam String question) {
        log.info("Endpoint : /system-prompt/default invoked");
        return chatClientWithDefaultSystemPrompt.prompt()
                .user(question)
                .call()
                .content();
    }

    @GetMapping("/prompt-template-variables")
    public String adviseWithPromptTemplateVariables(@RequestParam String language, @RequestParam String task) {
        PromptTemplate promptTemplate = new PromptTemplate("""
                        Write a short {language} code snippet that demonstrates how to {task}. Include comments explaining each step
                        """
        );

        Prompt prompt = promptTemplate.create(Map.of(
                "language", language,
                "task", task
        ));

        return "<pre>" + chatClient.prompt(prompt)
                .call()
                .content() + "</pre>";
    }

    @GetMapping("/inline-prompt-template")
    public String adviseWithInlinePromptTemplate(@RequestParam String language, @RequestParam String task) {

        return "<pre>" + chatClient.prompt()
                .user(u -> u.text(
                        """
                        Write a short {language} code snippet that demonstrates how to {task}. Include comments explaining each step
                        """)
                        .param("language", language)
                        .param("task", task))
                .call()
                .content() + "</pre>";
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
