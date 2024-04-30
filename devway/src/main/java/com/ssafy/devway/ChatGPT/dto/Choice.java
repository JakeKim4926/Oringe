package com.ssafy.devway.ChatGPT.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class Choice {

  private Integer index;
  private Message message;
  private String finish_reason;

}

