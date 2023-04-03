package br.edu.unoesc.lista28.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.edu.unoesc.lista28.dto.FuncionarioDTO;
import br.edu.unoesc.lista28.model.Funcionario;
import br.edu.unoesc.lista28.service.FuncionarioService;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionaryRestController {
	
	@Autowired
	private FuncionarioService servico;
	
	@GetMapping
	public ResponseEntity<Iterable<Funcionario>> listar() {
		List<Funcionario> funcionarios = servico.listar();
		
		if (funcionarios.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(funcionarios);
	}

	@GetMapping("/listar-paginas")
	public ResponseEntity<Page<FuncionarioDTO>> listarPaginado(Pageable pagina) {
		return ResponseEntity.ok(servico.listarPaginado(pagina));
	}
	
	@GetMapping("/paginas")
	public ResponseEntity<Page<Funcionario>> listarPaginas(
			@RequestParam(value="nome", defaultValue = "")String nome,
			@RequestParam(value="pagina", defaultValue = "0")Integer pagina,
			@RequestParam(value="tamPagina", defaultValue = "24")Integer tamPagina,
			@RequestParam(value="ordenacao", defaultValue = "nome")String campo,
			@RequestParam(value="direcao", defaultValue = "ASC")String direcao){
		return ResponseEntity.ok(servico.buscaPaginadaPorNome(nome, pagina, tamPagina, campo, direcao));
	}
	
	@RequestMapping(value = "/{id}",
				    method = RequestMethod.GET,
				    produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Funcionario> porId(@PathVariable Long id) {
		Optional<Funcionario> funcionario = servico.porId(id);
		
		if (funcionario.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(funcionario.get());
	}
	
//	headers = "Accept=application/xml"
	@RequestMapping(value = "/xml/{id}", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<Funcionario> porIdXML(@PathVariable Long id) {
		Funcionario funcionario = servico.buscar(id);
		
		return ResponseEntity.ok(funcionario);
	}
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Funcionario>> porNome(@RequestParam("nome") String nome) {
		List<Funcionario> funcionarios = servico.buscarPorNome(nome);
		
		if (funcionarios.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(funcionarios); 
	}
	
	@GetMapping(value = "/faixa-salarial")
	public ResponseEntity<List<Funcionario>> porFaixaSalarial(
			@RequestParam("minimo") Optional<BigDecimal> minimo,
			@RequestParam("maximo") Optional<BigDecimal> maximo) {
		
		List<Funcionario> funcionarios = servico.buscarPorFaixaSalarial(
													minimo.orElse(BigDecimal.ZERO),
													maximo.orElse(new BigDecimal("9999999999")));
		
		if (funcionarios.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(funcionarios); 
	}
	
	@GetMapping(value = "/dependentes")
	public ResponseEntity<List<Funcionario>> possuiDependentes() {
		List<Funcionario> funcionarios = servico.buscarPossuiDependentes();
		
		if (funcionarios.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(funcionarios); 
	}
	
    @PostMapping()
    //@ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> incluir(@RequestBody Funcionario funcionario) {
    	funcionario = servico.incluir(funcionario);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
        									 .path("/{id}")
        									 .buildAndExpand(funcionario.getId())
        									 .toUri();
        
        System.out.println(uri.toString());
        
        return ResponseEntity.created(uri).build();
    }
     
    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizar(@PathVariable("id") Long id, @RequestBody Funcionario funcionario) {
    	if (servico.porId(id).isEmpty()) {
    		return ResponseEntity.notFound().build();
    	}
    	
        return ResponseEntity.ok(servico.alterar(id, funcionario));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
    	try {
    		servico.excluir(id);   		
    	} catch (ObjectNotFoundException e) {
    		return ResponseEntity.notFound().build();
    	}
    	
    	return ResponseEntity.noContent().build();
    }
}