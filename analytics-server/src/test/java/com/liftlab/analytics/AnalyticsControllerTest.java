package com.liftlab.analytics;

import com.liftlab.analytics.aggregation.service.AggregationService;
import com.liftlab.analytics.common.exception.GlobalExceptionHandler;
import com.liftlab.analytics.config.AnalyticsProperties;
import com.liftlab.analytics.ingest.rest.EventIngestController;
import com.liftlab.analytics.ingest.RateLimiter;
import com.liftlab.analytics.metrics.rest.MetricsController;
import com.liftlab.analytics.metrics.service.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { EventIngestController.class, MetricsController.class })
@Import(GlobalExceptionHandler.class)
class AnalyticsControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RateLimiter rateLimiter;

    @MockBean
    AggregationService aggregationService;

    @MockBean
    MetricsService metricsService;

    @MockBean
    AnalyticsProperties analyticsProperties;

    private static final String VALID_EVENT = """
        {"timestamp":"2024-03-15T14:30:00Z","user_id":"usr_789","event_type":"page_view","page_url":"/products","session_id":"sess_456"}
        """;

    @BeforeEach
    void setUp() {
        when(analyticsProperties.getActiveWindowMinutes()).thenReturn(5);
        when(analyticsProperties.getPageViewWindowMinutes()).thenReturn(15);
    }

    @Test
    void ingest_validEvent_returns202() throws Exception {
        when(rateLimiter.allow()).thenReturn(true);
        mvc.perform(post("/events").contentType(MediaType.APPLICATION_JSON).content(VALID_EVENT))
            .andExpect(status().isAccepted());
        verify(aggregationService).process(any());
    }

    @Test
    void ingest_rateLimitExceeded_returns429() throws Exception {
        when(rateLimiter.allow()).thenReturn(false);
        mvc.perform(post("/events").contentType(MediaType.APPLICATION_JSON).content(VALID_EVENT))
            .andExpect(status().isTooManyRequests());
        verify(aggregationService, never()).process(any());
    }

    @Test
    void ingest_invalidPayload_returns400() throws Exception {
        when(rateLimiter.allow()).thenReturn(true);
        mvc.perform(post("/events").contentType(MediaType.APPLICATION_JSON)
                .content("{\"event_type\":\"page_view\",\"page_url\":\"/\",\"session_id\":\"s1\"}"))
            .andExpect(status().isBadRequest());
        verify(aggregationService, never()).process(any());
    }

    @Test
    void ingest_malformedTimestamp_returns400() throws Exception {
        when(rateLimiter.allow()).thenReturn(true);
        mvc.perform(post("/events").contentType(MediaType.APPLICATION_JSON)
                .content("{\"timestamp\":\"not-a-time\",\"user_id\":\"usr_1\",\"event_type\":\"page_view\",\"page_url\":\"/\",\"session_id\":\"s1\"}"))
            .andExpect(status().isBadRequest());
        verify(aggregationService, never()).process(any());
    }

    @Test
    void metrics_activeUsers_returnsCount() throws Exception {
        when(metricsService.getActiveUsersCount()).thenReturn(10L);
        mvc.perform(get("/metrics/active-users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.activeUsersCount").value(10));
    }

    @Test
    void metrics_topPages_returnsList() throws Exception {
        when(metricsService.getTopPages(5)).thenReturn(List.of(
            new MetricsService.PageViewCount("/products", 50),
            new MetricsService.PageViewCount("/", 30)));
        mvc.perform(get("/metrics/top-pages"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.topPages.length()").value(2))
            .andExpect(jsonPath("$.topPages[0].url").value("/products"))
            .andExpect(jsonPath("$.topPages[0].count").value(50));
    }

    @Test
    void metrics_activeSessions_returnsCount() throws Exception {
        when(metricsService.getActiveSessionsForUser("usr_789")).thenReturn(2L);
        mvc.perform(get("/metrics/active-sessions").param("userId", "usr_789"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("usr_789"))
            .andExpect(jsonPath("$.activeSessionsCount").value(2));
    }
}
