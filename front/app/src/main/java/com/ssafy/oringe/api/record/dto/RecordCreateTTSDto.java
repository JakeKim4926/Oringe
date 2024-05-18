package com.ssafy.oringe.api.record.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecordCreateTTSDto {
    private Long memberId;
    private String recordTTS;
}
