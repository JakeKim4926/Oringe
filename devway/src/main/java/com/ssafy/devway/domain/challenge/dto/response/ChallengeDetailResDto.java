package com.ssafy.devway.domain.challenge.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ChallengeDetailResDto {
    private Long recordId;

    private Boolean recordSuccess;

    private LocalDate recordDate;

}
