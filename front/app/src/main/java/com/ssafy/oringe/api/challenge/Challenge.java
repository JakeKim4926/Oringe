package com.ssafy.oringe.api.challenge;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Challenge {
    private Long challengeId;
    private String challengeTitle;
    private String challengeStart;
    private String challengeEnd;
    private List<Integer> challengeCycle;
    private Boolean challengeAlarm;
    private String challengeAlarmTime;
    private String challengeMemo;
    private List<Integer> order;
}
