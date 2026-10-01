package br.com.bitsolucoes.portal_solicitacoes.repository;

import br.com.bitsolucoes.portal_solicitacoes.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID> {
}
