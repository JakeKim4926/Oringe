package com.ssafy.devway.domain.member.repository;

import com.ssafy.devway.domain.member.document.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends MongoRepository<Member, Long> {
    Member findByMemberId(Long MemberId);

    Member findByMemberEmail(String MemberEmail);

}
