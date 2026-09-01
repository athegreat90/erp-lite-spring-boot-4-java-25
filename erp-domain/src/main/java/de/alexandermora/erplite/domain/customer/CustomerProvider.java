package de.alexandermora.erplite.domain.customer;

import de.alexandermora.erplite.domain.customer.CustomerInfo;

import java.util.Optional;

/*
* Port for external service for JSONPlaceholder
* */
public interface CustomerProvider {
    Optional<CustomerInfo> findById(Long id);
    boolean existsById(Long id);
}
