package br.com.bitsolucoes.portal_solicitacoes.service;

import br.com.bitsolucoes.portal_solicitacoes.dto.request.CreateRequestDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.request.RequestResponseDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.request.UpdateRequestDTO;
import br.com.bitsolucoes.portal_solicitacoes.entity.Request;
import br.com.bitsolucoes.portal_solicitacoes.enums.RequestStatus;
import br.com.bitsolucoes.portal_solicitacoes.exception.BusinessException;
import br.com.bitsolucoes.portal_solicitacoes.exception.ResourceNotFoundException;
import br.com.bitsolucoes.portal_solicitacoes.repository.RequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestResponseDTO create(CreateRequestDTO dto) {

        // REGRA:
        // Toda solicitação criada deve iniciar como OPEN

        Request request = new Request();

        request.setTitle(dto.title());
        request.setDescription(dto.description());
        request.setCategory(dto.category());
        request.setStatus(RequestStatus.OPEN);
        request.setCreatedAt(LocalDateTime.now());

        // O usuário solicitante deve vir do usuário autenticado
        // e não do DTO enviado pelo front.

        Request savedRequest = requestRepository.save(request);

        return toResponseDTO(savedRequest);
    }

    public RequestResponseDTO findById(UUID id){
        Request request = findRequest(id);

        return toResponseDTO(request);
    }

    public List<RequestResponseDTO> findAll() {

        return requestRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public RequestResponseDTO update(UUID id, UpdateRequestDTO dto){
        Request request = findRequest(id);

        if(request.getStatus() != RequestStatus.OPEN){
            throw new BusinessException("Apenas solicitações abertas podem ser editadas!");
        }

        request.setTitle(dto.title());
        request.setDescription(dto.description());
        request.setCategory(dto.category());

        Request updatedRequest = requestRepository.save(request);

        return toResponseDTO(updatedRequest);
    }

    public void delete(UUID id) {
        Request request = findRequest(id);

        if(request.getStatus() != RequestStatus.OPEN) {
            throw new BusinessException("Apenas solicitações abertas podem ser excluídas!");
        }

        requestRepository.delete(request);
    }

    public RequestResponseDTO updateStatus(UUID id, RequestStatus status) {
        Request request = findRequest(id);

        request.setStatus(status);
        Request updatedRequest = requestRepository.save(request);

        return toResponseDTO(updatedRequest);
    }

    private Request findRequest(UUID id) {
        return requestRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Solicitação não encontrada."));
    }

    private RequestResponseDTO toResponseDTO(Request request) {

        return new RequestResponseDTO(
                request.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getUser().getUsername(),
                request.getCreatedAt(),
                request.getStatus()
        );
    }
}
