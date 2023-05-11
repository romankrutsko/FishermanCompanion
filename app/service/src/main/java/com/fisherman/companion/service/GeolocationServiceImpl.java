package com.fisherman.companion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fisherman.companion.dto.Predictions;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.PredictionResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeolocationServiceImpl implements GeolocationService {

    @Value("${google.api.key}")
    private final String apiKey;

    public GenericListResponse<String> getAutocompleteSettlements(final String request) {
        String url = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/place/autocomplete/json")
                                         .queryParam("input", request)
                                         .queryParam("types", "(cities)")
                                         .queryParam("components", "country:ua")
                                         .queryParam("key", apiKey)
                                         .toUriString();

        WebClient client = WebClient.create();
        PredictionResponse response = client.get().uri(url).retrieve().bodyToMono(PredictionResponse.class).block();
        List<Predictions> predictionsList = response.predictions();

        List<String> cityList = predictionsList.stream()
                                               .map(Predictions::description)
                                               .toList();

        return GenericListResponse.of(cityList);
    }
}
