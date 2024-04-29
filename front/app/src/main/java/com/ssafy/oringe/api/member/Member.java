package com.ssafy.oringe.api.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    private Long memberId;
    private String memberEmail;
    private String memberNickName;

    // Getter 메서드들
    public Long getMemberId() {
        return memberId;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public String getMemberNickName() {
        return memberNickName;
    }
}
