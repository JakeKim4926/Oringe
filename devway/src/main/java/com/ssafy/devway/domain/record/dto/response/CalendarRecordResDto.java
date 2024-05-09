package com.ssafy.devway.domain.record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class CalendarRecordResDto {


    private Long recordId;

    private Boolean recordSuccess;

    private LocalDate recordDate;
}
