package de.alexandermora.erplite.infrastructure.rest.customer.adapter;

import de.alexandermora.erplite.domain.entity.customer.CustomerInfo;
import de.alexandermora.erplite.domain.port.service.CustomerProviderServicePort;
import de.alexandermora.erplite.infrastructure.rest.customer.client.JsonPlaceholderClient;
import de.alexandermora.erplite.infrastructure.rest.customer.dto.UserDTO;
import de.alexandermora.erplite.infrastructure.rest.customer.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class JsonPlaceholderCustomerProviderAdapter implements CustomerProviderServicePort {

    private final JsonPlaceholderClient jsonPlaceholderClient;
    private final CustomerMapper customerMapper;

    @Override
    public Optional<CustomerInfo> findById(Long id) {
        try {
            final UserDTO response = jsonPlaceholderClient.getUserById(id);
            if (response == null) {
                log.warn("No customer found with id {}", id);
                return Optional.empty();
            }
            return Optional.of(customerMapper.toCustomerInfo(response));
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("No customer found with id {}", id);
            return Optional.empty();
        } catch (RestClientResponseException ex) {
            log.error("Error fetching customer with id {}: {}", id, ex.getStatusCode(), ex);
            return Optional.empty();
        } catch (RestClientException ex) {
            String format = String.format("Error fetching customer with id %d: %s", id, ex.getMessage());
            log.error(format, ex);
            return Optional.empty();
        } catch (Exception ex) {
            String format = String.format("Unexpected error fetching customer with id %d: %s", id, ex.getMessage());
            log.error(format, ex);
            return Optional.empty();
        }
    }

    @Override
    public boolean existsById(Long id) {
        log.info("existsById called with id: {}", id);
        return findById(id).isPresent();
    }
}
