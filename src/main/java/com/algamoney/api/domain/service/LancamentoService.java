package com.algamoney.api.domain.service;


import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algamoney.api.domain.exceptions.CategoriaNaoEncontradaException;
import com.algamoney.api.domain.exceptions.EntidadeComDadosErradosException;
import com.algamoney.api.domain.exceptions.LancamentoNaoEncontradoException;
import com.algamoney.api.domain.exceptions.PessoaNaoEncontradaException;
import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.model.Pessoa;
import com.algamoney.api.event.RecursoCriadoEvent;
import com.algamoney.api.repository.CategoriaRepository;
import com.algamoney.api.repository.LancamentoRepository;
import com.algamoney.api.repository.PessoaRepository;

@Service
public class LancamentoService {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired 
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	public Lancamento buscar(Long codigo) {
		return lancamentoRepository.findById(codigo).orElseThrow(()->new LancamentoNaoEncontradoException(codigo));
	}
	
	@Transactional
	public Lancamento adicionar(Lancamento lancamento, HttpServletResponse response) {
		Long catCodigo = lancamento.getCategoria().getCodigo();
		Long pesCodigo = lancamento.getPessoa().getCodigo();
		
		
	
		categoriaRepository.findById(catCodigo).orElseThrow(()->new EntidadeComDadosErradosException("A 'categoria.codigo' "
				+ "com valor '"+ catCodigo+"' não foi encontrada."));
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(pesCodigo);
		if(pessoa.isEmpty()) {
			throw new EntidadeComDadosErradosException(String.format(" 'pessoa.codigo' com"
					+ " valor '%d' não foi encontrada.", pesCodigo));
		}else if(pessoa.get().getAtivo() == false) {
			throw new EntidadeComDadosErradosException(String.format(" 'pessoa.codigo' com"
					+ " valor '%d' está inativa.", pesCodigo));
		}
		
		lancamentoRepository.save(lancamento);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamento.getCodigo()));
		
		return lancamento;
		
	}
	
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Long codigo = lancamento.getCodigo();
		Long catCodigo = lancamento.getCategoria().getCodigo();
		Long peCodigo = lancamento.getPessoa().getCodigo();
		
		Lancamento lancamentoAtual = lancamentoRepository.findById(codigo).orElseThrow(()-> new LancamentoNaoEncontradoException(codigo));
		categoriaRepository.findById(catCodigo).orElseThrow(()-> new CategoriaNaoEncontradaException(catCodigo));
		pessoaRepository.findById(peCodigo).orElseThrow(()-> new PessoaNaoEncontradaException(peCodigo));
		
		BeanUtils.copyProperties(lancamento, lancamentoAtual, "codigo");
		lancamentoRepository.save(lancamentoAtual);
		
		 return lancamentoAtual;
	}
	
	@Transactional
	public void remover(Long codigo) {
		lancamentoRepository.findById(codigo).orElseThrow(()->new LancamentoNaoEncontradoException(codigo));
		lancamentoRepository.deleteById(codigo);
	}
}
