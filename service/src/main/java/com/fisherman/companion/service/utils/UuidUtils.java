package com.fisherman.companion.service.utils;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UuidUtils {
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
