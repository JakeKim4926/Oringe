package com.ssafy.devway.ChatGPT.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRequestDTO {
  private final List<Message> messages;
  private final String model;
  private final int max_tokens;
  private final double temperature;
  private final double top_p;

}
