package br.com.bitsolucoes.portal_solicitacoes.dto.request;

import br.com.bitsolucoes.portal_solicitacoes.enums.RequestCategory;

public record CreateRequestDTO(
        String title,
        String description,
        RequestCategory category
) {
}
