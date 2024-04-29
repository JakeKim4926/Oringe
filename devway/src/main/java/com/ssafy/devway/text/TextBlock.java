package com.ssafy.devway.text;

import static com.ssafy.devway.text.CheckerMode.ALLOWED_ALL;
import static com.ssafy.devway.text.CheckerMode.ALLOWED_ENGLISH_ALL;
import static com.ssafy.devway.text.CheckerMode.ALLOWED_ENGLISH_LOWERCASE;
import static com.ssafy.devway.text.CheckerMode.ALLOWED_ENGLISH_UPPERCASE;
import static com.ssafy.devway.text.CheckerMode.ALLOWED_KOREAN;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextBlock {

  private String content;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Boolean stringChecker(int check_mode) {
    CheckerMode mode = CheckerMode.fromInt(check_mode);
    if (mode == null) {
      return false;  // 유효하지 않은 mode 값을 처리
    }

    switch (mode) {
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
      default:
        return false;  // 예외 처리
    }
  }

  public boolean isOnlyLowercaseEnglish() {
    Pattern pattern = Pattern.compile("^[a-z]+$");
    Matcher matcher = pattern.matcher(content);
    return matcher.matches();
  }

  public boolean isOnlyUppercaseEnglish( ) {
    Pattern pattern = Pattern.compile("^[A-Z]+$");
    Matcher matcher = pattern.matcher(content);
    return matcher.matches();
  }

  public boolean isOnlyEnglish() {
    Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
    Matcher matcher = pattern.matcher(content);
    return matcher.matches();
  }

  public boolean isOnlyKorean( ) {
    Pattern pattern = Pattern.compile("^[가-힣]+$");
    Matcher matcher = pattern.matcher(content);
    return matcher.matches();
  }

}
