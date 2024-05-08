package com.ssafy.devway;

import com.ssafy.devway.book.BookBlock;
import com.ssafy.devway.book.BookMode;
import com.ssafy.devway.book.dto.BookResponseDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevwayApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(DevwayApplication.class, args);
    }
}
