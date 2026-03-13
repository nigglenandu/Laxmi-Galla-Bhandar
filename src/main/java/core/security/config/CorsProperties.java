package core.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Type-safe, grouped CORS configuration properties.
 * Example application.yml:
 * <pre>
 * app:
 *   security:
 *     cors:
 *       allowed-origin-patterns:
 *         - "http://localhost:*"
 *         - "https://*.yourdomain.com"
 *       allowed-methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
 *       allowed-headers: ["Authorization", "Content-Type", "*"]
 *       exposed-headers: ["Set-Cookie", "Authorization"]
 *       allow-credentials: true
 *       max-age: 3600
 * </pre>
 * # CORS Configuration
 * <p>
 * app.security.cors.allowed-origin-patterns[0]=http://localhost:*
 * app.security.cors.allowed-origin-patterns[1]=https://*.yourdomain.com
 * <p>
 * app.security.cors.allowed-methods[0]=GET
 * app.security.cors.allowed-methods[1]=POST
 * app.security.cors.allowed-methods[2]=PUT
 * app.security.cors.allowed-methods[3]=DELETE
 * app.security.cors.allowed-methods[4]=OPTIONS
 * <p>
 * app.security.cors.allowed-headers[0]=Authorization
 * app.security.cors.allowed-headers[1]=Content-Type
 * app.security.cors.allowed-headers[2]=*
 * <p>
 * app.security.cors.exposed-headers[0]=Set-Cookie
 * app.security.cors.exposed-headers[1]=Authorization
 * <p>
 * app.security.cors.allow-credentials=true
 * app.security.cors.max-age=3600
 */
@Data
@ConfigurationProperties(prefix = "app.security.cors")
public class CorsProperties {
    private List<String> allowedOriginPatterns = List.of("http://localhost:*");
    private List<String> allowedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
    private List<String> allowedHeaders = Arrays.asList("*");
    private List<String> exposedHeaders = Arrays.asList("Set-Cookie", "Authorization");
    private boolean allowCredentials = true;
    private long maxAge = 3600; // seconds
}