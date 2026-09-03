package de.alexandermora.erplite.infrastructure.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {@code address.geo} coordinates of a {@link UserDTO}. The API returns these as strings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoDTO(
        String lat,
        String lng
) {
}
