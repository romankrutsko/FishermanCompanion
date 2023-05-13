package com.fisherman.companion.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisherman.companion.dto.request.GeoAutocompleteRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.service.GeolocationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/geo")
public class GeolocationController {
    private final GeolocationService geolocationService;

    @PostMapping("/autocomplete")
    GenericListResponse<String> autocompleteSettlements(@RequestBody GeoAutocompleteRequest geoAutocompleteRequest) {
        return geolocationService.getAutocompleteSettlements(geoAutocompleteRequest.settlement());
    }
}
