package com.ssafy.devway.text;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CheckerMode {

    ALLOWED_ALL(1),
    ALLOWED_ENGLISH_LOWERCASE(2),
    ALLOWED_ENGLISH_UPPERCASE(3),
    ALLOWED_ENGLISH_ALL(4),
    ALLOWED_KOREAN(5),
    ALLOWED_BLANK(6),
    ALLOWED_SPECIAL_CHARACTER(7),
    DENIED_SPECIAL_CHARACTER(8),
    DENIED_CHARACTER(9);

    public final int textMode;
}
