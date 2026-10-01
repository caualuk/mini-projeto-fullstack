package br.com.bitsolucoes.portal_solicitacoes.dto.request;

import br.com.bitsolucoes.portal_solicitacoes.enums.RequestCategory;
import br.com.bitsolucoes.portal_solicitacoes.enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RequestResponseDTO(
        UUID id,
        String title,
        String description,
        RequestCategory category,
        String requester,
        LocalDateTime createdAt,
        RequestStatus status
) {
}
