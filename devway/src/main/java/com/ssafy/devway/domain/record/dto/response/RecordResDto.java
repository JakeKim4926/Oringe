package com.ssafy.devway.domain.record.dto.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
public class RecordResDto {

    private Long memberId;
    private Long challengeId;
    private LocalDate recordDate;
    private Boolean recordSuccess;
    private List<String> recordTemplates;
}
