package restaurant.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.model.Supplement;
import restaurant.domain.repository.SupplementRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplementDomainService {

    private final SupplementRepository supplementRepository;

    public Supplement addSupplement(Supplement supplement) {
        return supplementRepository.save(supplement);
    }

    public List<Supplement> getAllSupplements() {
        return supplementRepository.findAll(); // Retrieve all supplements
    }

    public Optional<Supplement> getSupplementById(String id) {
        return supplementRepository.findById(id);
    }

    public Supplement updateSupplement(Supplement supplement) {
        return supplementRepository.save(supplement);
    }

    public void deleteSupplement(String id) {
        supplementRepository.deleteById(id);
    }

    public List<Supplement> getSupplementsByIds(List<String> supplementIds) {
        return new ArrayList<>(supplementRepository.findAllById(supplementIds));
    }
    public List<Supplement> searchByNamePrefix(String query, int page, int size) {
        return supplementRepository.searchByNamePrefix(query, page, size);
    }
}
