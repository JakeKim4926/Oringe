package com.ssafy.devway.domain.challenge.controller;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.challenge.dto.request.ChallengeCreateReqDto;
import com.ssafy.devway.domain.challenge.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge")
@Tag(name = "챌린지", description = "Challenge API")
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping
    @Operation(summary = "챌린지 생성")
    public ResponseEntity<Challenge> postChallenge(
        @RequestBody ChallengeCreateReqDto dto) {
        System.out.println(dto);
        return ResponseEntity.ok(challengeService.insertChallenge(dto));
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "챌린지 목록 조회")
    public ResponseEntity<List<Challenge>> getChallengeList(@PathVariable Long memberId, @RequestParam int status) {
        return ResponseEntity.ok(challengeService.selectChallengeList(memberId, status));
    }

    @GetMapping("/today")
    @Operation(summary = "오늘 챌린지 목록 조회")
    public ResponseEntity<List<Challenge>> getTodayChallengeList(Long memberId) {
        return ResponseEntity.ok(challengeService.selectTodayChallengeList(memberId));
    }

    @DeleteMapping("/{challengeId}")
    @Operation(summary = "특정 챌린지 삭제")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long challengeId) {
        challengeService.deleteChallenge(challengeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/challengeStatus")
    @Operation(summary = "챌린지 진행 상태 스케줄링")
    public ResponseEntity<Void> putChallengeStatus() {
        challengeService.updateChallengeStatus();
        return ResponseEntity.ok().build();
    }

}
