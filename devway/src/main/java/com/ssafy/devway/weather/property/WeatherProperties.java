package com.ssafy.devway.weather.property;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class WeatherProperties {
    private String apiKey;
    private String nowUrl = "https://api.openweathermap.org/data/2.5/weather"; //현재
    private String dateUrl = "https://api.openweathermap.org/data/2.5/forecast"; //5일 세시간
}
