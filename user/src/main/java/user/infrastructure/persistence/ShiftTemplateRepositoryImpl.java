package user.infrastructure.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import user.domain.model.ShiftTemplate;
import user.domain.repository.ShiftTemplateRepository;
import user.infrastructure.mapper.ShiftTemplateMapper;
import user.infrastructure.persistence.entities.ShiftTemplateDocument;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ShiftTemplateRepositoryImpl implements ShiftTemplateRepository {

    private MongoShiftTemplateRepository mongoShiftTemplateRepository;

    @Override
    public List<ShiftTemplate> findAll() {
        List<ShiftTemplateDocument> documents = mongoShiftTemplateRepository.findAll();
        return documents.stream()
                .map(ShiftTemplateMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ShiftTemplate save(ShiftTemplate template) {
        // Map domain object to MongoDB document
        ShiftTemplateDocument document = ShiftTemplateMapper.toDocument(template);

        // Save document to MongoDB
        mongoShiftTemplateRepository.save(document);
        return template;
    }

    @Override
    public Optional<ShiftTemplate> findById(String templateId) {
        return mongoShiftTemplateRepository.findById(templateId)
                .map(ShiftTemplateMapper::toDomain);
    }

    @Override
    public void deleteAll() {
        mongoShiftTemplateRepository.deleteAll();

    }

    @Override
    public int count() {
        long mongoCount = mongoShiftTemplateRepository.count();
        return (int) mongoCount;
    }

    @Override
    public boolean existsById(String templateId) {
        return mongoShiftTemplateRepository.existsById(templateId);
    }

    @Override
    public void deleteById(String templateId) {
        mongoShiftTemplateRepository.deleteById(templateId);
    }


}
