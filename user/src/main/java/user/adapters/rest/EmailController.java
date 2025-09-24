package user.adapters.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import user.application.service.EmailFetchingService;
import user.domain.model.Email;

import java.util.List;

@RestController
@RequestMapping("/api/moderateur")
@AllArgsConstructor
public class EmailController {

    private final EmailFetchingService emailService;

    @GetMapping("/get-emails")
    public List<Email> getEmails() throws Exception {
        return emailService.fetchEmails();
    }
}