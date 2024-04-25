package com.ssafy.devway.global.config.autoIncrementSequence.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "database_sequences")
@Data
public class AutoIncrementSequence {

    @Id
    private String id;
    private long seq; // 증가량

}
