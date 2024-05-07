package com.ssafy.devway.domain.record.service;

import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_AUDIO;
import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_CONTENT;
import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_IMAGE;
import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_STT;
import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_TITLE;
import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_TTS;
import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_VIDEO;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.challenge.repository.ChallengeRepository;
import com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders;
import com.ssafy.devway.domain.challengeDetail.document.ChallengeDetail;
import com.ssafy.devway.domain.challengeDetail.repository.ChallengeDetailRepository;
import com.ssafy.devway.domain.challengeDetail.service.ChallengeDetailService;
import com.ssafy.devway.domain.member.document.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import com.ssafy.devway.domain.record.document.Record;
import com.ssafy.devway.domain.record.dto.request.RecordCreateReqDto;
import com.ssafy.devway.domain.record.dto.response.CalendarRecordResDto;
import com.ssafy.devway.domain.record.repository.RecordRespository;
import com.ssafy.devway.global.config.autoIncrementSequence.service.AutoIncrementSequenceService;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {

  private final ChallengeDetailService challengeDetailService;
  private final RecordRespository recordRespository;
  private final MemberRepository memberRepository;
  private final ChallengeRepository challengeRepository;
  private final AutoIncrementSequenceService autoIncrementSequenceService;
  private final ChallengeDetailRepository challengeDetailRepository;

  private String imgUrl = "";

  public ResponseEntity<?> insertRecord(RecordCreateReqDto dto) {
    Member member = memberRepository.findByMemberId(dto.getMemberId());
    Challenge challenge = challengeRepository.findByChallengeId(dto.getChallengeId());

    LocalDate today = LocalDate.now();

    List<Integer> templatesOrder = challengeDetailService.getTemplatesOrder(
        challenge.getChallengeDetail().getChallengeDetailId());

    for (int i = 0; i < dto.getRecordTemplates().size(); i++) {
      Boolean bConfirm = confirmTemplates(templatesOrder.get(i), dto.getRecordTemplates().get(i));
      if (!bConfirm) {
        return ResponseEntity.badRequest().build();
      }
    }

    Record record = Record.builder()
        .recordId(autoIncrementSequenceService.generateSequence(Record.SEQUENCE_NAME))
        .challenge(challenge)
        .member(member)
        .recordDate(today)
        .recordSuccess(false)
        .recordTemplates(dto.getRecordTemplates())
        .build();

    Record save = recordRespository.save(record);

    if (save == null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    return ResponseEntity.ok().build();
  }

  @Transactional(readOnly = true)
  public ResponseEntity<List<CalendarRecordResDto>> selectCalendarRecord(
      Long memberId,
      Long challengeId,
      int month) {
    List<Record> recordList = recordRespository.findByChallenge_ChallengeIdAndMember_MemberIdOrderByRecordDateAsc(
        challengeId, memberId);

    if (recordList.isEmpty()) {
      ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    List<CalendarRecordResDto> collect = recordList.stream()
        .filter(record -> record.getRecordDate().getMonthValue() == month)
        .map(this::convertToCalendarRecordResDto)
        .toList();

    return ResponseEntity.ok(collect);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<Record> selectRecord(Long recordId) {
    Record byRecordId = recordRespository.findByRecordId(recordId);
    if (byRecordId == null) {
      ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

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
    if (byRecordId == null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    return ResponseEntity.ok(byRecordId.getRecordTemplates());
  }

  public ResponseEntity<?> insertImage(MultipartFile file, String recordId) {
    String path = recordId + "_";
    try {
      // Save the file to a location
      Files.copy(file.getInputStream(), Paths.get(path + file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);

      imgUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
          .path("/files/")
          .path(file.getOriginalFilename())
          .toUriString();

      System.out.println(imgUrl);
      return ResponseEntity.ok(imgUrl);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Could not upload the file: " + file.getOriginalFilename() + "!");
    }
  }

  private CalendarRecordResDto convertToCalendarRecordResDto(Record record) {
    return new CalendarRecordResDto(
        record.getRecordId(),
        record.getRecordSuccess(),
        record.getRecordDate()
    );
  }

  private Boolean confirmTemplates(Integer challengerDetailIndex, String challengerDetailContent) {
    if (challengerDetailIndex == CHALLENGE_DETAIL_TITLE.getOrderCode()) {
      if(challengerDetailContent.length() >= 100)
        return false;
    } else if (challengerDetailIndex == CHALLENGE_DETAIL_CONTENT.getOrderCode()) {
      if(challengerDetailContent.length() >= 1000)
        return false;
    } else if (challengerDetailIndex == CHALLENGE_DETAIL_IMAGE.getOrderCode()) {
//      return false;
    } else if (challengerDetailIndex == CHALLENGE_DETAIL_AUDIO.getOrderCode()) {
//      return false;
    } else if (challengerDetailIndex == CHALLENGE_DETAIL_VIDEO.getOrderCode()) {
//      return false;
    } else if (challengerDetailIndex == CHALLENGE_DETAIL_STT.getOrderCode()) {
//      return false;
    } else if (challengerDetailIndex == CHALLENGE_DETAIL_TTS.getOrderCode()) {
//      return false;
    }

    return true;
  }

}
