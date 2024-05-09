package com.ssafy.devway.book.dto.kakao;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class KakaoResponseDTO {

    private List<Document> documents;
    private Meta meta;
}
