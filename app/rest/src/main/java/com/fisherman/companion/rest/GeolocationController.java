package com.fisherman.companion.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fisherman.companion.dto.Geolocation;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.service.GeolocationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/location")
public class GeolocationController {
    private final GeolocationService geolocationService;

    @GetMapping
    Geolocation getSettlementCoordinates(@RequestParam(value = "settlement") final String settlement) {
        return geolocationService.getCoordinates(settlement);
    }

    @GetMapping("/autocomplete")
    GenericListResponse<String> autocompleteSettlements(@RequestParam(value = "settlement") final String settlement) {
        return geolocationService.getAutocompleteSettlements(settlement);
    }
}
