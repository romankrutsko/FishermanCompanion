package com.fisherman.companion.service;

import com.fisherman.companion.dto.Geolocation;
import com.fisherman.companion.dto.response.GenericListResponse;

public interface GeolocationService {
    GenericListResponse<String> getAutocompleteSettlements(String request);

    Geolocation getCoordinates(String settlementName);
}
