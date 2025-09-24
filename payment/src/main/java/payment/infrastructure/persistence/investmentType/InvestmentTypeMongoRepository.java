package payment.infrastructure.persistence.investmentType;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import payment.domain.model.InvestmentTypeDefinition;
@Repository
public interface InvestmentTypeMongoRepository extends MongoRepository<InvestmentTypeDocument, String> {
}
