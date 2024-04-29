package com.ssafy.devway.ChatGPT.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class Message {
  private String role;
  private String content;

}