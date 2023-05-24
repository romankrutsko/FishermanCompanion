package com.fisherman.companion.persistence;

import java.util.List;

import com.fisherman.companion.dto.RequestDto;

public interface RequestsRepository {
    Long createRequest(RequestDto request);

    RequestDto getRequestById(Long requestId);

    List<RequestDto> getRequestsByUserId(Long userId);

    List<RequestDto> getRequestsByPostId(Long postId);

    List<Long> getUserIdsOfAcceptedRequestsByPostId(Long postId, Long userId);

    void updateRequest(RequestDto request);

    void updateRequestStatus(RequestDto request);

    void deleteRequest(Long requestId);
}
