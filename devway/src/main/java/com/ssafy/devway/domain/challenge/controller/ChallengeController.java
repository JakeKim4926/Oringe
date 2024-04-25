package com.ssafy.devway.domain.challenge.controller;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.challenge.dto.request.ChallengeCreateReqDto;
import com.ssafy.devway.domain.challenge.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "챌린지", description = "Challenge API")
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping("/challenge")
    @Operation(summary = "챌린지 생성")
    public ResponseEntity<Challenge> postChallenge(
        @RequestBody ChallengeCreateReqDto dto, Long challengeDetailId,
        Long memberId) {
        return ResponseEntity.ok(
            challengeService.insertChallenge(dto, challengeDetailId, memberId));
    }

    @GetMapping("/challenge")
    @Operation(summary = "챌린지 목록 조회")
    public ResponseEntity<List<Challenge>> getChallengeList(Long memberId) {
        return ResponseEntity.ok(challengeService.selectChallengeList(memberId));
    }

    @GetMapping("/challenge/today")
    @Operation(summary = "오늘 챌린지 목록 조회")
    public ResponseEntity<List<Challenge>> getTodayChallengeList(Long memberId) {
        return ResponseEntity.ok(challengeService.selectTodayChallengeList(memberId));
    }

    @DeleteMapping("/challenge/{challengeId}")
    @Operation(summary = "특정 챌린지 삭제")
    public ResponseEntity<Long> deleteChallenge(@PathVariable Long challengeId) {
        return ResponseEntity.ok(challengeService.deleteChallenge(challengeId));
    }

    @PutMapping("/challenge/{challengeId}/challengeStatus")
    @Operation(summary = "특정 챌린지 진행 상태 업데이트")
    public ResponseEntity<Integer> putChallengeStatus(@PathVariable Long challengeId) {
        return ResponseEntity.ok(challengeService.updateChallengeStatus(challengeId));
    }

}
