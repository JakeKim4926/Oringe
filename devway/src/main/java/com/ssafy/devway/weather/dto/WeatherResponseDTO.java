package com.ssafy.devway.weather.dto;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class WeatherResponseDTO {

    private List<Weather> weather;
    private Main main;

    @Data
    public static class Weather {
        private String main;
        private String description;
    }
    @Data
    public static class Main {
        private double temp;

    }
}
