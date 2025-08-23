package vn.edu.fpt.transitlink.mail_sender.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import vn.edu.fpt.transitlink.mail_sender.exception.MailSenderErrorCode;
import vn.edu.fpt.transitlink.shared.exception.SystemException;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendHtmlMail(String to, String subject, String templateName, Map<String, Object> model) {
            sendHtmlMail(List.of(to), subject, templateName, model);
    }

    @Async
    public void sendHtmlMail(List<String> to, String subject, String templateName, Map<String, Object> model) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to.toArray(String[]::new));
            helper.setSubject(subject);
            helper.setText(renderTemplate(templateName, model), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new SystemException(MailSenderErrorCode.MAIL_SENDER_ERROR, "Failed to send email", e);
        }
    }


    private String renderTemplate(String templateName, Map<String, Object> model) {
        Context context = new Context();
        context.setVariables(model);
        return templateEngine.process(templateName, context);
    }
}
