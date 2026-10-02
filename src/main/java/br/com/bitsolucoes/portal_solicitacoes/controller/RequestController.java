package br.com.bitsolucoes.portal_solicitacoes.controller;

import br.com.bitsolucoes.portal_solicitacoes.dto.dashboard.DashboardResponseDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.request.CreateRequestDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.request.RequestResponseDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.request.UpdateRequestDTO;
import br.com.bitsolucoes.portal_solicitacoes.enums.RequestCategory;
import br.com.bitsolucoes.portal_solicitacoes.enums.RequestStatus;
import br.com.bitsolucoes.portal_solicitacoes.service.RequestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/requests")
@AllArgsConstructor
public class RequestController {
    private final RequestService requestService;

    // POST
    // Cria a solicitação
    @PostMapping
    public ResponseEntity<RequestResponseDTO> create(
            @RequestBody @Valid CreateRequestDTO dto
    ) {
        return ResponseEntity.ok(requestService.create(dto));
    }

    // GET
    // Busca as solicitações
    @GetMapping
    public ResponseEntity<List<RequestResponseDTO>> findAll(
            @RequestParam(required = false)LocalDateTime startDate,
            @RequestParam(required = false)LocalDateTime endDate,
            @RequestParam(required = false) RequestCategory category,
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) String title
            ) {
        return ResponseEntity.ok(
                requestService.findWithFilters(startDate, endDate, category, status, title)
        );
    }

    // Busca as solicitações por ID
    @GetMapping("/{id}")
    public ResponseEntity<RequestResponseDTO> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(requestService.findById(id));
    }

    // Busca o Dashboard
    @GetMapping("/dashboard")
    ResponseEntity<DashboardResponseDTO> getDashboard() {
        return ResponseEntity.ok(requestService.getDashboard());
    }

    // DELETE

    // Deleta a solicitação
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        requestService.delete(id);

        return ResponseEntity.noContent().build();
    }

    // UPDATE

    // Atualiza a solicitação
    @PutMapping("/{id}")
    public ResponseEntity<RequestResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(
                requestService.update(id, dto)
        );
    }

    // Atualiza o status da solicitação
    @PatchMapping("/{id}/status")
    public ResponseEntity<RequestResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestParam RequestStatus status
    ) {
        return ResponseEntity.ok(
                requestService.updateStatus(id, status)
        );
    }



}
