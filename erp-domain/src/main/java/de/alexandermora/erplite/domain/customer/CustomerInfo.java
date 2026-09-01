package de.alexandermora.erplite.domain.customer;


/*
* Value object immutable for JSONPlaceholder API
* */
public record CustomerInfo(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        String city,
        String zipCode,
        String companyName
) {
    public CustomerInfo {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Customer ID must be a positive number");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name must not be blank");
        }
    }
}
