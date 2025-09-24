package payment.adapters.rest;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import payment.application.service.InvestmentTypeService;
import payment.domain.model.InvestmentTypeDefinition;
import payment.domain.repository.InvestmentTypeRepository;

import java.util.List;

@RestController
@RequestMapping("/financier/investment-types")
@AllArgsConstructor
public class InvestmentTypeController {
    private final InvestmentTypeRepository typeRepository;

    private final InvestmentTypeService typeService;

    @PostMapping("/add-investment-type")
    public InvestmentTypeDefinition addType(@RequestBody InvestmentTypeDefinition type) {
        return typeService.createInvestmentType(type);
    }

    /**
     * List all investment types in the system.
     *
     * @return A list of all investment types.
     */
    @GetMapping("/list-investment-types")
    public List<InvestmentTypeDefinition> listTypes() {
        return typeService.getAllInvestmentTypes();
    }

    /**
     * Update an existing investment type.
     *
     * @param id The ID of the investment type to update
     * @param type The updated investment type details
     * @return The updated investment type
     */
    @PutMapping("/update/{id}")
    public InvestmentTypeDefinition updateType(@PathVariable String id, @RequestBody InvestmentTypeDefinition type) {
        return typeService.updateInvestmentType(id, type);
    }

    /**
     * Delete an investment type by ID.
     *
     * @param id The ID of the investment type to delete
     */
    @DeleteMapping("/delete/{id}")
    public void deleteType(@PathVariable String id) {
        typeService.deleteInvestmentType(id);
    }
}