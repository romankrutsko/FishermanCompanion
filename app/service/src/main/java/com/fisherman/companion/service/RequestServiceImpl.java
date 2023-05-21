package com.fisherman.companion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.RequestDto;
import com.fisherman.companion.dto.RequestStatus;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.request.CreateRequestRequest;
import com.fisherman.companion.dto.request.UpdateRequestRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.persistence.RequestsRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestsRepository requestsRepository;
    private final TokenService tokenService;

    @Override
    public RequestDto createRequest(final HttpServletRequest request, final CreateRequestRequest createRequestRequest) {
        tokenService.verifyAuthentication(request);

        final RequestDto requestDto = mapToRequestDto(createRequestRequest);

        final Long requestId = requestsRepository.createRequest(requestDto);

        requestDto.setId(requestId);

        return requestDto;
    }

    private RequestDto mapToRequestDto(final CreateRequestRequest request) {
        return RequestDto
                .builder()
                .userId(request.userId())
                .postId(request.postId())
                .comment(request.comment())
                .status(RequestStatus.PENDING)
                .build();
    }

    @Override
    public GenericListResponse<RequestDto> getUserRequestsByUserId(final HttpServletRequest request) {
        final UserDto userDto = tokenService.verifyAuthentication(request);

        final List<RequestDto> listOfCurrentUserRequests = requestsRepository.getRequestsByUserId(userDto.id());

        return GenericListResponse.of(listOfCurrentUserRequests);
    }

    @Override
    public GenericListResponse<RequestDto> getRequestsByPostId(final HttpServletRequest request, final Long postId) {
        tokenService.verifyAuthentication(request);

        final List<RequestDto> listOfRequestsToPost = requestsRepository.getRequestsByPostId(postId);

        return GenericListResponse.of(listOfRequestsToPost);
    }

    @Override
    public RequestDto updateRequestComment(final HttpServletRequest request, final UpdateRequestRequest updateRequestRequest, final Long id) {
        tokenService.verifyAuthentication(request);

        final RequestDto requestDto = RequestDto
                .builder().id(id)
                .comment(updateRequestRequest.comment())
                .build();

        requestsRepository.updateRequest(requestDto);

        return requestsRepository.getRequestById(id);
    }

    @Override
    public RequestDto updateRequestStatus(final HttpServletRequest request, final String status, final Long id) {
        tokenService.verifyAuthentication(request);

        final RequestDto requestDto = RequestDto
                .builder().id(id)
                .status(RequestStatus.valueOf(status.toUpperCase()))
                .build();

        requestsRepository.updateRequestStatus(requestDto);

        return requestsRepository.getRequestById(id);
    }

    @Override
    public void deleteRequestById(final HttpServletRequest request,final Long id) {
        tokenService.verifyAuthentication(request);

        requestsRepository.deleteRequest(id);
    }
}
