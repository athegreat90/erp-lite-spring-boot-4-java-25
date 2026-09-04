package de.alexandermora.erplite.infrastructure.rest.customer.adapter;

import de.alexandermora.erplite.domain.customer.CustomerInfo;
import de.alexandermora.erplite.domain.port.CustomerProviderService;
import de.alexandermora.erplite.infrastructure.rest.customer.dto.UserDTO;
import de.alexandermora.erplite.infrastructure.rest.customer.mapper.CustomerMapper;
import de.alexandermora.erplite.infrastructure.rest.customer.model.JsonPlaceHolderConfigModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;


@Service
@Slf4j
public class JsonPlaceholderCustomerProviderAdapter implements CustomerProviderService {

    private final RestClient jsonClient;
    private final CustomerMapper customerMapper;
    private final String endpoint;

    public JsonPlaceholderCustomerProviderAdapter(@Qualifier("jsonplaceholder") RestClient restClient, CustomerMapper customerMapper, JsonPlaceHolderConfigModel configModel) {
        this.jsonClient = restClient;
        this.customerMapper = customerMapper;
        this.endpoint = configModel.usersEndpoints();
    }

    @Override
    public Optional<CustomerInfo> findById(Long id) {
        try {
            final UserDTO response = jsonClient.get().uri(endpoint, id).retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.error("Client error while fetching customer with id {}: {}", id, res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("Server error while fetching customer with id {}: {}", id, res.getStatusCode());
                    }).toEntity(UserDTO.class).getBody();
            if (response == null) {
                log.warn("No customer found with id {}", id);
                return Optional.empty();
            }
            return Optional.of(customerMapper.toCustomerInfo(response));
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
