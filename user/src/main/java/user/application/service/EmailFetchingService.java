package user.application.service;

import jakarta.mail.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import user.domain.model.Email;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class EmailFetchingService {
    @Value("${spring.mail.imap.host}")
    private String imapHost;

    @Value("${spring.mail.imap.username}")
    private String username;

    @Value("${spring.mail.imap.password}")
    private String password;

    public List<Email> fetchEmails() throws MessagingException {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.ssl.enable", "true");

        Session session = Session.getInstance(props, null);
        Store store = session.getStore("imap");
        try {
            store.connect(imapHost, username, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();
            List<Email> emailList = new ArrayList<>();
            for (Message message : messages) {
                Email email = new Email();
                email.setFrom(message.getFrom()[0].toString());
                email.setSubject(message.getSubject());
                email.setDate(message.getSentDate());
                email.setContent(getTextFromMessage(message));
                emailList.add(email);
            }

            return emailList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (store.isConnected()) {
                store.close();
            }
        }
    }

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    return bodyPart.getContent().toString();
                }
            }
        }
        return "";
    }

}