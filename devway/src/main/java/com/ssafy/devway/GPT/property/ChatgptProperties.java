package com.ssafy.devway.GPT.property;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ChatgptProperties {

    private String apiKey;
    private String url = "https://api.openai.com/v1/chat/completions";
    private String model = "gpt-4";
    private Integer maxTokens = 500;
    private Double temperature = 1.0;
    private Double topP = 1.0;
}