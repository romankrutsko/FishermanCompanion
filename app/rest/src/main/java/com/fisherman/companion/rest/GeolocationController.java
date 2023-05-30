package com.fisherman.companion.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisherman.companion.dto.Geolocation;
import com.fisherman.companion.dto.request.SettlementRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.service.GeolocationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/location")
public class GeolocationController {
    private final GeolocationService geolocationService;

    @PostMapping
    Geolocation getSettlementCoordinates(@RequestBody final SettlementRequest request) {
        return geolocationService.getCoordinates(request.settlement());
    }

    @PostMapping ("/autocomplete")
    GenericListResponse<String> autocompleteSettlements(final SettlementRequest request) {
        return geolocationService.getAutocompleteSettlements(request.settlement());
    }
}
