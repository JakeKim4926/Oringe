package com.ssafy.oringe.api.record.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecordCreateTTSDto {
    private Long memberId;
    private String recordTTS;
}
