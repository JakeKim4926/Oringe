package com.ssafy.devway.domain.record.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecordCreateTTSDto {
    private Long memberId;
    private String recordTTS;
}
