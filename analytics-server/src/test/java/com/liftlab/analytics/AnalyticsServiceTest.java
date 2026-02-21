package com.liftlab.analytics;

import com.liftlab.analytics.aggregation.service.AggregationService;
import com.liftlab.analytics.aggregation.AnalyticsStore;
import com.liftlab.analytics.common.model.EventRequest;
import com.liftlab.analytics.config.AnalyticsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    AnalyticsStore store;

    AggregationService service;

    @BeforeEach
    void setUp() {
        AnalyticsProperties config = new AnalyticsProperties();
        config.setActiveWindowMinutes(5);
        config.setPageViewWindowMinutes(15);
        service = new AggregationService(store, config);
    }

    @Test
    void process_pageViewEvent_updatesStore() {
        EventRequest event = new EventRequest();
        event.setTimestamp("2024-03-15T14:30:00Z");
        event.setUserId("usr_1");
        event.setSessionId("sess_1");
        event.setEventType("page_view");
        event.setPageUrl("/products");

        service.process(event);

        verify(store).addActiveUser(eq("usr_1"), anyDouble());
        verify(store).removeActiveUsersWithScoreBefore(anyDouble());
        verify(store).addUserSession(eq("usr_1"), eq("sess_1"), anyDouble());
        verify(store).removeUserSessionsWithScoreBefore(eq("usr_1"), anyDouble());
        verify(store).incrementPageView(contains("20240315"), eq("/products"));
    }
}
