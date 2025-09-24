package payment.infrastructure.persistence.investment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import payment.domain.model.Investment;
import payment.domain.repository.InvestmentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InvestmentAdapter implements InvestmentRepository {
    private final InvestmentMongoRepository investmentMongoRepository;

    @Autowired
    public InvestmentAdapter(InvestmentMongoRepository investmentMongoRepository) {
        this.investmentMongoRepository = investmentMongoRepository;
    }

    @Override
    public Investment save(Investment investment) {
        InvestmentDocument document = InvestmentDocument.fromDomain(investment);
        return investmentMongoRepository.save(document).toDomain();
    }

    @Override
    public Optional<Investment> findById(String id) {
        return investmentMongoRepository.findById(id)
                .map(InvestmentDocument::toDomain);
    }

    @Override
    public void deleteById(String investmentId) {
        investmentMongoRepository.deleteById(investmentId);
    }

    @Override
    public Page<Investment> findAll(Pageable pageable) {
        return investmentMongoRepository.findAll(pageable)
                .map(InvestmentDocument::toDomain);
    }

    @Override
    public List<Investment> findByRef(String ref) {
        return investmentMongoRepository.findByRef(ref).stream()
                .map(InvestmentDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Investment> findTopByEmailOrderByDateDesc(String investorEmail) {
        return investmentMongoRepository.findTopByEmailOrderByDateDesc(investorEmail)
                .map(InvestmentDocument::toDomain);
    }
}
