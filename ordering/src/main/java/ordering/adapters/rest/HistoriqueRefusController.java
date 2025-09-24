package ordering.adapters.rest;

import jakarta.validation.Valid;
import ordering.application.dto.HistoriqueRefus.HistoriqueRefusRequestDTO;
import ordering.application.dto.HistoriqueRefus.HistoriqueRefusResponseDTO;
import ordering.application.usecase.HistoriqueRefus.AddHistoriqueRefusUseCase;
import ordering.application.usecase.HistoriqueRefus.GetAllHistoriqueRefusUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historique-refus")
public class HistoriqueRefusController {

    private final AddHistoriqueRefusUseCase addHistoriqueRefusUseCase;
    private final GetAllHistoriqueRefusUseCase getAllHistoriqueRefusUseCase;

    @Autowired
    public HistoriqueRefusController(AddHistoriqueRefusUseCase addHistoriqueRefusUseCase,
                                     GetAllHistoriqueRefusUseCase getAllHistoriqueRefusUseCase) {
        this.addHistoriqueRefusUseCase = addHistoriqueRefusUseCase;
        this.getAllHistoriqueRefusUseCase = getAllHistoriqueRefusUseCase;
    }

    @PostMapping
    public HistoriqueRefusResponseDTO addHistoriqueRefus(@Valid @RequestBody HistoriqueRefusRequestDTO request) {
        return addHistoriqueRefusUseCase.execute(request);
    }

    @GetMapping
    public List<HistoriqueRefusResponseDTO> getAllHistoriqueRefus() {
        return getAllHistoriqueRefusUseCase.execute();
    }
}
