package com.ssafy.devway.GPT.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class ChatResponseDTO {

    private String id;
    private String object;
    private String model;
    private List<Choice> choices;
}
