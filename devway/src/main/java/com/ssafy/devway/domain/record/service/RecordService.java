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
import com.ssafy.devway.domain.challengeDetail.repository.ChallengeDetailRepository;
import com.ssafy.devway.domain.challengeDetail.service.ChallengeDetailService;
import com.ssafy.devway.domain.member.document.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import com.ssafy.devway.domain.record.document.Record;
import com.ssafy.devway.domain.record.dto.request.RecordCreateReqDto;
import com.ssafy.devway.domain.record.dto.response.CalendarRecordResDto;
import com.ssafy.devway.domain.record.dto.response.RecordTemplateDto;
import com.ssafy.devway.domain.record.repository.RecordRespository;
import com.ssafy.devway.global.config.autoIncrementSequence.service.AutoIncrementSequenceService;
import com.ssafy.devway.text.TextBlock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.w3c.dom.Text;

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
  private RecordTemplateDto recordTemplateDto = new RecordTemplateDto();

  public ResponseEntity<?> insertRecord(RecordCreateReqDto dto) {
    Member member = memberRepository.findByMemberId(dto.getMemberId());
    Challenge challenge = challengeRepository.findByChallengeId(dto.getChallengeId());

    LocalDate today = LocalDate.now();

    List<Integer> templatesOrder = challengeDetailService.getTemplatesOrder(
        challenge.getChallengeDetail().getChallengeDetailId());

    for (int order = 0; order < dto.getRecordTemplates().size(); order++) {
      Boolean bConfirm = confirmTemplates(templatesOrder.get(order), dto.getRecordTemplates());
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
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

  public ResponseEntity<?> insertRecordText(String title, int limitLength) {
    TextBlock textBlock = new TextBlock(title);

    String blank = title.replaceAll("\\s+", "");
    blank = blank.replaceAll("\"", "");          // Remove double quotation marks

    if(textBlock.getContent().length() >= limitLength)
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("제목은 " + String.valueOf(limitLength) + "자 이상 입력이 불가능 합니다.");
    if(blank.isEmpty())
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("공백 문자만 입력이 되었습니다.");
//    if(textBlock.containsSpecialCharacters())
//      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("특수 문자가 포함되어 있습니다.");
    return ResponseEntity.ok(textBlock.getContent());
  }

  public ResponseEntity<?> insertRecordFile(MultipartFile file, String challengeTemplate) {
    String timestamp = LocalDateTime.now().toString().substring(0, 19).replace(":", "-"); // Windows에서 ':' 문자가 파일명에 사용될 수 없음
    String directoryPath = "static/" + challengeTemplate + "/";

    String filename = timestamp + "_" + challengeTemplate + "_" + file.getOriginalFilename();
    String fullPath = directoryPath + filename;
    try {
      // Save path
      Path fileCreate = Paths.get(directoryPath);
      Files.createDirectories(fileCreate);

      // Save the file to a location
      Files.copy(file.getInputStream(), Paths.get(fullPath), StandardCopyOption.REPLACE_EXISTING);

      String imgUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
          .path("/files/")
          .path(file.getName())
          .toUriString();

      recordTemplateDto.setRecordImage(imgUrl);

      return ResponseEntity.ok(fullPath);
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

  private Boolean confirmTemplates(Integer challengeDetailIndex, List<String> challengeDetailContent) {
    if (challengeDetailIndex == CHALLENGE_DETAIL_TITLE.getOrderCode()) {
      if(recordTemplateDto.getRecordTitle().length() >= 100 || recordTemplateDto.getRecordTitle().isEmpty())
        return false;

      challengeDetailContent.add(recordTemplateDto.getRecordTitle());
    } else if (challengeDetailIndex == CHALLENGE_DETAIL_CONTENT.getOrderCode()) {
      if(recordTemplateDto.getRecordContent().length() >= 1000 || recordTemplateDto.getRecordContent().isEmpty())
        return false;

      challengeDetailContent.add(recordTemplateDto.getRecordContent());
    } else if (challengeDetailIndex == CHALLENGE_DETAIL_IMAGE.getOrderCode()) {
      if(recordTemplateDto.getRecordImage() == null || recordTemplateDto.getRecordImage().isEmpty())
        return false;

      challengeDetailContent.add(recordTemplateDto.getRecordImage());
    } else if (challengeDetailIndex == CHALLENGE_DETAIL_AUDIO.getOrderCode()) {
      if(recordTemplateDto.getRecordAudio() == null || recordTemplateDto.getRecordAudio().isEmpty())
        return false;

      challengeDetailContent.add(recordTemplateDto.getRecordAudio());
    } else if (challengeDetailIndex == CHALLENGE_DETAIL_VIDEO.getOrderCode()) {
      if(recordTemplateDto.getRecordVideo() == null || recordTemplateDto.getRecordVideo().isEmpty())
        return false;

      challengeDetailContent.add(recordTemplateDto.getRecordVideo());
    } else if (challengeDetailIndex == CHALLENGE_DETAIL_STT.getOrderCode()) {
      if(recordTemplateDto.getRecordSTT() == null || recordTemplateDto.getRecordSTT().isEmpty())
        return false;

      challengeDetailContent.add(recordTemplateDto.getRecordVideo());
    } else if (challengeDetailIndex == CHALLENGE_DETAIL_TTS.getOrderCode()) {
      if(recordTemplateDto.getRecordTTS() == null || recordTemplateDto.getRecordTTS().isEmpty())
        return false;

      challengeDetailContent.add(recordTemplateDto.getRecordVideo());
    }

    return true;
  }

}
