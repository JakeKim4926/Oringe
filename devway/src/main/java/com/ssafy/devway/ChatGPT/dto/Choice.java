package com.ssafy.devway.ChatGPT.dto;

public class Choice {

  private final String text;

  public Choice(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
}
