package com.ssafy.devway.text;

import static com.ssafy.devway.text.CheckerMode.ALLOWED_ALL;
import static com.ssafy.devway.text.CheckerMode.ALLOWED_ENGLISH_ALL;
import static com.ssafy.devway.text.CheckerMode.ALLOWED_ENGLISH_LOWERCASE;
import static com.ssafy.devway.text.CheckerMode.ALLOWED_ENGLISH_UPPERCASE;
import static com.ssafy.devway.text.CheckerMode.ALLOWED_KOREAN;

import com.ssafy.devway.block.element.BlockElement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.ToString;

@ToString
public class TextBlock implements BlockElement {

  private String content;

  public String getName() {
    return "TEXT";
  }
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Boolean stringChecker(CheckerMode check_mode) { //블랙리스트
    switch (check_mode) {
      case ALLOWED_ALL:
        return true;
      case ALLOWED_ENGLISH_LOWERCASE:
        return isOnlyLowercaseEnglish();
      case ALLOWED_ENGLISH_UPPERCASE:
        return isOnlyUppercaseEnglish();
      case ALLOWED_ENGLISH_ALL:
        return isOnlyEnglish();
      case ALLOWED_KOREAN:
        return isOnlyKorean();
      case ALLOWED_BLANK:
        return isBlank();
      case ALLOWED_SPECIAL_CHARACTER:
        return containsSpecialCharacters();
      case DENIED_SPECIAL_CHARACTER:
        return doesNotContainSpecialCharacters();
      case DENIED_CHARACTER:
        return containsNoCharacters();
      default:
        return false;  // 예외 처리
    }
  }
  public Boolean isOnlyLowercaseEnglish() {
    return content.matches("^[a-z]+$");
  }

  public boolean isOnlyUppercaseEnglish() {
    return content.matches("^[A-Z]+$");
  }

  public boolean isOnlyEnglish() {
    return content.matches("^[a-zA-Z]+$");
  }

  public boolean isOnlyKorean() {
    return content.matches("^[가-힣]+$");
  }

  public boolean isBlank() {
    return content.trim().isEmpty();
  }

  public boolean containsSpecialCharacters() {
    return content.matches(".*[^a-zA-Z0-9\\s].*");
  }

  public boolean doesNotContainSpecialCharacters() {
    return !containsSpecialCharacters();
  }

  public boolean containsNoCharacters() {
    return content.isEmpty();
  }

}
