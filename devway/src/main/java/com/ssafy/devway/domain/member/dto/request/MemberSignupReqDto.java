package com.ssafy.devway.domain.member.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class MemberSignupReqDto {

    String memberNickname;
    String memberEmail;

}
