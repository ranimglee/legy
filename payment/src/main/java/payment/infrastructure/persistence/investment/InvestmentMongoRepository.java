package payment.infrastructure.persistence.investment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository

public interface InvestmentMongoRepository extends MongoRepository<InvestmentDocument, String> {
    List<InvestmentDocument> findByRef(String ref);
    Page<InvestmentDocument> findAll(Pageable pageable);
    @Query(value = "{ 'email': ?0 }", sort = "{ 'date' : -1 }")
    Optional<InvestmentDocument> findTopByEmailOrderByDateDesc(String email);


}
