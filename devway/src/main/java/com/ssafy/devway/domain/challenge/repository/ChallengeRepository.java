package com.ssafy.devway.domain.challenge.repository;

import com.ssafy.devway.domain.challenge.document.Challenge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends MongoRepository<Challenge, Long> {

}
