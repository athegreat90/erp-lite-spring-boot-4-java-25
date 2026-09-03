package de.alexandermora.erplite;

import de.alexandermora.erplite.infrastructure.rest.customer.adapter.JsonPlaceholderCustomerProviderAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ErpLiteApplication implements CommandLineRunner {


    @Autowired
    private JsonPlaceholderCustomerProviderAdapter jsonPlaceholderCustomerProviderAdapter;


    public static void main(String[] args) {
        SpringApplication.run(ErpLiteApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        var r = jsonPlaceholderCustomerProviderAdapter.findById(1L);
        log.info("Found customer: {}", r.get());
    }
}
