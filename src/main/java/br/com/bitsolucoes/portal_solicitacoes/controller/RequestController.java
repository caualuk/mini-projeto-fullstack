package br.com.bitsolucoes.portal_solicitacoes.controller;

import br.com.bitsolucoes.portal_solicitacoes.dto.request.CreateRequestDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.request.RequestResponseDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.request.UpdateRequestDTO;
import br.com.bitsolucoes.portal_solicitacoes.enums.RequestStatus;
import br.com.bitsolucoes.portal_solicitacoes.service.RequestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/requests")
@AllArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestResponseDTO> create(
            @RequestBody @Valid CreateRequestDTO dto
    ) {
        return ResponseEntity.ok(requestService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<RequestResponseDTO>> findAll() {
        return ResponseEntity.ok(
                requestService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestResponseDTO> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(requestService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequestResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(
                requestService.update(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        requestService.delete(id);

        return ResponseEntity.noContent().build();
    }

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
