package com.ssafy.oringewatch.presentation.api.record.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecordCreateTTSDto {
    private Long memberId;
    private String recordTTS;
}
