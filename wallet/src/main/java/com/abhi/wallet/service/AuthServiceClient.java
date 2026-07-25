package com.abhi.wallet.service;

import com.abhi.wallet.common.ApiResponse;
import com.abhi.wallet.dto.response.UserDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {

    private final WebClient authServiceWebClient;

    // ─── Synchronous (blocking) call ──────────────────
    // Use when you need result before continuing
    public UserDetailsResponse getUserDetails(Long userId) {
        try {
            return authServiceWebClient
                    .get()
                    .uri("api/user/{userId}", userId)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::is4xxClientError,
                            response -> Mono.error(
                                    new ResourceNotFoundException(
                                            "User not found: " + userId))
                    )
                    .onStatus(
                            HttpStatusCode::is5xxServerError,
                            response -> Mono.error(
                                    new RuntimeException(
                                            "Auth service error for userId: " + userId))
                    )
                    .bodyToMono(new ParameterizedTypeReference
                            <ApiResponse<UserDetailsResponse>>() {})
                    .map(ApiResponse::getData)
                    .timeout(Duration.ofSeconds(3))
                    .block();  // blocks until response received

        } catch (ResourceNotFoundException e) {
            throw e;  // rethrow business exceptions
        } catch (Exception e) {
            log.error("Failed to fetch user details for userId: {}",
                    userId, e);
            throw new RuntimeException(
                    "Auth service unavailable");
        }
    }

    // ─── Asynchronous (non-blocking) call ─────────────
    // Use when you can do other work while waiting
    public Mono<UserDetailsResponse> getUserDetailAsync(Long userId) {
        return authServiceWebClient
                .get()
                .uri("api/user/{userId}", userId)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> Mono.error(
                                new ResourceNotFoundException(
                                        "User not found: " + userId))
                )
                .bodyToMono(new ParameterizedTypeReference
                        <ApiResponse<UserDetailsResponse>>() {})
                .map(ApiResponse::getData)
                .timeout(Duration.ofSeconds(3));
    }

    // ─── Parallel calls (fetch two users simultaneously) ──
    // Most efficient for transfer — fetch sender AND receiver at same time
    public List<UserDetailsResponse> getUserDetailsBatch(
            Long senderUserId, Long receiverUserId) {

        Mono<UserDetailsResponse> senderMono =
                getUserDetailAsync(senderUserId);
        Mono<UserDetailsResponse> receiverMono =
                getUserDetailAsync(receiverUserId);

        // Zip — fires BOTH requests simultaneously, waits for both
        return Mono.zip(senderMono, receiverMono)
                .map(tuple -> List.of(tuple.getT1(), tuple.getT2()))
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}