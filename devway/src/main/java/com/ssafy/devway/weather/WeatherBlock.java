package com.ssafy.devway.weather;

import com.google.gson.Gson;
import com.ssafy.devway.GPT.dto.ChatResponseDTO;
import com.ssafy.devway.GPT.property.ChatgptProperties;
import com.ssafy.devway.block.element.BlockElement;
import com.ssafy.devway.weather.dto.WeatherRequestDTO;
import com.ssafy.devway.weather.dto.WeatherResponseDTO;
import com.ssafy.devway.weather.property.WeatherProperties;
import java.io.IOException;
import java.net.URLEncoder;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.web.client.RestTemplate;

public class WeatherBlock implements BlockElement {

    @Getter
    private String weather;

    @Getter
    private String description;

    @Getter
    private double temperature;

    private final WeatherProperties properties;

    public WeatherBlock(String API_KEY) {
        this.properties = new WeatherProperties();

        properties.setApiKey(API_KEY);
    }

    @Override
    public String getName() {
        return "weather";
    }

    /**
     * 현재 날씨 불러오기
     * @param city
     * @param weatherCountry
     */
    public void todayWeather(String city, WeatherCountry weatherCountry) {
        String selectedCity = city + weatherCountry;
        StringBuilder urlBuilder = new StringBuilder(properties.getNowUrl());
        try {
            urlBuilder.append("?" + URLEncoder.encode("q", "UTF-8") + "=" + selectedCity);
            urlBuilder.append(
                "&" + URLEncoder.encode("appid", "UTF-8") + "=" + properties.getApiKey());
            urlBuilder.append("&" + URLEncoder.encode("lang", "UTF-8") + "=kr");
            urlBuilder.append("&" + URLEncoder.encode("units", "UTF-8") + "=metric");

            RestTemplate restTemplate = new RestTemplate();
            WeatherResponseDTO response = restTemplate.getForObject(urlBuilder.toString(), WeatherResponseDTO.class);
            if (response != null && response.getWeather() != null && !response.getWeather().isEmpty()) {
                weather = response.getWeather().get(0).getMain();
                description = response.getWeather().get(0).getDescription();
                temperature = response.getMain().getTemp();
            }
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
