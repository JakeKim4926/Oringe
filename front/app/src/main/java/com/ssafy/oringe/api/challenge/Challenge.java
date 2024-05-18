package com.ssafy.oringe.api.challenge;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @Override
    public String toString() {
        return challengeTitle;  // 스피너에서 보여줄 텍스트
    }
}
