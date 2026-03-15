package com.springai.spring_ai_demo.controller;

import com.springai.spring_ai_demo.service.ChatService;
import com.springai.spring_ai_demo.service.FormulaOneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private FormulaOneService formulaOneService;

    // To generate  entire response from Open AI api wrt the input text
    @GetMapping("/generate")
    public ResponseEntity<String> generateResponse(@RequestParam String text){
        String responseText = chatService.generateResponse(text);
        return new ResponseEntity<>(responseText, HttpStatus.OK);
    }

    // To generate response in streams from Open AI api wrt the input text for heavy responses
    @GetMapping("/stream")
    public Flux<String> streamResponse(@RequestParam String text){
        Flux<String> responseText = chatService.streamResponse(text);
        return responseText;
    }

    @GetMapping("/image")
    public ResponseEntity<List<String>> generateImages(@RequestParam String description, @RequestParam(required = false,defaultValue = "2") String numOfImages, @RequestParam(required = false,defaultValue = "512x512") String imageResolution) throws IOException {
        List<String> images = formulaOneService.generateImages(description,numOfImages);
        return new ResponseEntity<>(images,HttpStatus.OK);
    }
}
