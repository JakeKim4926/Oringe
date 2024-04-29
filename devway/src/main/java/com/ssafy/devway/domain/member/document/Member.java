package com.ssafy.devway.domain.member.document;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Member {
    @Transient
    public static final String SEQUENCE_NAME = "member_sequence";

    @Id
    @NotNull
    private Long memberId;

    @NotNull
    private String memberEmail;

    @NotNull
    private String memberNickName;
}
