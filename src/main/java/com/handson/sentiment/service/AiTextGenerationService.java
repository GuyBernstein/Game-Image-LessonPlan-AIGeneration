package com.handson.sentiment.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handson.sentiment.model.AiMessageType;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiTextGenerationService {
    @Value("${openai.api.key}")
    private String apiKey;

    @Autowired
    ObjectMapper objectMapper;

    private String userPromptGame;

    private String systemPromptGame;

    private String userPromptPlan;

    private String systemPromptPlan;

    private String systemPrompt;
    private String userPrompt;

    public String getUserPromptGame() {
        return userPromptGame;
    }

    public void setUserPromptGame(String userPromptGame) {
        this.userPromptGame = userPromptGame;
    }

    public String getSystemPromptGame() {
        return systemPromptGame;
    }

    public void setSystemPromptGame(String systemPromptGame) {
        this.systemPromptGame = systemPromptGame;
    }

    public String getUserPromptPlan() {
        return userPromptPlan;
    }

    public void setUserPromptPlan(String userPromptPlan) {
        this.userPromptPlan = userPromptPlan;
    }

    public String getSystemPromptPlan() {
        return systemPromptPlan;
    }

    public void setSystemPromptPlan(String systemPromptPlan) {
        this.systemPromptPlan = systemPromptPlan;
    }

    public TextResult generateText(AiMessageType type) throws UnirestException, JsonProcessingException {
        if (!setPrompt(type)) return null;

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.post("https://api.openai.com/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .body("{\n    \"model\": \"gpt-4o-mini\",\n    \"messages\": [\n      {\n        \"role\": \"system\",\n        \"content\": \"" + systemPrompt + "\"\n      },\n      {\n        \"role\": \"user\",\n        \"content\": \"" + userPrompt + "\"\n      }\n    ]\n  }")
                .asString();

        Response res = objectMapper.readValue(response.getBody(), Response.class);
        if (res.getChoices().isEmpty()) {
            return null;
        }
        return new TextResult(res.getCreated(), res.getChoices().get(0).getMessage().getContent());
    }

    private boolean setPrompt(AiMessageType type) {
        switch(type){
            case GAME:
            {
                systemPrompt = getSystemPromptGame();
                userPrompt = getUserPromptGame();
                break;
            }
            case PLAN:
            {
                systemPrompt = getSystemPromptPlan();
                userPrompt = getUserPromptPlan();
                break;
            }
            default:
                return false;

        }
        return true;
    }

    public static class TextResult {
        private final int created;
        private final String content;

        public TextResult(int created, String content) {
            this.created = created;
            this.content = content;
        }

        public int getCreated() {
            return created;
        }

        public String getContent() {
            return content;
        }

    }


    static class Response {
        private String id;
        private String object;
        private Integer created;
        private String model;
        private List<ResponseObject> choices;

        public List<ResponseObject> getChoices() {
            return choices;
        }

        public void setChoices(List<ResponseObject> choices) {
            this.choices = choices;
        }
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public Integer getCreated() {
            return created;
        }

        public void setCreated(Integer created) {
            this.created = created;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

    }

    static class ResponseObject {
        private Integer index;
        private ResponseContent message;

        private String logprobs;

        private String finish_reason;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }



        public String getLogprobs() {
            return logprobs;
        }

        public void setLogprobs(String logprobs) {
            this.logprobs = logprobs;
        }

        public String getFinish_reason() {
            return finish_reason;
        }

        public void setFinish_reason(String finish_reason) {
            this.finish_reason = finish_reason;
        }

        public ResponseContent getMessage() {
            return message;
        }

        public void setMessage(ResponseContent message) {
            this.message = message;
        }
    }

    static class ResponseContent {
        private String role;
        private String content;
        private String refusal;

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setRefusal(String refusal) {
            this.refusal = refusal;
        }

        public String getRefusal() {
            return refusal;
        }
    }

    public String getUserPrompt() {
        return userPrompt;
    }

    public void setUserPrompt(String userPrompt) {
        this.userPrompt = userPrompt;
    }

}
