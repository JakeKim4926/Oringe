package com.ssafy.devway.book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.devway.block.element.BlockElement;
import com.ssafy.devway.book.dto.BookResponseDTO;
import com.ssafy.devway.book.dto.kakao.Document;
import com.ssafy.devway.book.dto.kakao.KakaoResponseDTO;
import com.ssafy.devway.book.dto.kakao.Meta;
import com.ssafy.devway.book.property.BookProperties;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class BookBlock implements BlockElement {

    @Override
    public String getName() {
        return "BOOK";
    }

    @Getter
    private List<BookResponseDTO> bookList;
    @Getter
    private Meta howManyBook;

    private final BookProperties properties;

    public BookBlock(String API_KEY) {
        this.properties = new BookProperties();

        properties.setApiKey(API_KEY);
    }

    public void searchBookList(String query, BookMode sort, BookMode target) {
        int page = properties.getPage();
        int size = properties.getSize();

        try {
            if (page > 50) {
                throw new IllegalStateException("page is over");
            }
            if (size > 50) {
                throw new IllegalStateException("size is over");
            }

            List<Document> kakaoList = new ArrayList<>();
            bookList = new ArrayList<>();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + properties.getApiKey());

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            URI targetUrl = UriComponentsBuilder
                .fromUriString(properties.getUrl())
                .queryParam("query", query)
                .queryParam("sort", sort.textMode)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("target", target)
                .build()
                .encode(StandardCharsets.UTF_8) //인코딩
                .toUri();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> result = restTemplate.exchange(targetUrl, HttpMethod.GET,
                httpEntity,
                String.class);

            ObjectMapper mapper = new ObjectMapper();
            KakaoResponseDTO bookInfo = mapper.readValue(result.getBody(), KakaoResponseDTO.class);
            if (bookInfo == null) {
                throw new IllegalStateException("bookInfo is null");
            }

            kakaoList = bookInfo.getDocuments();
            if (bookInfo.getMeta().getPageable_count() == 0) {
                throw new IllegalStateException("search result is none");
            }

            howManyBook = bookInfo.getMeta();
            bookList = new ArrayList<>();
            for (Document document : kakaoList) {
                BookResponseDTO dto = BookResponseDTO.builder()
                    .title(document.getTitle())
                    .authors(document.getAuthors())
                    .isbn(document.getIsbn())
                    .price(document.getPrice())
                    .datetime(document.getDatetime())
                    .publisher(document.getPublisher())
                    .thumnail(document.getThumbnail())
                    .build();
                bookList.add(dto);
            }

        } catch (HttpClientErrorException e) {
            HttpStatusCode code = e.getStatusCode();
            if (code == HttpStatus.UNAUTHORIZED) {
                System.out.println("REST API KEY를 다시 확인해주세요.");
            } else if (code == HttpStatus.BAD_REQUEST) {
                System.out.println("검색어(query)를 알맞게 입력해주세요.");
            }
        } catch (IllegalStateException e) {
            String msg = e.getMessage();
            if (msg.equals("page is over")) {
                System.out.println("페이지 번호는 최대 50까지만 가능합니다.");
            }
            if (msg.equals("size is over")) {
                System.out.println("한 페이지당 문서 수는 최대 50까지만 가능합니다.");
            }
            if (msg.equals("search result is none")) {
                System.out.println("검색 결과가 없습니다.");
            }
            if (msg.equals("bookInfo is null")) {
                System.out.println("검색에 실패했습니다.");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
