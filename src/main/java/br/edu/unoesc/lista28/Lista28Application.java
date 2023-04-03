package br.edu.unoesc.lista28;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;

import br.edu.unoesc.lista28.Lista28Application;
import br.edu.unoesc.lista28.model.Funcionario;
import br.edu.unoesc.lista28.service.FuncionarioService;

@SpringBootApplication
public class Lista28Application {

	@Autowired
	@Qualifier("aplicacao")
	private String nomeAplicacao;
	
	@Value("${mensagem}")
	private String mensagem;
	
	@Value("${ambiente}")
	private String ambiente;
	
	public static void main(String[] args) {
		SpringApplication.run(Lista28Application.class, args);
	}
	

	@Bean
	CommandLineRunner commandLineRunner(FuncionarioService servico) {
		return args -> {
		
			servico.popularTabelaInicial();

			// Exemplo de tratamento de exceções
			try {
				//System.out.println(10/0);
				servico.excluir(2L);			
			} catch (EmptyResultDataAccessException e) {
				System.out.println("\n>>> Erro! Registro não encontrado! <<<\n");
				
			} catch (RuntimeException e) {
				System.out.println("\n>>> Erro de execução! <<<\n" + e.getMessage());
				
			}
			
			// Exemplo utilização da classe Optional
			Optional<Funcionario> p = servico.porId(10L);
			if (p.isEmpty()) {
				System.out.println("\n>>> Registro não encontrado! <<<\n");
				
			} else {
				System.out.println(p);				
				System.out.println(p.get());				
				System.out.println(p.get().getNome());	
				
			}
			
			Funcionario a = servico.buscarPorId(50L);
			a.setNome("Rodrigo");
			a.setEndereco("Rua R.");
			a.setNumDep(1);
			a.setSalario(new BigDecimal("1400.00"));
			a.setNascimento(LocalDate.of(2004, 5, 03));
			
			if (a.getId() == null) {
				servico.incluir(a);
				
			} else {
				servico.alterar(a.getId(), a);
				
			}
			
			// Recupera todos os registros
			System.out.println(servico.listar());
			
			// Exemplos dos métodos de busca
			for (var funcionario: servico.buscarPorNome("ano")) {
				System.out.println(funcionario);
				
			}
			
			for (var funcionario: servico.buscarPorFaixaSalarial(new BigDecimal("500"),  new BigDecimal("1500"))) {
				System.out.println(funcionario);
				
			}
			
			for (var funcionario: servico.buscarPossuiDependentes()) {
				System.out.println(funcionario);
				
			}
		};
	}

}
