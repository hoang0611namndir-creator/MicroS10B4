package com.pharmacy.notification;

import java.text.NumberFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    /** Gui email tuy y */
    @PostMapping("/email")
    public ResponseEntity<NotificationResponse> sendEmail(@Valid @RequestBody EmailRequest req) {
        return doSend(req.to(), req.subject(), req.body());
    }

    /** Gui email hoa don cho khach hang */
    @PostMapping("/bill")
    public ResponseEntity<NotificationResponse> sendBill(@Valid @RequestBody BillNotificationRequest req) {
        NumberFormat vnd = NumberFormat.getInstance(new Locale("vi", "VN"));
        String body = """
                Kính gửi %s,

                Cảm ơn quý khách đã mua thuốc tại %s.

                Tổng tiền thuốc : %s VND
                Thuế VAT (%s%%)  : %s VND
                Tổng thanh toán : %s VND

                Trân trọng,
                %s
                """.formatted(
                req.customerName(), req.branchName(),
                vnd.format(req.subtotal()),
                req.vatRate().stripTrailingZeros().toPlainString(),
                vnd.format(req.vatAmount()),
                vnd.format(req.totalAmount()),
                req.branchName());

        return doSend(req.customerEmail(), "Hóa đơn mua thuốc - " + req.branchName(), body);
    }

    private ResponseEntity<NotificationResponse> doSend(String to, String subject, String body) {
        try {
            String status = emailService.send(to, subject, body);
            return ResponseEntity.ok(new NotificationResponse(status, to,
                    "SENT".equals(status) ? "Đã gửi email" : "Chế độ MOCK: chỉ ghi log, chưa gửi email thật"));
        } catch (MailException e) {
            log.error("Gui email that bai", e);
            return ResponseEntity.status(502)
                    .body(new NotificationResponse("FAILED", to, "Gửi email thất bại: " + e.getMessage()));
        }
    }
}
