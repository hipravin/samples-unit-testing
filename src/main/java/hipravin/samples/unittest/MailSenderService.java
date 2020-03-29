package hipravin.samples.unittest;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
class MailSenderService {

    private final JavaMailSender javaMailSender;
    private final ReportProperties reportProperties;

    public MailSenderService(JavaMailSender javaMailSender, ReportProperties reportProperties) {
        this.javaMailSender = javaMailSender;
        this.reportProperties = reportProperties;
    }

    public void reportError(Throwable t) throws MessagingException {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));

        String content = "Error has happened: " + t.getMessage();

        sendEmail(reportProperties.getRecipients(), reportProperties.getFrom(), reportProperties.getSubject(), content,
                Collections.singletonMap("stacktrace.txt", sw.toString().getBytes(StandardCharsets.UTF_8)));
    }

    public void sendEmail(List<String> to, String from, String subject, String text,
                          Map<String, byte[]> textAttachments) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to.toArray(new String[]{}));
        helper.setSubject(subject);
        helper.setText(text);
        helper.setFrom(from);

        for (Map.Entry<String, byte[]> ta : textAttachments.entrySet()) {
            helper.addAttachment(ta.getKey(), new ByteArrayDataSource(ta.getValue(), "text/plain"));
        }

        javaMailSender.send(message);
    }
}