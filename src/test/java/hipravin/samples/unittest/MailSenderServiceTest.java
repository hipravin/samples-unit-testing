package hipravin.samples.unittest;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MailSenderServiceTest {
    @Autowired
    MailSenderService mailSenderService;

    GreenMail greenMail;
    ServerSetup[] serverSetups = new ServerSetup[]{new ServerSetup(2525, "localhost", "smtp")};

    @BeforeEach
    public void beforeEach() {
        greenMail = new GreenMail(serverSetups);
        greenMail.start();
    }

    @AfterEach
    public void afterEach() {
        greenMail.stop();
    }

    @Autowired
    ReportProperties reportProperties;

    @Test
    void testReportError() throws MessagingException, IOException {
        try {
            throw new RuntimeException("sample error message");
        } catch(Exception e) {
            mailSenderService.reportError(e);
        }

        MimeMessage[] messages = greenMail.getReceivedMessages();

        //4 recipients -> 4 emails
        assertEquals(4, greenMail.getReceivedMessages().length);
        assertEquals("Server status update", messages[0].getSubject());

        assertTrue(messages[0].getContent() instanceof MimeMultipart);
        MimeMultipart mp = (MimeMultipart) messages[0].getContent();
        assertEquals(2, mp.getCount());
        assertTrue(GreenMailUtil.getBody(mp.getBodyPart(0)).contains("sample error message"));
        assertTrue(GreenMailUtil.getBody(mp.getBodyPart(1)).contains("java.lang.RuntimeException"));
    }

    @Test
    void testSendSimple() throws MessagingException, IOException {
        Map<String, byte[]> textAttachments = new HashMap<>();
        textAttachments.put("stacktrace1.txt", "java.lang.NullPointeException at ...".getBytes(StandardCharsets.UTF_8));
        textAttachments.put("stacktrace2.txt", "java.lang.IndexOutOfBoundsException at ...".getBytes(StandardCharsets.UTF_8));

        mailSenderService.sendEmail(Collections.singletonList("receiver@company.com"), "sender@c.com", "server alert",
                "some text", textAttachments);

        MimeMessage[] messages = greenMail.getReceivedMessages();

        assertEquals(1, greenMail.getReceivedMessages().length);
        assertEquals("server alert", messages[0].getSubject());

        assertTrue(messages[0].getContent() instanceof MimeMultipart);
        MimeMultipart mp = (MimeMultipart) messages[0].getContent();
        assertEquals(3, mp.getCount());
        assertTrue(GreenMailUtil.getBody(mp.getBodyPart(0)).contains("some text"));
    }
}