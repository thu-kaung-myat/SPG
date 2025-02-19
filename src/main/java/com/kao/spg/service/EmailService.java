package com.kao.spg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVoucherEmail(String toEmail, String voucherCode, String itemName, long quantity, double totalAmount) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Transaction time
        String transactionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Create HTML email content
        String htmlContent = """
                <html>
                <body>
                    <h1>🎟️ Thank You for Your Purchase!</h1>
                    <p>Hello,</p>
                    <p>Your purchase was successful. Here are the details of your transaction:</p>
                    
                    <h3>📝 Order Details</h3>
                    <ul>
                        <li><strong>Voucher Code:</strong> %s</li>
                        <li><strong>Item Name:</strong> %s</li>
                        <li><strong>Quantity:</strong> %d</li>
                        <li><strong>Total Amount:</strong> $%.2f</li>
                        <li><strong>Transaction Time:</strong> %s</li>
                    </ul>

                    <p>We appreciate your business! If you have any questions, feel free to contact us.</p>
                    <hr>
                    <p>Regards, <br/>Travinity</p>
                </body>
                </html>
                """.formatted(voucherCode, itemName, quantity, totalAmount, transactionTime);

        // Set email details
        helper.setTo(toEmail);
        helper.setSubject("🎟️ Your Purchase Receipt");
        helper.setText(htmlContent, true);  // 'true' means HTML format

        // Send the email
        mailSender.send(message);
    }
}



