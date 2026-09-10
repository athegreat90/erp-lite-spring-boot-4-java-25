package de.alexandermora.erplite.domain.port.service;

import de.alexandermora.erplite.domain.entity.customer.CustomerInfo;

import java.util.Optional;

/*
* Port for external service for JSONPlaceholder
* */
public interface CustomerProviderServicePort {
    Optional<CustomerInfo> findById(Long id);
    boolean existsById(Long id);
}
