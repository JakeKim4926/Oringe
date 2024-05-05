package com.ssafy.devway.domain.record.service;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.challenge.repository.ChallengeRepository;
import com.ssafy.devway.domain.challengeDetail.document.ChallengeDetail;
import com.ssafy.devway.domain.challengeDetail.repository.ChallengeDetailRepository;
import com.ssafy.devway.domain.member.document.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import com.ssafy.devway.domain.record.document.Record;
import com.ssafy.devway.domain.record.dto.request.RecordCreateReqDto;
import com.ssafy.devway.domain.record.dto.response.CalendarRecordResDto;
import com.ssafy.devway.domain.record.repository.RecordRespository;
import com.ssafy.devway.global.config.autoIncrementSequence.service.AutoIncrementSequenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  private final ChallengeDetailRepository challengeDetailRepository;

  public ResponseEntity<?> insertRecord(RecordCreateReqDto dto) {
    Member member = memberRepository.findByMemberId(dto.getMemberId());
    Challenge challenge = challengeRepository.findByChallengeId(dto.getChallengeId());

    LocalDate today = LocalDate.now();

    Record record = Record.builder()
        .recordId(autoIncrementSequenceService.generateSequence(Record.SEQUENCE_NAME))
        .challenge(challenge)
        .member(member)
        .recordDate(today)
        .recordSuccess(false)
        .recordTemplates(dto.getRecordTemplates())
        .build();

    Record save = recordRespository.save(record);

    if(save == null)
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    return ResponseEntity.ok().build();
  }

  @Transactional(readOnly = true)
  public ResponseEntity<List<CalendarRecordResDto>> selectCalendarRecord(
      Long memberId,
      Long challengeId,
      int month) {
    List<Record> recordList = recordRespository.findByChallenge_ChallengeIdAndMember_MemberIdOrderByRecordDateAsc(
        challengeId, memberId);

    if(recordList.isEmpty())
      ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    List<CalendarRecordResDto> collect = recordList.stream()
        .filter(record -> record.getRecordDate().getMonthValue() == month)
        .map(this::convertToCalendarRecordResDto)
        .toList();

    return ResponseEntity.ok(collect);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<Record> selectRecord(Long recordId) {
    Record byRecordId = recordRespository.findByRecordId(recordId);
    if(byRecordId == null)
      ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    return ResponseEntity.ok(byRecordId);
  }

  public ResponseEntity<?> getSuccess(Long recordId) {
    Record byRecordId = recordRespository.findByRecordId(recordId);
    if (byRecordId == null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    return ResponseEntity.ok(byRecordId.getRecordSuccess());
  }

  public ResponseEntity<?> setSuccess(Long recordId) {
    Record byRecordId = recordRespository.findByRecordId(recordId);
    if (byRecordId == null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    byRecordId.setRecordSuccess(true);
    return ResponseEntity.ok().build();
  }

  public ResponseEntity<?> getTemplates(Long recordId) {
    Record byRecordId = recordRespository.findByRecordId(recordId);
    if(byRecordId == null)
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    return ResponseEntity.ok(byRecordId.getRecordTemplates());
  }

//  public ResponseEntity<?> textTemplate(Long memberId, Long challengeId, Long challengeDetailId) {
//    ChallengeDetail byChallengeDetailId = challengeDetailRepository.findByChallengeDetailId(
//        challengeDetailId);
//
//    return ResponseEntity.ok().build();
//  }

  private CalendarRecordResDto convertToCalendarRecordResDto(Record record) {
    return new CalendarRecordResDto(
        record.getRecordId(),
        record.getRecordSuccess(),
        record.getRecordDate()
    );
  }

}
