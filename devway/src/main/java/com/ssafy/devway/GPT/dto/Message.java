package com.ssafy.devway.GPT.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class Message {

    private String role;
    private String content;
}