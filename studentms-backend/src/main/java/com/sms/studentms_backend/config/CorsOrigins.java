package com.sms.studentms_backend.config;

import java.util.List;

public final class CorsOrigins {

    private CorsOrigins() {
    }

    public static List<String> allowedOrigins() {
        String configuredHost = System.getenv().getOrDefault("FRONTEND_HOST", "localhost:5173");
        String frontendOrigin = configuredHost.startsWith("http://") || configuredHost.startsWith("https://")
                ? configuredHost
                : configuredHost.equals("localhost:5173")
                        ? "http://" + configuredHost
                        : "https://" + configuredHost;
        return List.of("http://localhost:5173", frontendOrigin);
    }
}
