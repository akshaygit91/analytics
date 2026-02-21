package com.liftlab.analytics.ingest.rest;

import com.liftlab.analytics.aggregation.service.AggregationService;
import com.liftlab.analytics.common.exception.RateLimitExceededException;
import com.liftlab.analytics.common.model.EventRequest;
import com.liftlab.analytics.ingest.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventIngestController {

    private final RateLimiter rateLimiter;
    private final AggregationService aggregationService;

    @PostMapping
    public ResponseEntity<Void> ingest(@Valid @RequestBody EventRequest request) {
        if (!rateLimiter.allow()) {
            log.warn("Ingest rejected: rate limit exceeded");
            throw new RateLimitExceededException();
        }
        aggregationService.process(request);
        return ResponseEntity.accepted().build();
    }
}
