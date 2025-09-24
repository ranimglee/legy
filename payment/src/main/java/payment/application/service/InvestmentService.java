package payment.application.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import payment.application.dto.In.InvestmentRequest;
import payment.application.dto.Out.InvestmentResponse;
import payment.domain.exception.InvestmentNotFoundException;
import payment.domain.model.Investment;
import payment.domain.model.InvestmentTypeDefinition;
import payment.domain.repository.InvestmentRepository;
import payment.domain.repository.InvestmentTypeRepository;
import payment.domain.service.InvestmentTypeProcessor;
import payment.infrastructure.audit.AuditLogger;
import payment.infrastructure.mail.MailService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final InvestmentTypeRepository typeRepository;
    private final MailService mailService;
    private final AuditLogger auditLogger;
    private final Map<String, InvestmentTypeProcessor> processors;

    // -------------------------------
    // CREATE INVESTMENT FROM DYNAMIC TYPE
    // -------------------------------
    public Investment createInvestment(InvestmentRequest request) throws MessagingException {
        InvestmentTypeDefinition type = typeRepository.findById(request.getTypeId());
        if (type == null) {
            throw new IllegalArgumentException("Invalid investment type ID: " + request.getTypeId());
        }

        String processorKey = type.getName().toLowerCase();
        InvestmentTypeProcessor processor = processors.getOrDefault(processorKey, processors.get("generic"));

        processor.validate(request, type);
        Map<String, Object> derived = processor.computeDerivedFields(request, type);

        Investment investment = new Investment();
        investment.setTypeId(type.getId());
        investment.setFieldValues(request.getFields());
        investment.setDerivedFields(derived);
        investment.setDate(LocalDateTime.now());
        investment.setRef(generateUniqueReferenceForInvestor(request.getEmail()));
        investment.setInvestorName(request.getInvestorName());
        investment.setEmail(request.getEmail());
        investment.setAmount(request.getAmount());
        investment.setDescription(request.getDescription());

        Investment saved = investmentRepository.save(investment);

        auditLogger.log("ADD_INVESTMENT", saved.getId(), saved.getDate());
        mailService.sendConfirmation(saved.getEmail(), saved);

        return saved;
    }


    // -------------------------------
    // STATIC REF GENERATOR
    // -------------------------------
    private String generateUniqueReferenceForInvestor(String email) {
        String prefix = "INVS";
        return prefix + hashEmailToNumber(email);
    }

    private int hashEmailToNumber(String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(email.getBytes(StandardCharsets.UTF_8));

            int uniqueNumber = ((hash[0] & 0xFF) << 24) |
                    ((hash[1] & 0xFF) << 16) |
                    ((hash[2] & 0xFF) << 8)  |
                    (hash[3] & 0xFF);

            return Math.abs(uniqueNumber) % 10000;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating unique reference", e);
        }
    }

    // -------------------------------
    // FIND / UPDATE / DELETE / PAGINATION
    // -------------------------------

    public Investment findById(String id) {
        return investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException("Investment not found with id: " + id));
    }

    public Investment updateInvestment(Investment investment) {
        InvestmentTypeDefinition type = typeRepository.findById(investment.getTypeId());
        InvestmentTypeProcessor processor = processors.getOrDefault(type.getName().toLowerCase(), processors.get("generic"));

        // Wrap updated entity into InvestmentRequest
        InvestmentRequest request = new InvestmentRequest();
        request.setFields(investment.getFieldValues());
        request.setAmount(investment.getAmount());
        request.setEmail(investment.getEmail());
        request.setInvestorName(investment.getInvestorName());
        request.setDescription(investment.getDescription());

        // Recompute derived fields
        Map<String, Object> derived = processor.computeDerivedFields(request, type);
        investment.setDerivedFields(derived);

        Investment saved = investmentRepository.save(investment);
        auditLogger.log("UPDATE_INVESTMENT", saved.getId(), saved.getDate());
        return saved;
    }


    public void deleteInvestment(String investmentId) {
        investmentRepository.deleteById(investmentId);
        auditLogger.log("DELETE_INVESTMENT", investmentId, LocalDateTime.now());
    }

    public Page<Investment> getAllInvestments(Pageable pageable) {
        return investmentRepository.findAll(pageable);
    }


    public InvestmentResponse toResponse(Investment investment) {
        return InvestmentResponse.builder()
                .amount(investment.getAmount())
                .description(investment.getDescription())
                .investorName(investment.getInvestorName())
                .email(investment.getEmail())
                .date(investment.getDate())
                .typeId(investment.getTypeId())
                .ref(investment.getRef())
                .derivedFields(investment.getDerivedFields())

                .build();
    }

}
