package br.com.bitsolucoes.portal_solicitacoes.service;

import br.com.bitsolucoes.portal_solicitacoes.dto.solicitation.UpdateSolicitationDTO;
import br.com.bitsolucoes.portal_solicitacoes.entity.Solicitation;
import br.com.bitsolucoes.portal_solicitacoes.enums.SolicitationCategory;
import br.com.bitsolucoes.portal_solicitacoes.enums.SolicitationStatus;
import br.com.bitsolucoes.portal_solicitacoes.exception.BusinessException;
import br.com.bitsolucoes.portal_solicitacoes.mapper.SolicitationMapper;
import br.com.bitsolucoes.portal_solicitacoes.repository.SolicitationRepository;
import br.com.bitsolucoes.portal_solicitacoes.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SolicitationServiceTest {

    @Mock
    private SolicitationRepository solicitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SolicitationMapper solicitationMapper;

    @InjectMocks
    private SolicitationService solicitationService;

    @Test
    void impedirEdicaoDeSolicitacaoNaoAberta() {
        Solicitation solicitation = new Solicitation();
        solicitation.setStatus(SolicitationStatus.COMPLETED);

        when(solicitationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(solicitation));

        assertThrows(
                BusinessException.class,
                () -> solicitationService.update(
                        UUID.randomUUID(),
                        new UpdateSolicitationDTO(
                                "Novo título",
                                "Nova descrição",
                                SolicitationCategory.IT
                        )
                )
        );
    }

    @Test
    void editarSolicitacaoAberta() {
        Solicitation solicitation = new Solicitation();
        solicitation.setStatus(SolicitationStatus.OPEN);

        when(solicitationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(solicitation));

        UpdateSolicitationDTO dto = new UpdateSolicitationDTO(
                "Novo título",
                "Nova descrição",
                SolicitationCategory.IT
        );

        UUID id = UUID.randomUUID();

        solicitationService.update(id, dto);

        verify(solicitationMapper).updateEntity(dto, solicitation);
    }

    @Test
    void impedirExclusaoDeSolicitacaoNaoAberta() {
        Solicitation solicitation = new Solicitation();
        solicitation.setStatus(SolicitationStatus.COMPLETED);

        when(solicitationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(solicitation));

        assertThrows(
                BusinessException.class,
                () -> solicitationService.delete(UUID.randomUUID())
        );
    }

    @Test
    void excluirSolicitacaoAberta() {
        Solicitation solicitation = new Solicitation();
        solicitation.setStatus(SolicitationStatus.OPEN);

        when(solicitationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(solicitation));

        UUID id = UUID.randomUUID();

        solicitationService.delete(id);

        verify(solicitationRepository).delete(solicitation);


    }
}