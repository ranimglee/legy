package payment.infrastructure.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import payment.domain.model.Investment;

import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendConfirmation(String toEmail, Investment investment) throws MessagingException {
        if (toEmail == null || toEmail.isBlank()) return;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        // Formater la date pour qu'elle s'affiche sans l'heure
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formattedDate = investment.getDate().format(formatter);
        String htmlContent = "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Confirmation d'Investissement</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
                ".container { max-width: 600px; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }" +
                ".header { background-color: #ff680d; padding: 10px; color: white; text-align: center; font-size: 20px; font-weight: bold; border-radius: 6px 6px 0 0; }" +
                ".content { padding: 20px; color: #441406; }" +
                ".footer { margin-top: 20px; font-size: 14px; color: #5cac0e; text-align: center; }" +
                ".highlight { color: #d32701; font-weight: bold; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>Confirmation d'Investissement</div>" +
                "<div class='content'>" +
                "<p>Bonjour,</p>" +
                "<p>Nous confirmons la réception de votre investissement de <span class='highlight'>" + investment.getAmount() + " €</span>.</p>" +
                "<p><strong>Description :</strong> " + investment.getDescription() + "</p>" +
                "<p><strong>Date :</strong> " + formattedDate + "</p>" +
                "<p>Merci pour votre confiance.</p>" +
                "<p>L'équipe <span class='highlight'>Legy</span></p>" +
                "</div>" +
                "<div class='footer'>© 2024 Legy. Tous droits réservés.</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        helper.setTo(toEmail);
        helper.setSubject("Confirmation d'investissement");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
