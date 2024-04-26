package com.ssafy.devway.domain.challenge.repository;

import com.ssafy.devway.domain.challenge.document.Challenge;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends MongoRepository<Challenge, Long> {

    Challenge findByChallengeId(Long challengeId);

    List<Challenge> findByMember_MemberIdOrderByChallengeIdDesc(Long memberId);

}
