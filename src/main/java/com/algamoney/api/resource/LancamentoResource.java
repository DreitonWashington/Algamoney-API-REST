package com.algamoney.api.resource;


import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.service.LancamentoService;
import com.algamoney.api.dto.LancamentoEstatisticaCategoria;
import com.algamoney.api.dto.LancamentoEstatisticaDia;
import com.algamoney.api.repository.LancamentoRepository;
import com.algamoney.api.repository.customQuery.LancamentoCustomRepository;
import com.algamoney.api.repository.filter.LancamentoFilter;
import com.algamoney.api.repository.lancamento.LancamentoRepositoryImpl;
import com.algamoney.api.repository.projection.LancamentoMinDTO;

import net.sf.jasperreports.engine.JRException;


@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoRepositoryImpl lancRepImpl;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private LancamentoCustomRepository lancamentoCustomRepository;
	
	@PostMapping("/anexo")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO')")
	public String uploadAnexo(@RequestParam MultipartFile anexo) throws IOException	{
		FileOutputStream out = new FileOutputStream("C:/Users/dreit/OneDrive/Área de Trabalho/anexo--" + anexo.getOriginalFilename());
		out.write(anexo.getBytes());
		out.close();
		return "ok";
	}
	
	@GetMapping("/relatorios/por-pessoa")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public ResponseEntity<byte[]> relatorioPorPessoa(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate inicio, 
			@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate fim) throws JRException{
		
		byte[] relatorio = lancamentoService.relatorioPorPessoa(inicio, fim);
		
		 HttpHeaders headers = new HttpHeaders();
		 headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
		 headers.add("Content-Disposition", "attachment; filename=relatorio_por_pessoa.pdf");
		
		 return ResponseEntity.ok().headers(headers)
				.body(relatorio);
	}
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public Page<Lancamento> listar(Pageable pageable){
		return lancamentoRepository.findAll(pageable);
	}
	
	@GetMapping("/estatistica/por-categoria")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public List<LancamentoEstatisticaCategoria> porCategoria(@RequestParam(name = "date", required = true) String date){
		
		LocalDate dateF = LocalDate.parse(date);
		return this.lancamentoRepository.porCategoria(dateF);//LocalDate.now());
	}
	
	@GetMapping("/estatistica/por-dia")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public List<LancamentoEstatisticaDia> porDia(@RequestParam(name = "date", required = true) String date){
		
		LocalDate dateF = LocalDate.parse(date);
		return this.lancamentoRepository.porDia(dateF);//LocalDate.now());
	}
	
	//@GetMapping(params = "resumo")
	/*@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public Page<LancamentoMinDTO> resumir(LancamentoFilter lancamentoFilter,Pageable pageable){
		
		Page<LancamentoMinProjection> listLanMinProj = lancamentoRepository.resumir(pageable);
		Page<LancamentoMinDTO> result;
		
		result = listLanMinProj.map(x -> new LancamentoMinDTO(x));
		
		return result;
	}*/
	
	/*@GetMapping("/pesquisa")
	 * @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public Page<Lancamento> pesquisa(LancamentoFilter lancamentoFilter, Pageable pageable){
		return lancamentoRepository.filtrar(lancamentoFilter, pageable);
	}*/
	
	@GetMapping(params = "resumo")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public Page<LancamentoMinDTO> pesquisaComFiltros(LancamentoFilter lancamentoFilter, Pageable pageable){
		return lancRepImpl.resumo(lancamentoFilter, pageable);
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
	
	
	@GetMapping(path = "/pesquisar")
	public List<?> pesquisar(@RequestParam(value="descricao", required=false) String descricao, 
			@RequestParam(value="dataVencimento", required=false) String dataVen, 
			@RequestParam(value="dataPagamento", required=false) String dataPag,
			@RequestParam(value="resumo", required=false) Boolean res){
		
		List<Lancamento>lista = lancamentoCustomRepository.find(descricao, dataVen, dataPag);
		
		if(res == null || res == true) {
			List<LancamentoMinDTO> listaDto = new ArrayList<>();
			for(Lancamento x : lista) {
				listaDto.add(new LancamentoMinDTO(x.getCodigo(),x.getDescricao(),x.getDataVencimento(),
						x.getDataPagamento(),x.getValor(),x.getTipo(),x.getCategoria().getNome(),
						x.getPessoa().getNome()));
			}
			return listaDto;
		}
		
		return lista;
	}
	
}

