package ordering.infrastructure.persistence;

import ordering.infrastructure.Document.IssueMonthlyStats;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IssueMonthlyStatsRepository extends MongoRepository<IssueMonthlyStats, String> {
    Optional<IssueMonthlyStats> findByMonth(String month);
}
