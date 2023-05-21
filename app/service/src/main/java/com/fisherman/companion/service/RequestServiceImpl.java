package com.fisherman.companion.service;

import org.springframework.stereotype.Service;

import com.fisherman.companion.persistence.RequestsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestsRepository requestsRepository;


}
