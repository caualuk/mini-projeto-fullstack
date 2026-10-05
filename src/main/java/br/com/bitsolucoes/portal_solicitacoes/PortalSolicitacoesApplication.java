package br.com.bitsolucoes.portal_solicitacoes;

import br.com.bitsolucoes.portal_solicitacoes.config.DotenvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PortalSolicitacoesApplication {

	public static void main(String[] args) {
		DotenvConfig.loadEnv();

		SpringApplication.run(PortalSolicitacoesApplication.class, args);
	}

}
