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

import static org.testng.Assert.assertNotNull;

@Epic("Free MES E2E")
@Feature("MES Master Data")
public class MesSpecApiE2ETest {
    private final ApiClient api = new ApiClient();

    @BeforeClass
    public void login() throws Exception {
        api.login();
    }

    @Test
    @Story("Specification list")
    @Description("Authorized request to /mes/md/spec/list returns a page result.")
    @Severity(SeverityLevel.CRITICAL)
    public void canQuerySpecList() throws Exception {
        JsonNode data = api.get("/mes/md/spec/list?pageNo=1&pageSize=10").path("data");
        assertNotNull(data, "Page result should not be null");
    }
}
