package ordering.infrastructure.repository;

import ordering.infrastructure.Document.OrderIssueDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoOrderIssueRepository extends MongoRepository<OrderIssueDocument, String> {
    List<OrderIssueDocument> findAllByUserId(String userId);

    boolean existsByIssueRef(String issueRef);

    long countByOrderId(String orderId);
}
