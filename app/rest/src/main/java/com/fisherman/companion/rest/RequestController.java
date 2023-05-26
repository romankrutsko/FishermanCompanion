package com.fisherman.companion.rest;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisherman.companion.dto.request.CreateRequestRequest;
import com.fisherman.companion.dto.request.UpdateRequestRequest;
import com.fisherman.companion.dto.request.UpdateStatusRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.RequestFullDetailsResponse;
import com.fisherman.companion.service.RequestService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    RequestFullDetailsResponse createRequest(HttpServletRequest request, @RequestBody CreateRequestRequest createRequestRequest) {
        return requestService.createRequest(request, createRequestRequest);
    }
    @GetMapping
    GenericListResponse<RequestFullDetailsResponse> getUserNotAcceptedRequests(HttpServletRequest request) {
        return requestService.getUserNotAcceptedRequestsByUserId(request);
    }

    @GetMapping("/{postId}")
    GenericListResponse<RequestFullDetailsResponse> getRequestsByPostId(HttpServletRequest request, @PathVariable(value = "postId") Long postId) {
        return requestService.getRequestsByPostId(request, postId);
    }

    @PostMapping("/{requestId}")
    RequestFullDetailsResponse updateRequest(HttpServletRequest request, @RequestBody UpdateRequestRequest updateRequestRequest, @PathVariable(value = "requestId") Long requestId) {
        return requestService.updateRequestComment(request, updateRequestRequest, requestId);
    }

    @PatchMapping("/{requestId}")
    RequestFullDetailsResponse updateStatus(HttpServletRequest request, @RequestBody final UpdateStatusRequest updateStatusRequest, @PathVariable(value = "requestId") Long requestId) {
        return requestService.updateRequestStatus(request, updateStatusRequest.status(), requestId);
    }

    @DeleteMapping("/{requestId}")
    void deleteUserRating(HttpServletRequest request, @PathVariable(value = "requestId") Long requestId) {
        requestService.deleteRequestById(request, requestId);
    }
}
