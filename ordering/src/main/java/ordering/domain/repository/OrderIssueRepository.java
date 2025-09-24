package ordering.domain.repository;

import ordering.domain.model.OrderIssue;

import java.util.List;
import java.util.Optional;

public interface OrderIssueRepository {
    List<OrderIssue> findAll();


    Optional<OrderIssue> findById(String id);

    OrderIssue save(OrderIssue issue);

    boolean existsByIssueRef(String issueRef);

    long countByOrderId(String s);
    List<OrderIssue> findAllByUserId(String userId);


}
