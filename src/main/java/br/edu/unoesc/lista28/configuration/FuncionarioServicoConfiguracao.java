package br.edu.unoesc.lista28.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FuncionarioServicoConfiguracao {
	@Bean(name = "aplicacao")
	public String applicationName() {
		return "Sistema de Controle de Funcionários";
	}
}