package io.metaxk.e2e.backend;

import com.fasterxml.jackson.databind.JsonNode;
import io.metaxk.e2e.http.ApiClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

@Epic("Free MES E2E")
@Feature("Backend Authentication")
public class AuthApiE2ETest {
    private final ApiClient api = new ApiClient();
    private String accessToken;

    @BeforeClass
    public void login() throws Exception {
        accessToken = api.login();
    }

    @Test(priority = 1)
    @Story("Admin login")
    @Description("Admin can obtain a non-empty access token through /system/auth/login.")
    @Severity(SeverityLevel.BLOCKER)
    public void adminCanLogin() {
        assertNotNull(accessToken, "Access token should be returned");
        assertFalse(accessToken.isBlank(), "Access token should not be empty");
        assertNotEquals(accessToken, "null", "Access token should not be literal null");
    }

    @Test(priority = 2)
    @Story("Current permission info")
    @Description("Authorized request to /system/auth/get-permission-info returns current user.")
    @Severity(SeverityLevel.CRITICAL)
    public void canGetPermissionInfo() throws Exception {
        JsonNode data = api.get("/system/auth/get-permission-info").path("data");
        assertNotNull(data, "Permission info data should not be null");
        assertNotNull(data.path("user").path("id").asText(null), "Current user id should be returned");
    }
}
