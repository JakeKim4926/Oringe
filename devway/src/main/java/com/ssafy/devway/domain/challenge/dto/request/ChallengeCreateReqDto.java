package com.ssafy.devway.domain.challenge.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ChallengeCreateReqDto {

    private String challengeTitle;

    private String challengeStart;

    private String challengeEnd;

    private List<Integer> challengeCycle;

    private Boolean challengeAlarm;

    private String challengeAlarmTime;

    private String challengeMemo;

    private List<Integer> order;

}
