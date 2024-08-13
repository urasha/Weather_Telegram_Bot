package ru.urasha.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class WeatherDataCollector {

    @Value("${base-url}")
    private String baseURL;

    @Value("${api-key}")
    private String apiKey;

    public JsonNode getWeatherParams(String location) throws URISyntaxException, IOException, InterruptedException {
        URI uri = new URI(String.format("%s%s?key=%s", baseURL, location, apiKey));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readTree(response.body());
    }
}
