package com.springai.spring_ai_demo.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {

    @Autowired
    private ChatModel chatModel;

    /**
     * This method will take input message from user and call open AI API to generate response and will return response
     * @param text : message/input
     * @return : Response from OPEN AI
     */
    public String generateResponse(String text) {
        return chatModel.call(text);
    }

    /**
     * This method will take input message from user and call open AI API to generate stream of response as it is generated
     * Used for heavy response which take time (Similar to ChatGPT)
     * @param text : message/input
     * @return : Response stream from OPEN AI
     */
    public Flux<String> streamResponse(String text) {
        return chatModel.stream(text);
    }
}
