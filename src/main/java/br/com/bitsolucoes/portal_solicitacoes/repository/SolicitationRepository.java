package br.com.bitsolucoes.portal_solicitacoes.repository;

import br.com.bitsolucoes.portal_solicitacoes.entity.Solicitation;
import br.com.bitsolucoes.portal_solicitacoes.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SolicitationRepository extends JpaRepository<Solicitation, UUID> {

    long countByStatus(RequestStatus requestStatus);
}
