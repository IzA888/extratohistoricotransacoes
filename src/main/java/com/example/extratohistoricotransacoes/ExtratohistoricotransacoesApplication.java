package com.example.extratohistoricotransacoes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.extratohistoricotransacoes.commands.repository")
public class ExtratohistoricotransacoesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExtratohistoricotransacoesApplication.class, args);
	}

}
