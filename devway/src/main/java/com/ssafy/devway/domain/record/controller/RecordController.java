package com.ssafy.devway.domain.record.controller;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.record.document.Record;
import com.ssafy.devway.domain.record.dto.request.RecordCreateReqDto;
import com.ssafy.devway.domain.record.dto.response.CalendarRecordResDto;
import com.ssafy.devway.domain.record.repository.RecordRespository;
import com.ssafy.devway.domain.record.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oringe/api")
@Tag(name = "인증", description = "Record API")
public class RecordController {

  private final RecordService recordService;

  @PostMapping("/record")
  @Operation(summary = "인증 생성")
  public ResponseEntity<?> postRecord(@RequestBody RecordCreateReqDto recordCreateReqDto) {
    return recordService.insertRecord(recordCreateReqDto);
  }

  @GetMapping("/record/challenge")
  @Operation(summary = "특정 챌린지의 월 별 인증 전체 조회")
  public ResponseEntity<List<CalendarRecordResDto>> getCalendarRecord(
      @RequestParam Long memberId,
      @RequestParam Long challengeId,
      @RequestParam int month) {
    return recordService.selectCalendarRecord(memberId, challengeId, month);
  }

  @GetMapping("/record")
  @Operation(summary = "인증 상세 조회")
  public ResponseEntity<Record> getRecord(@RequestParam Long recordId) {
    return recordService.selectRecord(recordId);
  }

  @GetMapping("/record/success")
  @Operation(summary = "인증 성공 여부 조회")
  public ResponseEntity<?> getSuccess(@RequestParam Long recordId) {
    return recordService.getSuccess(recordId);
  }

  @PatchMapping("/record/success")
  @Operation(summary = "인증 성공 하기")
  public ResponseEntity<?> setSuccess(@RequestParam Long recordId) {
    return recordService.setSuccess(recordId);
  }

  @GetMapping("/record/templates")
  @Operation(summary = "인증 내용 조회")
  public ResponseEntity<?> getTemplates(@RequestParam Long recordId) {
    return recordService.getTemplates(recordId);
  }
}
