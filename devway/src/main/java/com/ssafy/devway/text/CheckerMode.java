package com.ssafy.devway.text;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
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


    private final int textMode;

    CheckerMode(int textMode) {
        this.textMode = textMode;
    }
    public int getTextMode() {
      return textMode;
    }
    // 주어진 정수에 해당하는 enum 인스턴스를 찾습니다.
//    public static CheckerMode fromInt(int textMode) {
//        for (CheckerMode mode : values()) {
//            if (mode.getTextMode() == textMode) {
//                return mode;
//            }
//        }
//        return null;  // 찾지 못하면 null을 반환
//    }
}
