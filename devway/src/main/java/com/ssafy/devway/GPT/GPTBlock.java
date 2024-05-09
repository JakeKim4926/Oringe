package com.ssafy.devway.GPT;

import com.google.gson.Gson;
import com.ssafy.devway.GPT.dto.ChatRequestDTO;
import com.ssafy.devway.GPT.dto.ChatResponseDTO;
import com.ssafy.devway.GPT.dto.Message;
import com.ssafy.devway.GPT.property.ChatgptProperties;
import com.ssafy.devway.block.element.BlockElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GPTBlock implements BlockElement {

    @Override
    public String getName() {
        return "GPT";
    }

    @Getter
    private String lastQuestion;
    @Getter
    private String lastAnswer;

    private final ChatgptProperties properties;
    private final OkHttpClient httpClient;
    private final Gson gson;

    public GPTBlock(String API_KEY) {
        this.properties = new ChatgptProperties();
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();

        properties.setApiKey(API_KEY);
    }

    public String askQuestion(String prompt, GPTMode gptMode) throws IOException {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("user", prompt + gptMode.textMode));

        ChatRequestDTO requestDto = new ChatRequestDTO(
            messages,
            properties.getModel(),  // Make sure this getter actually returns the model
            properties.getMaxTokens(),
            properties.getTemperature(),
            properties.getTopP()
        );

        String jsonBody = gson.toJson(requestDto);
        RequestBody body = RequestBody.create(jsonBody,
            MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
            .url(properties.getUrl())
            .addHeader("Authorization", "Bearer " + properties.getApiKey())
            .post(body)
            .build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {

                String responseBody = response.body().string();
                ChatResponseDTO chatResponse = gson.fromJson(responseBody, ChatResponseDTO.class);
                lastQuestion = prompt;
                lastAnswer = chatResponse.getChoices().get(0).getMessage().getContent();

                return lastAnswer;
            } else {
                throw new RuntimeException(
                    "Failed to communicate with ChatGPT: " + response.message());
            }
        } catch (Exception e) {
            if (response != null) {
                System.out.println();
                try {
                    return
                        "HTTP status code: " + response.code() +
                            "HTTP response body: " + (response.body() != null ? response.body()
                            .string()
                            : "null");
                } catch (IOException ex) {
                    return "Failed to read response body";
                }
            } else {
                return "Error during API request: " + e.getMessage();
            }
        }
    }

}
