package com.fisherman.companion.service;

import com.fisherman.companion.dto.request.CreateRequestRequest;
import com.fisherman.companion.dto.request.UpdateRequestRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.RequestResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface RequestService {
    RequestResponse createRequest(HttpServletRequest request, CreateRequestRequest createRequestRequest);

    GenericListResponse<RequestResponse> getUserRequestsByUserId(HttpServletRequest request);

    GenericListResponse<RequestResponse> getRequestsByPostId(HttpServletRequest request, Long postId);

    RequestResponse updateRequestComment(HttpServletRequest request, UpdateRequestRequest updateRequestRequest, Long id);

    RequestResponse updateRequestStatus(HttpServletRequest request, String status, Long id);

    void deleteRequestById(HttpServletRequest request, Long id);
}
