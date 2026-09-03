package de.alexandermora.erplite.infrastructure.rest.customer.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Binds {@code jsonplaceholder.api.*} from
 * {@code classpath:jsonplaceholder/jsonplaceholder.yml}.
 */
@Validated
@ConfigurationProperties(prefix = "jsonplaceholder.api")
public record JsonPlaceHolderConfigModel(
        @NotBlank(message = "JSONPlaceholder base URL must not be blank")
        @Pattern(regexp = "^https?://.+", message = "JSONPlaceholder base URL must be an http(s) URL")
        String baseUrl,
        @NotBlank(message = "JSONPlaceholder users endpoint must not be blank")
        @Pattern(regexp = "^/.+", message = "JSONPlaceholder users endpoint must start with '/'")
        String usersEndpoints,
        @Positive(message = "JSONPlaceholder connection timeout (ms) must be positive")
        int connectionTimeout,
        @Positive(message = "JSONPlaceholder read timeout (ms) must be positive")
        int readTimeout,
        boolean enabled
) {
    /** Absolute URL of the users collection, e.g. {@code https://jsonplaceholder.typicode.com/users}. */
    public String usersUrl() {
        return baseUrl + usersEndpoints;
    }
}