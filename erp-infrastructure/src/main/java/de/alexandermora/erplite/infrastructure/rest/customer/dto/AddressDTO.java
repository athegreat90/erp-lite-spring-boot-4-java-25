package de.alexandermora.erplite.infrastructure.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {@code address} block of a {@link UserDTO}, including the nested {@link GeoDTO}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressDTO(
        String street,
        String suite,
        String city,
        String zipcode,
        GeoDTO geo
) {
}
