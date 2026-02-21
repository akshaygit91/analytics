package com.liftlab.analytics.generator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Sends random events to the ingest API at a fixed interval. Env: INGEST_URL (default http://localhost:9000), INTERVAL_MS (default 500). */
public class MockEventGenerator {

    private static final String DEFAULT_URL = "http://localhost:9000";
    private static final List<String> USERS = List.of("usr_101", "usr_102", "usr_789");
    private static final List<String> PAGES = List.of(
        "/home", "/products", "/products/electronics", "/products/laptops",
        "/cart", "/checkout", "/account/orders", "/deals", "/search", "/wishlist"
    );
    private static final List<String> TYPES = List.of("page_view", "page_view", "click");

    public static void main(String[] args) throws InterruptedException {
        String base = System.getenv("INGEST_URL");
        if (base == null || base.isBlank()) base = DEFAULT_URL;
        String url = base.replaceAll("/$", "") + "/events";

        long interval = 200; // default: 200ms (5 events/sec for demo bombardment)
        try {
            String env = System.getenv("INTERVAL_MS");
            if (env != null && !env.isBlank()) interval = Long.parseLong(env);
        } catch (NumberFormatException ignored) {}

        var client = HttpClient.newHttpClient();
        var rnd = ThreadLocalRandom.current();

        while (true) {
            String body = String.format(
                "{\"timestamp\":\"%s\",\"user_id\":\"%s\",\"event_type\":\"%s\",\"page_url\":\"%s\",\"session_id\":\"sess_%d\"}",
                Instant.now(), pick(USERS, rnd), pick(TYPES, rnd), pick(PAGES, rnd), rnd.nextInt(10000));
            try {
                client.send(HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                    java.net.http.HttpResponse.BodyHandlers.discarding());
            } catch (Exception e) {
                System.err.println("Send failed: " + e.getMessage());
            }
            Thread.sleep(interval);
        }
    }

    private static <T> T pick(List<T> list, ThreadLocalRandom rnd) {
        return list.get(rnd.nextInt(list.size()));
    }
}
