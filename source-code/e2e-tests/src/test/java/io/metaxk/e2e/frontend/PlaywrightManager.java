package io.metaxk.e2e.frontend;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import io.metaxk.e2e.config.E2EConfig;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Shares one browser instance for the suite and creates isolated contexts per test.
 */
public abstract class PlaywrightManager {
    private static Playwright playwright;
    private static Browser browser;
    protected BrowserContext context;

    @BeforeSuite(alwaysRun = true)
    public void startBrowser() {
        if (playwright == null) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(E2EConfig.get().headless));
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void createContext(Method method) {
        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1440, 900));
        if (E2EConfig.get().traceEnabled) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        }
    }

    @AfterMethod(alwaysRun = true)
    public void closeContext() {
        Optional.ofNullable(context).ifPresent(item -> {
            if (E2EConfig.get().traceEnabled) {
                try {
                    Files.createDirectories(Paths.get("target/playwright-traces"));
                } catch (Exception ignored) {
                    // tracing should not make the test fail on report directory creation
                }
                item.tracing().stop(new Tracing.StopOptions()
                        .setPath(Paths.get("target/playwright-traces", System.currentTimeMillis() + "-trace.zip")));
            }
            item.close();
        });
        context = null;
    }

    @AfterSuite(alwaysRun = true)
    public void stopBrowser() {
        Optional.ofNullable(browser).ifPresent(Browser::close);
        Optional.ofNullable(playwright).ifPresent(Playwright::close);
        browser = null;
        playwright = null;
    }
}
