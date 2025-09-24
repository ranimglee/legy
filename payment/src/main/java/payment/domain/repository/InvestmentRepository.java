package payment.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payment.domain.model.Investment;

import java.util.List;
import java.util.Optional;

public interface InvestmentRepository {
    Investment save(Investment investment);

    Optional<Investment>  findById(String id);

    void deleteById(String investmentId);

    Page<Investment> findAll(Pageable pageable);

    List<Investment> findByRef(String ref);

    Optional<Investment> findTopByEmailOrderByDateDesc(String investorEmail);
}
