package br.com.bitsolucoes.portal_solicitacoes.dto.request;

import br.com.bitsolucoes.portal_solicitacoes.enums.RequestCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRequestDTO(
        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "A categoria é obrigatória")
        RequestCategory category
) {
}
