package com.ssafy.devway.book.dto.kakao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class Meta {

    private int total_count;
    private int pageable_count;
    private Boolean is_end;
}
