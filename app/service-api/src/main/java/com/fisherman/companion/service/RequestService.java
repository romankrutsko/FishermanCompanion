package com.fisherman.companion.service;

import com.fisherman.companion.dto.request.CreateRequestRequest;
import com.fisherman.companion.dto.request.UpdateRequestRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.RequestFullDetailsResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface RequestService {
    RequestFullDetailsResponse createRequest(HttpServletRequest request, CreateRequestRequest createRequestRequest);

    GenericListResponse<RequestFullDetailsResponse> getUserRequestsByUserId(HttpServletRequest request);

    GenericListResponse<RequestFullDetailsResponse> getRequestsByPostId(HttpServletRequest request, Long postId);

    RequestFullDetailsResponse updateRequestComment(HttpServletRequest request, UpdateRequestRequest updateRequestRequest, Long id);

    RequestFullDetailsResponse updateRequestStatus(HttpServletRequest request, String status, Long id);

    void deleteRequestById(HttpServletRequest request, Long id);
}
