package com.fisherman.companion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.RequestDto;
import com.fisherman.companion.dto.RequestStatus;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.request.CreateRequestRequest;
import com.fisherman.companion.dto.request.UpdateRequestRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.RequestResponse;
import com.fisherman.companion.persistence.RequestsRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestsRepository requestsRepository;
    private final TokenService tokenService;

    @Override
    public RequestResponse createRequest(final HttpServletRequest request, final CreateRequestRequest createRequestRequest) {
        tokenService.verifyAuthentication(request);

        final RequestDto requestDto = mapToRequestDto(createRequestRequest);

        final Long requestId = requestsRepository.createRequest(requestDto);

        requestDto.setId(requestId);

        return mapToResponse(requestDto);
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

    public RequestResponse mapToResponse(RequestDto requestDto) {
        final String translatedStatus = translateStatus(requestDto.getStatus());

        return RequestResponse.builder()
                              .id(requestDto.getId())
                              .userId(requestDto.getUserId())
                              .postId(requestDto.getPostId())
                              .comment(requestDto.getComment())
                              .status(translatedStatus)
                              .build();
    }

    private static String translateStatus(RequestStatus status) {
        return switch (status) {
            case PENDING -> "ОЧІКУВАННЯ";
            case ACCEPTED -> "ПРИЙНЯТИЙ";
            case DECLINED -> "ВІДХИЛЕНИЙ";
        };
    }

    @Override
    public GenericListResponse<RequestResponse> getUserRequestsByUserId(final HttpServletRequest request) {
        final UserDto userDto = tokenService.verifyAuthentication(request);

        final List<RequestDto> listOfCurrentUserRequests = requestsRepository.getRequestsByUserId(userDto.id());

        final List<RequestResponse> requestResponses = listOfCurrentUserRequests.stream().map(this::mapToResponse).toList();

        return GenericListResponse.of(requestResponses);
    }

    @Override
    public GenericListResponse<RequestResponse> getRequestsByPostId(final HttpServletRequest request, final Long postId) {
        tokenService.verifyAuthentication(request);

        final List<RequestDto> listOfRequestsToPost = requestsRepository.getRequestsByPostId(postId);

        final List<RequestResponse> requestResponses = listOfRequestsToPost.stream().map(this::mapToResponse).toList();

        return GenericListResponse.of(requestResponses);
    }

    @Override
    public RequestResponse updateRequestComment(final HttpServletRequest request, final UpdateRequestRequest updateRequestRequest, final Long id) {
        tokenService.verifyAuthentication(request);

        final RequestDto requestDto = RequestDto
                .builder().id(id)
                .comment(updateRequestRequest.comment())
                .build();

        requestsRepository.updateRequest(requestDto);

        final RequestDto updated = requestsRepository.getRequestById(id);

        return mapToResponse(updated);
    }

    @Override
    public RequestResponse updateRequestStatus(final HttpServletRequest request, final String status, final Long id) {
        tokenService.verifyAuthentication(request);

        final RequestStatus requestStatus = mapToStatus(status.toUpperCase());

        final RequestDto requestDto = RequestDto
                .builder().id(id)
                .status(requestStatus)
                .build();

        requestsRepository.updateRequestStatus(requestDto);

        final RequestDto updated = requestsRepository.getRequestById(id);

        return mapToResponse(updated);
    }

    public RequestStatus mapToStatus(String statusInUkrainian) {
        return switch (statusInUkrainian) {
            case "ОЧІКУВАННЯ" -> RequestStatus.PENDING;
            case "ПРИЙНЯТИЙ" -> RequestStatus.ACCEPTED;
            case "ВІДХИЛЕНИЙ" -> RequestStatus.DECLINED;
            default -> throw new IllegalArgumentException("Invalid Ukrainian status: " + statusInUkrainian);
        };
    }

    @Override
    public void deleteRequestById(final HttpServletRequest request,final Long id) {
        tokenService.verifyAuthentication(request);

        requestsRepository.deleteRequest(id);
    }
}
