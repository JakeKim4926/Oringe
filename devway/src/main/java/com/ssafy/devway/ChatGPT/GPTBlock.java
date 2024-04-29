package com.ssafy.devway.ChatGPT;

import com.google.gson.Gson;
import com.ssafy.devway.ChatGPT.dto.ChatRequestDTO;
import com.ssafy.devway.ChatGPT.dto.ChatResponseDTO;
import com.ssafy.devway.ChatGPT.property.ChatgptProperties;
import com.ssafy.devway.block.element.BlockElement;
import java.io.IOException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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

  private final ChatgptProperties properties;
  private final OkHttpClient httpClient;
  private final Gson gson;

  public GPTBlock(String API_KEY) {
    this.properties = new ChatgptProperties();
    this.httpClient = new OkHttpClient();
    this.gson = new Gson();

    properties.setApiKey(API_KEY);
  }

  public String askGpt(String prompt) throws IOException {
    RequestBody body = RequestBody.create(
        gson.toJson(
            new ChatRequestDTO(prompt, properties.getMaxTokens(), properties.getTemperature(),
                properties.getTopP())),
        MediaType.get("application/json; charset=utf-8")
    );

    Request request = new Request.Builder()
        .url(properties.getUrl())
        .addHeader("Authorization", "Bearer " + properties.getApiKey())
        .post(body)
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      if (response.isSuccessful() && response.body() != null) {
        ChatResponseDTO chatResponse = gson.fromJson(response.body().string(),
            ChatResponseDTO.class);
        return chatResponse.getChoices().get(0).getText();
      } else {
        throw new RuntimeException("Failed to communicate with ChatGPT: " + response.message());
      }
    }
  }

}
