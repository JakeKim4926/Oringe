package com.ssafy.devway.global.config.autoIncrementSequence.document;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "database_sequences")
@Getter @Setter
public class AutoIncrementSequence {
    @Id
    private String id;
    private long seq;

}
