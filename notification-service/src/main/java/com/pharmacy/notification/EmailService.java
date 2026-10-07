package com.pharmacy.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    // true: chi in noi dung email ra log, khong gui that (dung khi chua co SMTP)
    @Value("${notification.mock:true}")
    private boolean mock;

    @Value("${spring.mail.username:no-reply@pharmacy.local}")
    private String from;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    /** @return "SENT" neu gui that, "MOCK" neu chi ghi log */
    public String send(String to, String subject, String body) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();

        if (mock || sender == null) {
            log.info("""

                    ===== [MOCK EMAIL] =====
                    To     : {}
                    Subject: {}
                    {}
                    ========================""", to, subject, body);
            return "MOCK";
        }

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        sender.send(msg);
        log.info("Da gui email toi {}", to);
        return "SENT";
    }
}
