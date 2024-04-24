package com.ssafy.devway.domain.challenge.controller;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.challenge.repository.ChallengeRepository;
import com.ssafy.devway.global.config.autoIncrementSequence.service.AutoIncrementSequenceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class ChallengeController {

    private final ChallengeRepository challengeRepository;
    private final AutoIncrementSequenceService autoIncrementSequenceService;

    @PostMapping("/api/challenge")
    @Operation(summary = "챌린지 생성")
    public void addChallenge() {
        Challenge challenge = Challenge.builder()
            .challengeId(autoIncrementSequenceService.generateSequence(Challenge.SEQUENCE_NAME))
            .challengeTitle("dd")
            .build();
        challengeRepository.save(challenge);
    }

}
