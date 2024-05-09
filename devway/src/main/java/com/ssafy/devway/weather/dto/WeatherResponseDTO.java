package com.ssafy.devway.weather.dto;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class WeatherResponseDTO {

    private List<Weather> weather;
    private Main main;
    private String name;
}
