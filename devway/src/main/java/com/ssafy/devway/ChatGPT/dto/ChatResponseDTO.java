package com.ssafy.devway.ChatGPT.dto;

import java.util.ArrayList;
import java.util.List;

public class ChatResponseDTO {

  private final List<Choice> choices;

  public ChatResponseDTO() {
    choices = new ArrayList<>();
  }
  public ChatResponseDTO(List<Choice> choices) {
    this.choices = choices;
  }

  public List<Choice> getChoices() {
    return choices;
  }

}
