package com.fisherman.companion.service;

import com.fisherman.companion.dto.RequestDto;
import com.fisherman.companion.dto.request.CreateRequestRequest;
import com.fisherman.companion.dto.request.UpdateRequestRequest;
import com.fisherman.companion.dto.response.GenericListResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface RequestService {
    RequestDto createRequest(HttpServletRequest request, CreateRequestRequest createRequestRequest);

    GenericListResponse<RequestDto> getUserRequestsByUserId(HttpServletRequest request);

    GenericListResponse<RequestDto> getRequestsByPostId(HttpServletRequest request, Long postId);

    RequestDto updateRequestComment(HttpServletRequest request, UpdateRequestRequest updateRequestRequest, Long id);

    RequestDto updateRequestStatus(HttpServletRequest request, String status, Long id);

    void deleteRequestById(HttpServletRequest request, Long id);
}
