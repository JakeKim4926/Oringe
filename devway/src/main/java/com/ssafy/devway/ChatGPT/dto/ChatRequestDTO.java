package com.ssafy.devway.ChatGPT.dto;

public class ChatRequestDTO {
  private final String prompt;
  private final int maxTokens;
  private final double temperature;
  private final double topP;

  public ChatRequestDTO(String prompt, int maxTokens, double temperature, double topP) {
    this.prompt = prompt;
    this.maxTokens = maxTokens;
    this.temperature = temperature;
    this.topP = topP;
  }
}
