package com.ssafy.devway.book;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookMode {

    SORT_BY_ACCURACY("accuracy"),
    SORT_BY_LATEST("latest"),
    TARGET_IS_TITLE("title"),
    TARGET_IS_ISBN("isbn"),
    TARGET_IS_PUBLISHER("publisher"),
    TARGET_IS_PERSON("person");

    public final String textMode;
}
