package payment.infrastructure.persistence.investmentType;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import payment.domain.model.InvestmentTypeDefinition;
import payment.domain.repository.InvestmentTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Repository
public class InvestmentTypeAdapter implements InvestmentTypeRepository {

    private final InvestmentTypeMongoRepository repository;

    @Override
    public InvestmentTypeDefinition save(InvestmentTypeDefinition typeDefinition) {
        InvestmentTypeDocument doc = InvestmentTypeDocument.fromDomain(typeDefinition);
        InvestmentTypeDocument saved = repository.save(doc);
        return saved.toDomain();
    }

    @Override
    public InvestmentTypeDefinition findById(String id) {
        return repository.findById(id)
                .map(InvestmentTypeDocument::toDomain)
                .orElse(null); // or throw a custom exception
    }

    @Override
    public List<InvestmentTypeDefinition> findAll() {
        return repository.findAll()
                .stream()
                .map(InvestmentTypeDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
