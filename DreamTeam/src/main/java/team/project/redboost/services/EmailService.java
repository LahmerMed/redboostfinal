package team.project.redboost.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Collections;
import java.util.Properties;

@Service
public class EmailService {

    private static final String CLIENT_ID = "717073407944-pmbmhmhpdg3jove9da1582o9ihl2itat.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REFRESH_TOKEN = "1//04zVEiaFZSdMoCgYIARAAGAQSNwF-L9Ir6Atk0teYm-jQqoFjQv7Qt5VL29f7fBQkPXQ4zI_HgCg_3_jt7fy-wo6zHq-Su0tYY_Q";

    private static final String USER_EMAIL = "messaoudimarwa75@gmail.com";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final HttpTransport httpTransport;
    private Credential credential;

    public EmailService() throws GeneralSecurityException, IOException {
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.credential = getCredentials();
    }

    /**
     * Génère un objet Credential en utilisant le Refresh Token
     */
    private Credential getCredentials() throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                new StringReader("{\"installed\":{\"client_id\":\"" + CLIENT_ID + "\",\"client_secret\":\"" + CLIENT_SECRET + "\"}}")
        );

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singletonList("https://www.googleapis.com/auth/gmail.send")
        ).build();

        Credential credential = flow.createAndStoreCredential(new GoogleTokenResponse().setRefreshToken(REFRESH_TOKEN), null);
        credential.refreshToken();  // Actualisation du token

        return credential;
    }

    /**
     * Création du service Gmail
     */
    private Gmail getGmailService() throws IOException {
        return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName("MyApp")
                .build();
    }

    /**
     * Envoi d'un email via Gmail API
     */
    public void sendEmail(String to, String subject, String body) throws MessagingException, IOException {
        try {
            Gmail gmail = getGmailService();

            MimeMessage mimeMessage = createEmail(to, USER_EMAIL, subject, body);
            Message message = createMessageWithEmail(mimeMessage);

            gmail.users().messages().send(USER_EMAIL, message).execute();
        } catch (IOException e) {
            if (e.getMessage().contains("invalid_grant")) {
                // Refresh the token and retry
                credential.refreshToken();
                Gmail gmail = getGmailService();

                MimeMessage mimeMessage = createEmail(to, USER_EMAIL, subject, body);
                Message message = createMessageWithEmail(mimeMessage);

                gmail.users().messages().send(USER_EMAIL, message).execute();
            } else {
                throw e;
            }
        }
    }

    /**
     * Crée un email formaté en MIME
     */
    private MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);

        return email;
    }

    /**
     * Encode un email MIME en base64
     */
    private Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}