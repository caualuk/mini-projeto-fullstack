package br.com.bitsolucoes.portal_solicitacoes.service;

import br.com.bitsolucoes.portal_solicitacoes.dto.dashboard.DashboardResponseDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.request.CreateRequestDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.request.RequestResponseDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.request.UpdateRequestDTO;
import br.com.bitsolucoes.portal_solicitacoes.entity.Request;
import br.com.bitsolucoes.portal_solicitacoes.entity.User;
import br.com.bitsolucoes.portal_solicitacoes.enums.RequestCategory;
import br.com.bitsolucoes.portal_solicitacoes.enums.RequestStatus;
import br.com.bitsolucoes.portal_solicitacoes.exception.BusinessException;
import br.com.bitsolucoes.portal_solicitacoes.exception.ResourceNotFoundException;
import br.com.bitsolucoes.portal_solicitacoes.repository.RequestRepository;
import br.com.bitsolucoes.portal_solicitacoes.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public RequestResponseDTO create(CreateRequestDTO dto) {

        // REGRA:
        // Toda solicitação criada deve iniciar como OPEN

        Request request = new Request();

        String username = SecurityContextHolder.getContext()
                        .getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário não encontrado.")
                );

        request.setTitle(dto.title());
        request.setDescription(dto.description());
        request.setCategory(dto.category());
        request.setStatus(RequestStatus.OPEN);
        request.setCreatedAt(LocalDateTime.now());
        request.setUser(user);

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

    public List<RequestResponseDTO> findWithFilters(
            LocalDateTime startDate,
            LocalDateTime endDate,
            RequestCategory category,
            RequestStatus status,
            String title
    ){

        if(startDate != null && startDate.isAfter(LocalDateTime.now())){
            throw new BusinessException("A data inicial não pode ser posterior à data atual.");
        }

       return requestRepository.findAll()
               .stream()
               .filter(request -> startDate == null ||
                       !request.getCreatedAt().isBefore(startDate))
               .filter(request -> endDate == null ||
                       !request.getCreatedAt().isAfter(endDate))
               .filter(request -> category == null ||
                       request.getCategory() == category)
               .filter(request -> status == null ||
                       request.getStatus() == status)
               .filter(request -> title == null || title.isBlank() ||
                       request.getTitle().toLowerCase()
                               .contains(title.toLowerCase()))
               .map(this::toResponseDTO)
               .toList();
    }

    public DashboardResponseDTO getDashboard() {


        long totalRequests = requestRepository.count();

        long openRequests = requestRepository.countByStatus(RequestStatus.OPEN);

        long requestsInProgress = requestRepository.countByStatus(RequestStatus.IN_PROGRESS);

        long completedRequests = requestRepository.countByStatus(RequestStatus.COMPLETED);

        return new DashboardResponseDTO(
                totalRequests,
                openRequests,
                requestsInProgress,
                completedRequests
        );
    }
}
