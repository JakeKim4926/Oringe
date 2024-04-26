package com.ssafy.devway.domain.record.repository;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.record.document.Record;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRespository extends MongoRepository<Record, Long> {

    List<Record> findByChallenge_ChallengeIdAndMember_MemberIdOrderByRecordDateAsc(Long challengeId,
        Long memberId);

    Record findByRecordId(Long recordId);
}
