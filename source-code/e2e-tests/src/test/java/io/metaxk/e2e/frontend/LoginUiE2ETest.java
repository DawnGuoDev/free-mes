package io.metaxk.e2e.frontend;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.metaxk.e2e.config.E2EConfig;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.time.Duration;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertFalse;

@Epic("Free MES E2E")
@Feature("Frontend Login")
public class LoginUiE2ETest extends PlaywrightManager {

    @Test
    @Story("Admin UI login")
    @Description("Admin can log in from the web UI and enter the main application layout.")
    @Severity(SeverityLevel.BLOCKER)
    public void adminCanLoginFromUi() {
        E2EConfig config = E2EConfig.get();
        Page page = context.newPage();
        page.setDefaultTimeout(Duration.ofSeconds(15).toMillis());
        page.navigate(config.frontBaseUrl + "/login");
        Allure.addAttachment("login-page", "image/png", new ByteArrayInputStream(page.screenshot()), "png");

        Locator username = page.getByTestId("login-username");
        Locator password = page.getByTestId("login-password");
        assertThat(username).isVisible();
        assertThat(password).isVisible();
        username.fill(config.username);
        password.fill(config.password);
        page.getByTestId("login-submit").click();

        page.waitForURL(url -> !url.getPath().contains("/login"), 
                new Page.WaitForURLOptions().setTimeout(Duration.ofSeconds(30).toMillis()));
        assertFalse(page.url().contains("/login"), "Should leave login page after successful authentication");
        Allure.addAttachment("after-login", "image/png", new ByteArrayInputStream(page.screenshot()), "png");
    }

    @Test
    @Story("Login validation")
    @Description("Password field accepts masked input and empty username does not submit successfully.")
    @Severity(SeverityLevel.NORMAL)
    public void emptyUsernameShowsValidationError() {
        E2EConfig config = E2EConfig.get();
        Page page = context.newPage();
        page.navigate(config.frontBaseUrl + "/login");
        page.getByTestId("login-password").fill(config.password);
        page.getByTestId("login-submit").click();
        Locator validation = page.locator(".el-form-item__error");
        assertThat(validation.first()).isVisible();
    }
}
