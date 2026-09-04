package de.alexandermora.erplite.infrastructure.mail.adapter;

import de.alexandermora.erplite.domain.order.OrderId;
import de.alexandermora.erplite.domain.port.OrderConfirmEmailService;
import de.alexandermora.erplite.domain.shared.Email;
import de.alexandermora.erplite.domain.shared.Money;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResendAdapter implements OrderConfirmEmailService {

    private final JavaMailSender mailSender;

    @Value("${resend.from-address}")
    private String fromAddress;

    @Value("${email.company:Hellsing}")
    private String companyName;

    @Value("classpath:templates/email-order-confirm-template.html")
    private Resource emailTemplate;

    @Override
    public void sendMail(Email email, OrderId orderId, String orderNumber, Money money, String customerName, Integer itemsCount) {
        // Implement the logic to send an email using the Resend service
        // You can use the mailSender to send the email
        // For example, you can create a SimpleMailMessage or MimeMessage and set the necessary properties

        log.info("Sending order confirmation email to: {}", email.value());
        log.info("Order ID: {}, Order Number: {}, Total Amount: {}, Customer Name: {}, Items Count: {}",
                orderId.value(), orderNumber, money.amount(), customerName, itemsCount);

        // Here you would implement the actual email sending logic using the Resend service

        try {
            var mimeMessage = mailSender.createMimeMessage();

            var messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom(fromAddress);
            messageHelper.setTo(email.value());
            messageHelper.setSubject("Order Confirmation - " + orderNumber);
            messageHelper.setText(buildHtmlContent(orderNumber, orderId, money, customerName, itemsCount), true);
            mailSender.send(mimeMessage);
            log.info("Order confirmation email sent to: {}", email.value());

        } catch (MessagingException e) {
            log.error("Error sending email", e);
            throw new RuntimeException("Failed to send email", e);
        }

    }

    private String buildHtmlContent(
            String orderNumber,
            OrderId orderId,
            Money totalAmount,
            String customerName,
            int itemsCount
    ) {
        try {
            String template = emailTemplate.getContentAsString(StandardCharsets.UTF_8);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDate = now.format(formatter);

            String currentYear = String.valueOf(now.getYear());

            return template
                    .replace("{{orderNumber}}", orderNumber)
                    .replace("{{orderId}}", orderId.value().toString())
                    .replace("{{orderDate}}", formattedDate)
                    .replace("{{customerName}}", customerName)
                    .replace("{{itemsCount}}", String.valueOf(itemsCount))
                    .replace("{{totalAmount}}", totalAmount.amount().toString())
                    .replace("{{currency}}", totalAmount.currency().getCurrencyCode())
                    .replace("{{year}}", currentYear)
                    .replace("{{companyName}}", companyName);

        } catch (IOException e) {
            log.error("Error loading email template", e);
            throw new RuntimeException("Failed to load email template", e);
        }
    }
}
