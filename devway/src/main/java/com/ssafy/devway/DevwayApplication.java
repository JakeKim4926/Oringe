package com.ssafy.devway;

import com.ssafy.devway.block.block.Block;
import com.ssafy.devway.text.CheckerMode;
import com.ssafy.devway.text.TextBlock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevwayApplication {

  public static void main(String[] args) {

    TextBlock topic1 = new TextBlock();
    SpringApplication.run(DevwayApplication.class, args);}
}
