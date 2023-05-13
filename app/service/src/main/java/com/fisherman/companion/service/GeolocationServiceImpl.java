package com.fisherman.companion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fisherman.companion.dto.Geolocation;
import com.fisherman.companion.dto.Predictions;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.PredictionResponse;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.service.exception.RequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeolocationServiceImpl implements GeolocationService {

    @Value("${google.api.key}")
    private String apiKey;

    @Override
    public GenericListResponse<String> getAutocompleteSettlements(final String request) {
        final String url = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/place/autocomplete/json")
                                         .queryParam("input", request)
                                         .queryParam("types", "(cities)")
                                         .queryParam("components", "country:ua")
                                         .queryParam("key", apiKey)
                                         .toUriString();

        final WebClient client = WebClient.create();
        final PredictionResponse response = client.get().uri(url).retrieve().bodyToMono(PredictionResponse.class).block();
        final List<Predictions> predictionsList = Optional.ofNullable(response).map(this::getPredictions).orElse(List.of());

        final List<String> cityList = predictionsList.stream()
                                               .map(Predictions::description)
                                               .toList();

        return GenericListResponse.of(cityList);
    }

    private List<Predictions> getPredictions(final PredictionResponse predictionResponse) {
        return Optional.of(predictionResponse)
                       .filter(response -> response.status().equals("OK"))
                       .map(PredictionResponse::predictions)
                       .orElseThrow(() -> new RequestException(ResponseStatus.UNABLE_TO_GET_SETTLEMENTS.getCode()));
    }

    @Override
    public Geolocation getCoordinates(final String settlementName) {
        final String formattedSettlement = replaceSpacesWithPlus(settlementName);

        final String url = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/geocode/json")
                                         .queryParam("address", formattedSettlement)
                                         .queryParam("components", "country:ua")
                                         .queryParam("key", apiKey)
                                         .toUriString();

        final WebClient client = WebClient.create();

        JsonNode response = client.get().uri(url).retrieve().bodyToMono(JsonNode.class).block();

        return getGeolocation(response);
    }

    public String replaceSpacesWithPlus(final String input) {
        return input.replace(" ", "+");
    }

    private Geolocation getGeolocation(final JsonNode response) {
        return Optional.ofNullable(response)
                       .filter(r -> r.get("status").asText().equals("OK"))
                       .map(r -> r.get("results").get(0).get("geometry"))
                       .map(this::getCoordinatesFromGeometry)
                       .orElseThrow(() -> new RequestException(ResponseStatus.UNABLE_TO_GET_SETTLEMENTS.getCode()));
    }

    private Geolocation getCoordinatesFromGeometry(final JsonNode geometryNode) {
        final JsonNode locationNode = geometryNode.get("location");
        final Double lat = locationNode.get("lat").asDouble();
        final Double lng = locationNode.get("lng").asDouble();

        return new Geolocation(lat, lng);
    }

    @Override
    public String getSettlementName(final Double lat, final Double lng) {
        final String url = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/geocode/json")
                                               .queryParam("latlng", lat + "," + lng)
                                               .queryParam("result_type", "locality")
                                               .queryParam("key", apiKey)
                                               .toUriString();

        final WebClient client = WebClient.create();
        final JsonNode response = client.get().uri(url).retrieve().bodyToMono(JsonNode.class).block();

        return Optional.ofNullable(response)
                       .map(r -> r.get("results"))
                       .filter(results -> results.isArray() && results.size() > 0)
                       .map(results -> results.get(0))
                       .map(result -> result.get("formatted_address"))
                       .map(JsonNode::asText)
                       .orElseThrow(() -> new RequestException(ResponseStatus.UNABLE_TO_GET_SETTLEMENT_FROM_COORDINATES.getCode()));
    }
}
