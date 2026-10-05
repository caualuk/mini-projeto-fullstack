package br.com.bitsolucoes.portal_solicitacoes.config;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvConfig {

    public static void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory("../")
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}
