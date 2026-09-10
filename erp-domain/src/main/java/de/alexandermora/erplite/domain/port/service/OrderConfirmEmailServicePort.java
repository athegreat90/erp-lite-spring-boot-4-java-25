package de.alexandermora.erplite.domain.port.service;

import de.alexandermora.erplite.domain.entity.order.OrderId;
import de.alexandermora.erplite.domain.shared.Email;
import de.alexandermora.erplite.domain.shared.Money;


/*
 * Port for email service in order created
 * */
public interface OrderConfirmEmailServicePort {
    void sendMail(Email email, OrderId orderId, String orderNumber, Money money, String customerName, Integer itemsCount);
}
