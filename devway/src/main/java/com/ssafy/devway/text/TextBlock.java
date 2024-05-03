package com.ssafy.devway.text;

import static com.ssafy.devway.text.CheckerMode.ALLOWED_ALL;
import static com.ssafy.devway.text.CheckerMode.ALLOWED_ENGLISH_ALL;
import static com.ssafy.devway.text.CheckerMode.ALLOWED_ENGLISH_LOWERCASE;
import static com.ssafy.devway.text.CheckerMode.ALLOWED_ENGLISH_UPPERCASE;
import static com.ssafy.devway.text.CheckerMode.ALLOWED_KOREAN;

import com.ssafy.devway.block.element.BlockElement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
public class TextBlock implements BlockElement {

    private String content;
    private int length_limit = 100000;

    public TextBlock(String content) {
        this.content = content;
    }

    public String getName() {
        return "TEXT";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content.length() <= length_limit)
            this.content = content;
        else {
            System.out.println("입력된 문자열이 길이 제한을 초과합니다.");
        }
    }
    public void setLengthLimit(int length_limit) {
        this.length_limit = length_limit;
    }

    public int getLengthLimit() {
        return length_limit;
    }
    public int getContentLength() {
        return content.length();
    }
    public boolean isLengthOver(int len){
        return content.length() >= len;
    }

    // 오직 한글만 있는지 검사
    public boolean onlyKorean() {
        return content.matches("^[가-힣]+$");
    }

    // 오직 영어만 있는지 검사
    public boolean onlyEnglish() {
        return content.matches("^[a-zA-Z]+$");
    }

    // 오직 숫자만 있는지 검사
    public boolean onlyNumber() {
        return content.matches("^[0-9]+$");
    }

    // 특수문자를 포함하고 있는지 검사
    public boolean containsSpecialCharacters() {
        return content.matches(".*[^a-zA-Z0-9\\s].*");
    }

    // 사용자가 입력한 문자가 몇 개 포함되어 있는지 검사
    public int countChar(char ch) {
        int count = 0;
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }
    public boolean matchesPattern(String regex) {
        return content.matches(regex);
    }

//    public Boolean stringChecker(CheckerMode check_mode) { //블랙리스트 방식으로
//        switch (check_mode) {
//            case ALLOWED_ALL:
//                return true;
//            case ALLOWED_ENGLISH_LOWERCASE:
//                return isOnlyLowercaseEnglish();
//            case ALLOWED_ENGLISH_UPPERCASE:
//                return isOnlyUppercaseEnglish();
//            case ALLOWED_ENGLISH_ALL:
//                return isOnlyEnglish();
//            case ALLOWED_KOREAN:
//                return isOnlyKorean();
//            case ALLOWED_BLANK:
//                return isBlank();
//            case ALLOWED_SPECIAL_CHARACTER:
//                return containsSpecialCharacters();
//            case DENIED_SPECIAL_CHARACTER:
//                return doesNotContainSpecialCharacters();
//            case DENIED_CHARACTER:
//                return containsNoCharacters();
//            default:
//                return false;  // 예외 처리
//        }
//    }
//
//    public Boolean isOnlyLowercaseEnglish() {
//        return content.matches("^[a-z]+$");
//    }
//
//    public boolean isOnlyUppercaseEnglish() {
//        return content.matches("^[A-Z]+$");
//    }
//
//    public boolean isOnlyEnglish() {
//        return content.matches("^[a-zA-Z]+$");
//    }
//
//    public boolean isOnlyKorean() {
//        return content.matches("^[가-힣]+$");
//    }
//
//    public boolean isBlank() {
//        return content.trim().isEmpty();
//    }
//
//    public boolean containsSpecialCharacters() {
//        return content.matches(".*[^a-zA-Z0-9\\s].*");
//    }
//
//    public boolean doesNotContainSpecialCharacters() {
//        return !containsSpecialCharacters();
//    }
//
//    public boolean containsNoCharacters() {
//        return content.isEmpty();
//    }

}
