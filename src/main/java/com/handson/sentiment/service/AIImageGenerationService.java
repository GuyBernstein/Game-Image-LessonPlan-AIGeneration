package com.handson.sentiment.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIImageGenerationService {
    @Value("${openai.api.key}")
    private String apiKey;

    @Autowired
    ObjectMapper objectMapper;


    public ImageResult generateImage(String prompt) throws UnirestException, JsonProcessingException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.post("https://api.openai.com/v1/images/generations")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .body("{\n    \"model\": \"dall-e-3\",\n    \"prompt\": \"" + prompt + "\",\n    \"n\": 1,\n    \"size\": \"1024x1024\"\n  }")
                .asString();


        ImageResponse res = objectMapper.readValue(response.getBody(), ImageResponse.class);
        if (res.getData().isEmpty()) {
            return null;
        }

        // Truncate the image revised prompt to maximum 500 length (using the setter)
        ImageResponseObject responseObj = res.getData().get(0);
        responseObj.setRevised_prompt(responseObj.getRevisedPrompt());

        // Return the result
        return new ImageResult(res.getCreated(), responseObj.getUrl(), responseObj.getRevisedPrompt());
    }

    public static class ImageResult{
        private final int created;
        private final String imageUrl;
        private final String revisedPrompt;

        public ImageResult(int statusCode, String imageUrl, String revisedPrompt) {
            this.created = statusCode;
            this.imageUrl = imageUrl;
            this.revisedPrompt = revisedPrompt;
        }

        public int getCreated() {
            return created;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getRevisedPrompt() {
            return revisedPrompt;
        }
    }

    static class ImageResponse {
        private Integer created;
        private List<ImageResponseObject> data;

        public Integer getCreated() {
            return created;
        }

        public void setCreated(Integer created) {
            this.created = created;
        }

        public List<ImageResponseObject> getData() {
            return data;
        }

        public void setData(List<ImageResponseObject> data) {
            this.data = data;
        }
    }

    static class ImageResponseObject {
        private String revised_prompt;
        private String url;

        public String getRevisedPrompt() {
            return revised_prompt;
        }

        public void setRevised_prompt(String revised_prompt) {
            if (revised_prompt != null && revised_prompt.length() > 500) {
                this.revised_prompt = revised_prompt.substring(0, 500);
            } else {
                this.revised_prompt = revised_prompt;
            }
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
