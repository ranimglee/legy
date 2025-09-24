package restaurant.domain.repository;

import restaurant.domain.model.Supplement;

import java.util.List;
import java.util.Optional;

public interface SupplementRepository {
    Supplement save(Supplement supplement); // Ajouter ou mettre à jour un supplément

    Optional<Supplement> findById(String id); // Trouver un supplément par ID

    List<Supplement> findAll(); // Récupérer tous les suppléments

    void deleteById(String id); // Supprimer un supplément par ID
    List<Supplement> findAllById(List<String> supplementIds);

    List<Supplement> searchByNamePrefix(String query, int page, int size);
}
