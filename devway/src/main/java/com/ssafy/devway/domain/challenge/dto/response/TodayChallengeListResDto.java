package com.ssafy.devway.domain.challenge.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TodayChallengeListResDto {

    private Long challengeId;
    private String challengeTitle;
    private LocalDate challengeStart;
    private LocalDate challengeEnd;
    private Boolean recordSuccess;
}
