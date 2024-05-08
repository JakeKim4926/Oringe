package com.ssafy.devway.weather.dto;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class ForecastResponseDTO {

    private List<Forecast> list;
    private City city;

    @Data
    public static class Main {
        private double temp;
        private double temp_min;
        private double temp_max;
    }

    @Data
    public static class Forecast {
        private String dt_txt;
        private Main main;
        private double pop;
    }

    @Data
    public static class City {
        private String name;
    }
}
