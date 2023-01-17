package com.algamoney.api.resource;


import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.service.LancamentoService;
import com.algamoney.api.repository.LancamentoRepository;
import com.algamoney.api.repository.filter.LancamentoFilter;
import com.algamoney.api.repository.projection.LancamentoMinDTO;
import com.algamoney.api.repository.projection.LancamentoMinProjection;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public Page<Lancamento> listar(Pageable pageable){
		return lancamentoRepository.findAll(pageable);
	}
	
	@GetMapping(params = "resumo")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public Page<LancamentoMinDTO> resumir(Pageable pageable){
		
		Page<LancamentoMinProjection> listLanMinProj = lancamentoRepository.resumir(pageable);
		Page<LancamentoMinDTO> result;
		
		result = listLanMinProj.map(x -> new LancamentoMinDTO(x));
		
		return result;
	}
	
	@GetMapping("/pesquisa/")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public Page<Lancamento> pesquisa(LancamentoFilter lancamentoFilter, Pageable pageable){
		return lancamentoRepository.filtrar(lancamentoFilter, pageable);
	}
	
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public Lancamento buscaPorCodigo(@PathVariable Long codigo) {
		return lancamentoService.buscar(codigo);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO')")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Lancamento adicionar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
		return lancamentoService.adicionar(lancamento, response);
	}
	
	@PutMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO')")
	public Lancamento atualizar(@PathVariable Long codigo, @RequestBody Lancamento lancamento) {
		lancamento.setCodigo(codigo);
		return lancamentoService.atualizar(lancamento);
	}
	
	
	@DeleteMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO')")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deletar(@PathVariable Long codigo) {
		lancamentoService.remover(codigo);
	}
	
}

