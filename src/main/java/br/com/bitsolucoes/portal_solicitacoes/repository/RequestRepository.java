package br.com.bitsolucoes.portal_solicitacoes.repository;

import br.com.bitsolucoes.portal_solicitacoes.dto.dashboard.DashboardResponseDTO;
import br.com.bitsolucoes.portal_solicitacoes.entity.Request;
import br.com.bitsolucoes.portal_solicitacoes.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID> {

    long countByStatus(RequestStatus requestStatus);
}
