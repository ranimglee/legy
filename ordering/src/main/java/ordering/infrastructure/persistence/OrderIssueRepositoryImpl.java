package ordering.infrastructure.persistence;

import lombok.AllArgsConstructor;
import ordering.domain.model.IssueResolutionStatus;
import ordering.domain.model.OrderIssue;
import ordering.domain.repository.OrderIssueRepository;
import ordering.infrastructure.Document.OrderIssueDocument;
import ordering.infrastructure.mapper.OrderIssueDocumentMapper;
import ordering.infrastructure.repository.MongoOrderIssueRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class OrderIssueRepositoryImpl implements OrderIssueRepository {

    private final MongoOrderIssueRepository mongoOrderIssueRepository;
    private final OrderIssueDocumentMapper issueMapper;


    @Override
    public List<OrderIssue> findAll() {
        return mongoOrderIssueRepository.findAll()
                .stream()
                .map(issueMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OrderIssue> findById(String id) {
        return mongoOrderIssueRepository.findById(id)
                .map(issueMapper::toDomain);
    }

    @Override
    public OrderIssue save(OrderIssue issue) {
        OrderIssueDocument document = issueMapper.toDocument(issue);
        OrderIssueDocument savedDoc = mongoOrderIssueRepository.save(document);
        return issueMapper.toDomain(savedDoc);
    }
    @Override
    public List<OrderIssue> findAllByUserId(String userId) {
        return mongoOrderIssueRepository.findAllByUserId(userId)
                .stream()
                .map(OrderIssueDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByIssueRef(String issueRef) {
        return mongoOrderIssueRepository.existsByIssueRef(issueRef);
    }

    @Override
    public long countByOrderId(String orderId) {
        return mongoOrderIssueRepository.countByOrderId(orderId);
    }

}
