package com.ssafy.devway.domain.member.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Transient
    public static final String SEQUENCE_NAME = "member_sequence";

    @Id
    private Long memberId;

    private String memberNickName;

    private Boolean memberIsFirst;
}
