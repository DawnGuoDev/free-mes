package io.metaxk.e2e.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metaxk.e2e.config.E2EConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Minimal typed API client used by backend E2E tests.
 */
public final class ApiClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final E2EConfig config = E2EConfig.get();
    private String accessToken;

    public String login() throws IOException, InterruptedException {
        Map<String, Object> body = new HashMap<>();
        body.put("username", config.username);
        body.put("password", config.password);

        JsonNode response = post("/system/auth/login", body, false);
        assertEquals(response.path("code").asInt(), 0, "Login API should return code=0");
        accessToken = response.path("data").path("accessToken").asText();
        if (accessToken == null || accessToken.isBlank()) {
            accessToken = response.path("data").path("token").asText();
        }
        return accessToken;
    }

    public JsonNode get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(config.apiBaseUrl + path))
                .timeout(Duration.ofSeconds(20))
                .header("tenant-id", config.tenantId)
                .header("Authorization", "Bearer " + requireToken())
                .GET()
                .build();
        return execute(request);
    }

    public JsonNode post(String path, Object payload, boolean authorized) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(config.apiBaseUrl + path))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .header("tenant-id", config.tenantId)
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(payload)));
        if (authorized) {
            builder.header("Authorization", "Bearer " + requireToken());
        }
        return execute(builder.build());
    }

    private JsonNode execute(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200, "HTTP status should be 200 for " + request.uri());
        JsonNode json = MAPPER.readTree(response.body());
        assertEquals(json.path("code").asInt(), 0, "Business response should return code=0: " + response.body());
        return json;
    }

    private String requireToken() {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("Access token is missing. Call login() first.");
        }
        return accessToken;
    }
}
