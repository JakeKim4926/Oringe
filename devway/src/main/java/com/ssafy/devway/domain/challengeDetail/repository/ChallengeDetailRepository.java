package com.ssafy.devway.domain.challengeDetail.repository;

import com.ssafy.devway.domain.challengeDetail.document.ChallengeDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeDetailRepository extends MongoRepository<ChallengeDetail, Long> {
  ChallengeDetail findByChallengeDetailId(Long challengeDetailId);
}
