package de.alexandermora.erplite.domain.port;

import de.alexandermora.erplite.domain.customer.CustomerInfo;

import java.util.Optional;

/*
* Port for external service for JSONPlaceholder
* */
public interface CustomerProviderService {
    Optional<CustomerInfo> findById(Long id);
    boolean existsById(Long id);
}
