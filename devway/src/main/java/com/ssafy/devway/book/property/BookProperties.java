package com.ssafy.devway.book.property;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BookProperties {
    private String apiKey;
    private String url = "https://dapi.kakao.com/v3/search/book";
}
