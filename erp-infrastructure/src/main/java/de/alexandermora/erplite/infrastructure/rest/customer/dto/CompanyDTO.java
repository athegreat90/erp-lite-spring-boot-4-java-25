package de.alexandermora.erplite.infrastructure.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@code company} block of a {@link UserDTO}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyDTO(
        String name,
        @JsonProperty("catchPhrase")
        String cp,
        String bs
) {
}
