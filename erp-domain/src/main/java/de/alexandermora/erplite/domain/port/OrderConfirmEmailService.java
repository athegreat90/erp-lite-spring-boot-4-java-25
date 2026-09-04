package de.alexandermora.erplite.domain.port;

import de.alexandermora.erplite.domain.order.OrderId;
import de.alexandermora.erplite.domain.shared.Email;
import de.alexandermora.erplite.domain.shared.Money;


/*
 * Port for email service in order created
 * */
public interface OrderConfirmEmailService {
    void sendMail(Email email, OrderId orderId, String orderNumber, Money money, String customerName, Integer itemsCount);
}
