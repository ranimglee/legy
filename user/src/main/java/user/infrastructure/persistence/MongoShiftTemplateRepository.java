package user.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import user.infrastructure.persistence.entities.ShiftTemplateDocument;

import java.util.List;

@Repository
public interface MongoShiftTemplateRepository extends MongoRepository<ShiftTemplateDocument,String> {
    List<ShiftTemplateDocument> findAll();
}
