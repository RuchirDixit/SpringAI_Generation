package com.springai.spring_ai_demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springai.spring_ai_demo.response.FormulaOneResponse;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormulaOneService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private ImageModel openAiImageModel;

    /**
     * This is method that return information about any formula one content. If question asked is not related to F1 it returns a joke saying not relevant question
     * @param input : input question
     * @return : JSON (@FormulaOneResponse) containing content field having the actual response
     * @throws JsonProcessingException
     */
    public FormulaOneResponse generateFormulaOneResponse(String input) throws IOException {

        // loading the prompt from 'f1_bot_prompt.txt' file in resources folder
        String prompt = loadPromptFromFile("prompts/f1_bot_prompt.txt");
        Map<String,String> map = new HashMap<>();
        map.put("input",input);
        // Method to replace placeholders in prompt file with actual text
        String readyPrompt = replaceInputWithMessageInPrompt(prompt,map);
        ChatResponse f1Response = chatModel.call(new Prompt(prompt));
        //get actual text content from open AI API response
        String responseStr = f1Response.getResult().getOutput().getText();
        // generate JSON from of this string, in FormulaOneResponse format
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseStr, FormulaOneResponse.class);

    }


    /**
     * Generates images based on description given of 1024x1024 resolution
     * @param imageDescription : Description to generate image
     * @param numOfImages : Number of images to generate
     * @return : List of String with local Image path - spring-ai-demo/images/
     * @throws IOException
     */
    public List<String> generateImages(String imageDescription,String numOfImages) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        clearImagesFolder();
        String promptToReplace = loadPromptFromFile("prompts/image_gen_prompt.txt");
        Map<String,String> mapToReplace = new HashMap<>();
        mapToReplace.put("description",imageDescription);
        String prompt = replaceInputWithMessageInPrompt(promptToReplace,mapToReplace);
        for(int i = 0; i < Integer.valueOf(numOfImages); i++) {
            OpenAiImageOptions options = OpenAiImageOptions.builder()
                    .width(1024)
                    .height(1024)
                    .build();
            ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
            ImageResponse response = openAiImageModel.call(imagePrompt);
            // get single url and add it to List<String> of imageUrls, as this model generated just one image at a time
            String url = response.getResults()
                    .get(0)
                    .getOutput()
                    .getUrl();
            // Download the generated image
            BufferedImage original = ImageIO.read(new URL(url));

            // Resize image to 512x512
            Image scaled = original.getScaledInstance(512, 512,Image.SCALE_SMOOTH);

            BufferedImage resized = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resized.createGraphics();
            g.drawImage(scaled, 0, 0, null);
            g.dispose();

            // Save image locally
            String fileName = "generated_image_" + System.currentTimeMillis() + ".png";
            File output = new File("images/" + fileName);

            output.getParentFile().mkdirs(); // create folder if not exists

            ImageIO.write(resized, "png", output);

            imageUrls.add(output.getAbsolutePath());
        }
        return imageUrls;
    }

    /**
     * * Loads the input message given by the user and dynamically replaces it with {input} string in the prompt file
     * @param prompt : Prompt in which we need to dynamically set values
     * @param inputMap : Map of key (input variable to replace), value (Actual input by which we need to replace it)
     * @return : Ready prompt
     */
    private String replaceInputWithMessageInPrompt(String prompt, Map<String,String> inputMap) {
        for(Map.Entry<String,String> entry : inputMap.entrySet()){
            prompt+=prompt.replace("{"+entry.getKey()+"}", entry.getValue());
        }
        return prompt;
    }

    /**
     * This Methods reads the prompt present in the fileName and returns it
     * @param fileName : File in which prompt is present
     * @return : Prompt string
     * @throws IOException
     */
    private String loadPromptFromFile(String fileName) throws IOException {
        Path path = new ClassPathResource(fileName).getFile().toPath();
        return Files.readString(path);
    }

    // Method to delete all images in /images folder which are generated before
    private void clearImagesFolder() {
        File folder = new File("images");

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

}
