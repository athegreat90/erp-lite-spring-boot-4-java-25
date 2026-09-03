package de.alexandermora.erplite.infrastructure.rest.customer.config;

import de.alexandermora.erplite.infrastructure.rest.customer.model.JsonPlaceHolderConfigModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RestClientConfig {
    private final JsonPlaceHolderConfigModel jsonConfig;

    @Bean(name = "jsonplaceholder")
    @ConditionalOnProperty(prefix = "jsonplaceholder.api", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RestClient restClient() {

        return RestClient.builder()
                .baseUrl(jsonConfig.baseUrl())
                .defaultHeaders(header -> {
                    header.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    header.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                })
                .requestInterceptors(interceptors -> {
                    interceptors.add(loggingInterceptor());
                    interceptors.add(errorLoggingInterceptor());
                })
                .build();
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.info("Calling JSONPlaceholder API");
            log.info("Request: {} {} with body: {}", request.getMethod(), request.getURI(), new String(body));
            log.info("Headers: {}", request.getHeaders());
            final long startTime = System.currentTimeMillis();
            var response = execution.execute(request, body);
            final long endTime = System.currentTimeMillis() - startTime;
            log.info("Response: {} with status code: {} (took {} ms)", response.getStatusCode(), response.getStatusText(), endTime);
            return response;
        };
    }

    private ClientHttpRequestInterceptor errorLoggingInterceptor() {
        return (request, body, execution) -> {
            try {
                var response = execution.execute(request, body);
                if (!response.getStatusCode().is2xxSuccessful()) {
                    log.error("Request to {} failed with status code {} and reason: {}", request.getURI(), response.getStatusCode(), response.getStatusText());
                }
                return response;
            } catch (IOException e) {
                var msg = "Error message%s".formatted(e.getMessage());
                log.error(msg, e);
                throw e;
            }
        };
    }
}
