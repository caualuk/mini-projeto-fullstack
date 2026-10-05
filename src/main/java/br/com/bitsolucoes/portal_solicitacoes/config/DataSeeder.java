package br.com.bitsolucoes.portal_solicitacoes.config;

import br.com.bitsolucoes.portal_solicitacoes.entity.Solicitation;
import br.com.bitsolucoes.portal_solicitacoes.entity.User;
import br.com.bitsolucoes.portal_solicitacoes.enums.SolicitationCategory;
import br.com.bitsolucoes.portal_solicitacoes.enums.SolicitationStatus;
import br.com.bitsolucoes.portal_solicitacoes.repository.SolicitationRepository;
import br.com.bitsolucoes.portal_solicitacoes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// Popula o banco com usuários de demonstração e solicitações de exemplo.
// Só cria o que ainda não existe, então pode rodar a cada inicialização.

// Desative com APP_SEED_ENABLED=false.
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SolicitationRepository solicitationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User admin = createUserIfMissing("admin", "admin123");
        User colaborador = createUserIfMissing("colaborador", "123456");

        // Solicitações de exemplo apenas com o banco vazio
        if (solicitationRepository.count() > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        createSolicitation("Notebook para novo colaborador",
                "Preciso de um notebook configurado para o colaborador que inicia na segunda-feira.",
                SolicitationCategory.IT, SolicitationStatus.OPEN, admin, now.minusHours(2));

        createSolicitation("Atualização de dados bancários",
                "Solicito a atualização da conta para recebimento do salário a partir do próximo mês.",
                SolicitationCategory.HR, SolicitationStatus.IN_PROGRESS, colaborador, now.minusDays(1));

        createSolicitation("Compra de cadeiras ergonômicas",
                "Orçamento para 5 cadeiras ergonômicas para a equipe de suporte.",
                SolicitationCategory.PROCUREMENT, SolicitationStatus.OPEN, colaborador, now.minusDays(2));

        createSolicitation("Reembolso de despesas de viagem",
                "Reembolso das despesas da visita ao cliente em Campinas, notas fiscais anexas ao e-mail.",
                SolicitationCategory.FINANCE, SolicitationStatus.COMPLETED, admin, now.minusDays(4));

        createSolicitation("Ar-condicionado da sala de reunião",
                "O ar-condicionado da sala 3 está pingando e fazendo barulho.",
                SolicitationCategory.INFRASTRUCTURE, SolicitationStatus.IN_PROGRESS, admin, now.minusDays(6));
    }

    private User createUserIfMissing(String username, String password) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            return userRepository.save(user);
        });
    }

    private void createSolicitation(String title, String description, SolicitationCategory category,
                                    SolicitationStatus status, User user, LocalDateTime createdAt) {
        Solicitation solicitation = new Solicitation();
        solicitation.setTitle(title);
        solicitation.setDescription(description);
        solicitation.setCategory(category);
        solicitation.setStatus(status);
        solicitation.setUser(user);
        solicitation.setCreatedAt(createdAt);

        solicitationRepository.save(solicitation);
    }
}
