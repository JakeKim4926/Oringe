package com.ssafy.devway.book;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ssafy.devway.block.element.BlockElement;
import com.ssafy.devway.book.dto.BookResponseDTO;
import com.ssafy.devway.book.property.BookProperties;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class BookBlock implements BlockElement {

    @Override
    public String getName() {
        return "BOOK";
    }

    @Getter
    private BookResponseDTO responseDTO;

    private final BookProperties properties;
    private final OkHttpClient httpClient;
    private final Gson gson;

    public BookBlock(String API_KEY) {
        this.properties = new BookProperties();
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();

        properties.setApiKey(API_KEY);
    }

    public void askBookInfo(String query, BookSortMode sort, int page, int size,
        BookTargetMode target) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + properties.getApiKey());

        HttpEntity<String> httpEntity = new HttpEntity<>(headers); //엔티티로 만들기
        URI targetUrl = UriComponentsBuilder
            .fromUriString(properties.getUrl()) //기본 url
            .queryParam("query", query) //인자
            .queryParam("sort", sort.sortMode)
            .queryParam("page", page)
            .queryParam("size", size)
            .queryParam("target", target.targetMode)
            .build()
            .encode(StandardCharsets.UTF_8) //인코딩
            .toUri();

        ResponseEntity<Map> result = restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, Map.class);



//        Response response = null;
//        try {
//            response = httpClient.newCall(request).execute();
//            System.out.println("response: " + response);
//            if (response.isSuccessful() && response.body() != null) {
//                String responseBody = response.body().string();
//                BookResponseDTO bookReponse = gson.fromJson(responseBody, BookResponseDTO.class);
//                System.out.println(bookReponse);
//            } else {
//                throw new RuntimeException(
//                    "Failed to communicate with KakaoAPI: " + response.message());
//            }
//        } catch (Exception e) {
//            if (response != null) {
//                System.out.println();
//                try {
//                    String msg = "HTTP status code: " + response.code() +
//                        "HTTP response body: " + (response.body() != null ? response.body().string()
//                        : "null");
//                    System.out.println(msg);
//                } catch (IOException ex) {
//                    System.out.println("Failed to read response body");
//                }
//            } else {
//                System.out.println("Error during API request: " + e.getMessage());
//            }
//        }
    }
}
