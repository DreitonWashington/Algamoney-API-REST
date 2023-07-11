package com.algamoney.api.domain.service;


import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algamoney.api.domain.exceptions.PessoaNaoEncontradaException;
import com.algamoney.api.domain.model.Pessoa;
import com.algamoney.api.event.RecursoCriadoEvent;
import com.algamoney.api.repository.PessoaRepository;

@Service
public class PessoaService {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	public Pessoa buscar(Long codigo) {
		return pessoaRepository.findById(codigo).orElseThrow(()->new PessoaNaoEncontradaException(codigo));
	}
	
	@Transactional
	public Pessoa atualizar(Pessoa pessoa) {
		Long codigo = pessoa.getCodigo();
		Pessoa pessoaAtual = pessoaRepository.findById(codigo).orElseThrow(()->new PessoaNaoEncontradaException(codigo));
		
		pessoaAtual.getContatos().clear();
		pessoaAtual.getContatos().addAll(pessoa.getContatos());
		
		pessoaAtual.getContatos().forEach(c -> c.setPessoa(pessoaAtual));
		

		BeanUtils.copyProperties(pessoa, pessoaAtual, "codigo", "contatos");
		
		return pessoaRepository.save(pessoaAtual);
	}
	
	@Transactional
	public Pessoa add(Pessoa pessoa, HttpServletResponse response) {
		pessoa.getContatos().forEach(c -> c.setPessoa(pessoa));
		pessoaRepository.save(pessoa);        	
		publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoa.getCodigo()));
		
		return pessoa;
	}
	
	@Transactional
	public void deletar(Long codigo) {
		Pessoa pessoa = pessoaRepository.findById(codigo).orElseThrow(()->new PessoaNaoEncontradaException(codigo));
		try {
			pessoaRepository.delete(pessoa);			
		}catch(DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getMessage());
		}
	}
	
	@Transactional
	public void attPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pessoaAtual = pessoaRepository.findById(codigo).orElseThrow(()->new PessoaNaoEncontradaException(codigo));
		pessoaAtual.setAtivo(ativo);
		
		pessoaRepository.save(pessoaAtual);
	}
	
}
