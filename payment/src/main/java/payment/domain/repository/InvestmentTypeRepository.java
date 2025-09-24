package payment.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import payment.domain.model.InvestmentTypeDefinition;

import java.util.List;


public interface InvestmentTypeRepository {

    InvestmentTypeDefinition save(InvestmentTypeDefinition type);
    InvestmentTypeDefinition findById(String id);

    List<InvestmentTypeDefinition> findAll();

    boolean existsById(String id);

    void deleteById(String id);
}



