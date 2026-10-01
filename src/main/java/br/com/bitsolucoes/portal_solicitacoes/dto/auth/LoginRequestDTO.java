package br.com.bitsolucoes.portal_solicitacoes.dto.auth;

public record LoginRequestDTO(
        String username,
        String password
) {
}
