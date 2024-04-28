package com.ssafy.devway.domain.member.service;

import com.ssafy.devway.domain.member.document.Member;
import com.ssafy.devway.domain.member.dto.request.MemberSignupReqDto;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import com.ssafy.devway.domain.record.document.Record;
import com.ssafy.devway.global.config.autoIncrementSequence.service.AutoIncrementSequenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AutoIncrementSequenceService autoIncrementSequenceService;

    /*
     * 1.1 구글 가입
     * */
    public Member signup(MemberSignupReqDto dto) {
        Member member = memberRepository.findByMemberEmail(dto.getMemberEmail());

        if(member != null){
            throw new IllegalArgumentException("이미 가입 된 이메일입니다.");
        }

        Member newMember = Member.builder()
            .memberId(autoIncrementSequenceService.generateSequence(Member.SEQUENCE_NAME))
            .memberNickName(dto.getMemberNickname())
            .memberEmail(dto.getMemberEmail())
            .build();

        memberRepository.save(newMember);
        return member;
    }



    /*
     * 1.2 로그인 회원 조회
     * */
    public Member signin(String email) {
        Member member = memberRepository.findByMemberEmail(email);
        if(member == null){
            throw new IllegalArgumentException("해당 회원을 찾을 수 없습니다.");
        }

        return member;
    }

    public Boolean validMember(String memberEmail) {
        Member findedMember = signin(memberEmail);
        return findedMember != null;
    }

}
