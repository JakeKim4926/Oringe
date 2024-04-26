package com.ssafy.devway.domain.record.service;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.challenge.repository.ChallengeRepository;
import com.ssafy.devway.domain.member.document.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import com.ssafy.devway.domain.record.document.Record;
import com.ssafy.devway.domain.record.dto.request.RecordCreateReqDto;
import com.ssafy.devway.domain.record.dto.response.CalendarRecordResDto;
import com.ssafy.devway.domain.record.repository.RecordRespository;
import com.ssafy.devway.global.config.autoIncrementSequence.service.AutoIncrementSequenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {

    private final RecordRespository recordRespository;
    private final MemberRepository memberRepository;
    private final ChallengeRepository challengeRepository;
    private final AutoIncrementSequenceService autoIncrementSequenceService;

    /*
     * 4.1 인증 생성
     * */
    public Record insertRecord(RecordCreateReqDto dto, Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        Challenge challenge = challengeRepository.findByChallengeId(dto.getChallengeId());
        LocalDate today = LocalDate.now();
        Record record = Record.builder()
            .recordId(autoIncrementSequenceService.generateSequence(Record.SEQUENCE_NAME))
            .challenge(challenge)
            .member(member)
            .recordDate(today)
            .recordSuccess(true)
            .recordTemplates(dto.getRecordTemplates())
            .build();

        recordRespository.save(record);

        return record;
    }

    /*
     * 4.2 특정 챌린지의 월 별 인증 전체 조회
     * */
    @Transactional(readOnly = true)
    public List<CalendarRecordResDto> selectCalendarRecord(Long challengeId, Long memberId,
        int month) {
        List<Record> recordList = recordRespository.findByChallenge_ChallengeIdAndMember_MemberIdOrderByRecordDateAsc(
            challengeId, memberId);
        System.out.println(recordList);
        return recordList.stream()
            .filter(record -> record.getRecordDate().getMonthValue() == month)
            .map(this::convertToCalendarRecordResDto)
            .collect(Collectors.toList());
    }

    /*
     * 4.3 특정 인증 상세 조회
     * */
    @Transactional(readOnly = true)
    public Record selectRecord(Long recordId) {
        return recordRespository.findByRecordId(recordId);
    }

    /*
     * 4.4 특정 인증 삭제
     * */
    public Long deleteRecord(Long recordId) {
        recordRespository.deleteById(recordId);
        return recordId;
    }

    private CalendarRecordResDto convertToCalendarRecordResDto(Record record) {
        return new CalendarRecordResDto(
            record.getRecordId(),
            record.getRecordSuccess(),
            record.getRecordDate()
        );
    }

}
