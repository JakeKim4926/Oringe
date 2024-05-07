package com.ssafy.devway;

import com.ssafy.devway.GPT.GPTBlock;
import com.ssafy.devway.GPT.GPTMode;
import com.ssafy.devway.block.block.Block;
import com.ssafy.devway.book.BookBlock;
import com.ssafy.devway.book.BookSortMode;
import com.ssafy.devway.book.BookTargetMode;
import com.ssafy.devway.text.CheckerMode;
import com.ssafy.devway.text.TextBlock;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevwayApplication {

  public static void main(String[] args) throws IOException {
    SpringApplication.run(DevwayApplication.class, args);
    BookBlock BOOK = new BookBlock("97731ebd91d2c36602f25a44bf37a09d");
    BOOK.askBookInfo("해리", BookSortMode.BOOK_SORT_BY_ACCURACY, 1, 3, BookTargetMode.BOOK_TARGET_IS_TITLE);
    System.out.println(BOOK.getResponseDTO());
//    GPTBlock GPT = new GPTBlock("sk-proj-xe7g0CQdvSdmKZPTJPbGT3BlbkFJoEfOvwHmhjcKdGVMFiB2");
//    GPT.askQuestion("영화", GPTMode.GPT_TALK_START_ENGLISH);
//    System.out.println(GPT.getLastAnswer());
  }
}
