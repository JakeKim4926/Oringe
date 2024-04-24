package com.ssafy.devway.global.config.autoIncrementSequence.service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.ssafy.devway.global.config.autoIncrementSequence.document.AutoIncrementSequence;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutoIncrementSequenceService {

    private final MongoOperations mongoOperations;

    public Long generateSequence(String seqName) {
        AutoIncrementSequence sequence = mongoOperations.findAndModify(
            query(where("_id").is(seqName)),
            new Update().inc("seq", 1), options().returnNew(true).upsert(true),
            AutoIncrementSequence.class);

        return !Objects.isNull(sequence) ? sequence.getSeq() : 1;
    }

}
