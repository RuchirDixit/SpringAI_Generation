package com.springai.spring_ai_demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springai.spring_ai_demo.response.FormulaOneResponse;
import com.springai.spring_ai_demo.service.FormulaOneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/f1")
@CrossOrigin("*")
public class FormulaOneController {

    @Autowired
    private FormulaOneService formulaOneService;

    // F1 Bot
    @GetMapping("/f1bot")
    public FormulaOneResponse generateF1Response(@RequestParam String input) throws IOException {
        return formulaOneService.generateFormulaOneResponse(input);
    }
}
