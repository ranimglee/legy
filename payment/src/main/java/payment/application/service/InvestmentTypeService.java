package payment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payment.domain.exception.EntityNotFoundException;
import payment.domain.exception.InvestmentNotFoundException;
import payment.domain.model.FieldDefinition;
import payment.domain.model.InvestmentTypeDefinition;
import payment.domain.repository.InvestmentTypeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvestmentTypeService {

    private final InvestmentTypeRepository typeRepository;

    @Transactional
    public InvestmentTypeDefinition createInvestmentType(InvestmentTypeDefinition type) {
        validateInvestmentType(type);
        return typeRepository.save(type);
    }

    @Transactional(readOnly = true)
    public List<InvestmentTypeDefinition> getAllInvestmentTypes() {
        return typeRepository.findAll();
    }

    @Transactional
    public InvestmentTypeDefinition updateInvestmentType(String id, InvestmentTypeDefinition typeDetails) {
        InvestmentTypeDefinition existingType = typeRepository.findById(id);

        if (existingType == null) {
            throw new EntityNotFoundException("Investment type not found with id: " + id);
        }

        existingType.setName(typeDetails.getName());
        existingType.setFields(typeDetails.getFields());
        existingType.setProcessorClassName(typeDetails.getProcessorClassName());
        existingType.setDerivedFieldFormulas(typeDetails.getDerivedFieldFormulas());

        validateInvestmentType(existingType);
        return typeRepository.save(existingType);
    }


    @Transactional
    public void deleteInvestmentType(String id) {
        if (!typeRepository.existsById(id)) {
            throw new EntityNotFoundException("Investment type not found with id: " + id);
        }
        typeRepository.deleteById(id);
    }

    private void validateInvestmentType(InvestmentTypeDefinition type) {
        if (type.getId() == null || type.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Investment type ID cannot be empty");
        }
        if (type.getName() == null || type.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Investment type name cannot be empty");
        }
        if (type.getFields() == null || type.getFields().isEmpty()) {
            throw new IllegalArgumentException("Investment type fields cannot be null or empty");
        }
        if (type.getProcessorClassName() == null || type.getProcessorClassName().trim().isEmpty()) {
            throw new IllegalArgumentException("Processor class name cannot be empty");
        }
        if (type.getDerivedFieldFormulas() == null) {
            throw new IllegalArgumentException("Derived field formulas cannot be null");
        }
        // Additional validation for fields if needed (e.g., checking FieldDefinition objects)
        for (Map.Entry<String, FieldDefinition> entry : type.getFields().entrySet()) {
            if (entry.getKey() == null || entry.getKey().trim().isEmpty()) {
                throw new IllegalArgumentException("Field key cannot be empty");
            }
            if (entry.getValue() == null) {
                throw new IllegalArgumentException("Field definition cannot be null for key: " + entry.getKey());
            }
        }
    }
}