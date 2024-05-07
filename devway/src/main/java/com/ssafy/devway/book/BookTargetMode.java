package com.ssafy.devway.book;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookTargetMode {
    BOOK_TARGET_IS_TITLE("title"),
    BOOK_TARGET_IS_ISBN("isbn"),
    BOOK_TARGET_IS_PUBLISHER("publisher"),
    BOOK_TARGET_IS_PERSON("person");

    public final String targetMode;
}