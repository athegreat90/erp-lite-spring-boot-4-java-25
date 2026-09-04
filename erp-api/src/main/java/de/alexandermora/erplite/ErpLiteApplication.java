package de.alexandermora.erplite;

import de.alexandermora.erplite.domain.order.OrderId;
import de.alexandermora.erplite.domain.port.OrderConfirmEmailService;
import de.alexandermora.erplite.domain.shared.Email;
import de.alexandermora.erplite.domain.shared.Money;
import de.alexandermora.erplite.infrastructure.rest.customer.adapter.JsonPlaceholderCustomerProviderAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Currency;

@Slf4j
@SpringBootApplication
public class ErpLiteApplication implements CommandLineRunner {


    @Autowired
    private OrderConfirmEmailService orderConfirmEmailService;


    public static void main(String[] args) {
        SpringApplication.run(ErpLiteApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        orderConfirmEmailService.sendMail(
                Email.of("alexander.mora@proton.me"),
                OrderId.of(OrderId.generate().value()),
                "ORD-12345",
                Money.of(100D, Currency.getInstance("USD")),
                "Alexander Mora",
                1
        );
        log.info("Order confirmation email sent.");
    }
}
