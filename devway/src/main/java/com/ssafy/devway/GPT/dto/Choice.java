package com.ssafy.devway.GPT.dto;

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

