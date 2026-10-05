package br.com.bitsolucoes.portal_solicitacoes.service;

import br.com.bitsolucoes.portal_solicitacoes.dto.dashboard.DashboardResponseDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.solicitation.CreateSolicitationDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.solicitation.RequestSolicitationDTO;
import br.com.bitsolucoes.portal_solicitacoes.dto.solicitation.UpdateSolicitationDTO;
import br.com.bitsolucoes.portal_solicitacoes.entity.Solicitation;
import br.com.bitsolucoes.portal_solicitacoes.entity.User;
import br.com.bitsolucoes.portal_solicitacoes.enums.SolicitationCategory;
import br.com.bitsolucoes.portal_solicitacoes.enums.RequestStatus;
import br.com.bitsolucoes.portal_solicitacoes.exception.BusinessException;
import br.com.bitsolucoes.portal_solicitacoes.exception.ResourceNotFoundException;
import br.com.bitsolucoes.portal_solicitacoes.repository.SolicitationRepository;
import br.com.bitsolucoes.portal_solicitacoes.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SolicitationService {

    private final SolicitationRepository solicitationRepository;
    private final UserRepository userRepository;

    public RequestSolicitationDTO create(CreateSolicitationDTO dto) {

        // REGRA:
        // Toda solicitação criada deve iniciar como OPEN

        Solicitation solicitation = new Solicitation();

        String username = SecurityContextHolder.getContext()
                        .getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário não encontrado.")
                );

        solicitation.setTitle(dto.title());
        solicitation.setDescription(dto.description());
        solicitation.setCategory(dto.category());
        solicitation.setStatus(RequestStatus.OPEN);
        solicitation.setCreatedAt(LocalDateTime.now());
        solicitation.setUser(user);

        // O usuário solicitante deve vir do usuário autenticado
        // e não do DTO enviado pelo front.

        Solicitation savedSolicitation = solicitationRepository.save(solicitation);

        return toResponseDTO(savedSolicitation);
    }

    public RequestSolicitationDTO findById(UUID id){
        Solicitation solicitation = findRequest(id);

        return toResponseDTO(solicitation);
    }

    public List<RequestSolicitationDTO> findAll() {

        return solicitationRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public RequestSolicitationDTO update(UUID id, UpdateSolicitationDTO dto){
        Solicitation solicitation = findRequest(id);

        if(solicitation.getStatus() != RequestStatus.OPEN){
            throw new BusinessException("Apenas solicitações abertas podem ser editadas!");
        }

        solicitation.setTitle(dto.title());
        solicitation.setDescription(dto.description());
        solicitation.setCategory(dto.category());

        Solicitation updatedSolicitation = solicitationRepository.save(solicitation);

        return toResponseDTO(updatedSolicitation);
    }

    public void delete(UUID id) {
        Solicitation solicitation = findRequest(id);

        if(solicitation.getStatus() != RequestStatus.OPEN) {
            throw new BusinessException("Apenas solicitações abertas podem ser excluídas!");
        }

        solicitationRepository.delete(solicitation);
    }

    public RequestSolicitationDTO updateStatus(UUID id, RequestStatus status) {
        Solicitation solicitation = findRequest(id);

        solicitation.setStatus(status);
        Solicitation updatedSolicitation = solicitationRepository.save(solicitation);

        return toResponseDTO(updatedSolicitation);
    }

    private Solicitation findRequest(UUID id) {
        return solicitationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Solicitação não encontrada."));
    }

    private RequestSolicitationDTO toResponseDTO(Solicitation solicitation) {

        return new RequestSolicitationDTO(
                solicitation.getId(),
                solicitation.getTitle(),
                solicitation.getDescription(),
                solicitation.getCategory(),
                solicitation.getUser().getUsername(),
                solicitation.getCreatedAt(),
                solicitation.getStatus()
        );
    }

    public List<RequestSolicitationDTO> findWithFilters(
            LocalDateTime startDate,
            LocalDateTime endDate,
            SolicitationCategory category,
            RequestStatus status,
            String title
    ){

        if(startDate != null && startDate.isAfter(LocalDateTime.now())){
            throw new BusinessException("A data inicial não pode ser posterior à data atual.");
        }

       return solicitationRepository.findAll()
               .stream()
               .filter(solicitation -> startDate == null ||
                       !solicitation.getCreatedAt().isBefore(startDate))
               .filter(solicitation -> endDate == null ||
                       !solicitation.getCreatedAt().isAfter(endDate))
               .filter(solicitation -> category == null ||
                       solicitation.getCategory() == category)
               .filter(solicitation -> status == null ||
                       solicitation.getStatus() == status)
               .filter(solicitation -> title == null || title.isBlank() ||
                       solicitation.getTitle().toLowerCase()
                               .contains(title.toLowerCase()))
               .map(this::toResponseDTO)
               .toList();
    }

    public DashboardResponseDTO getDashboard() {


        long totalRequests = solicitationRepository.count();

        long openRequests = solicitationRepository.countByStatus(RequestStatus.OPEN);

        long requestsInProgress = solicitationRepository.countByStatus(RequestStatus.IN_PROGRESS);

        long completedRequests = solicitationRepository.countByStatus(RequestStatus.COMPLETED);

        return new DashboardResponseDTO(
                totalRequests,
                openRequests,
                requestsInProgress,
                completedRequests
        );
    }
}
