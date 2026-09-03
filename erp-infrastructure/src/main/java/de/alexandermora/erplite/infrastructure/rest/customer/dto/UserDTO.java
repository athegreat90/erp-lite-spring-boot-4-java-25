package de.alexandermora.erplite.infrastructure.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * JSONPlaceholder {@code /users/{id}} response
 * (see <a href="https://jsonplaceholder.typicode.com/users/1">example</a>).
 * Nested objects map to {@link AddressDTO} and {@link CompanyDTO}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDTO(
        Long id,
        String name,
        String username,
        String email,
        AddressDTO address,
        String phone,
        String website,
        CompanyDTO company
) {
}
