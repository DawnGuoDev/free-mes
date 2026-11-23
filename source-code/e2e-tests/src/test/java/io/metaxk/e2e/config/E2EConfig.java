package io.metaxk.e2e.config;

/**
 * Centralized E2E runtime configuration. Every value can be overridden by
 * environment variables or system properties.
 */
public final class E2EConfig {
    private static final E2EConfig INSTANCE = new E2EConfig();

    public final String apiBaseUrl;
    public final String frontBaseUrl;
    public final String username;
    public final String password;
    public final String tenantName;
    public final String tenantId;
    public final boolean headless;
    public final boolean traceEnabled;

    private E2EConfig() {
        this.apiBaseUrl = string("E2E_API_BASE_URL", "http://127.0.0.1:48080/admin-api");
        this.frontBaseUrl = string("E2E_FRONT_BASE_URL", "http://127.0.0.1:3080");
        this.username = string("E2E_USERNAME", "admin");
        this.password = string("E2E_PASSWORD", "admin123");
        this.tenantName = string("E2E_TENANT_NAME", "万界星空MES");
        this.tenantId = string("E2E_TENANT_ID", "1");
        this.headless = bool("E2E_HEADLESS", true);
        this.traceEnabled = bool("E2E_TRACE_ENABLED", false);
    }

    public static E2EConfig get() {
        return INSTANCE;
    }

    private static String string(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = System.getProperty(key);
        }
        return value == null || value.isBlank() ? fallback : value;
    }

    private static boolean bool(String key, boolean fallback) {
        String value = string(key, String.valueOf(fallback));
        return Boolean.parseBoolean(value);
    }

}
