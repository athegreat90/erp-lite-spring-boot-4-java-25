package de.alexandermora.erplite.infrastructure.rest.customer.client;

import de.alexandermora.erplite.infrastructure.rest.customer.dto.UserDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * Declarative HTTP client for the JSONPlaceholder {@code /users} API,
 * proxied by {@link org.springframework.web.service.invoker.HttpServiceProxyFactory}.
 */
@HttpExchange
public interface JsonPlaceholderClient {

    @GetExchange("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}
