package com.algamoney.api.domain.service;


import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
		
		BeanUtils.copyProperties(pessoa, pessoaAtual, "codigo");
		
		return pessoaRepository.save(pessoaAtual);
	}
	
	@Transactional
	public Pessoa add(Pessoa pessoa, HttpServletResponse response) {
		pessoaRepository.save(pessoa);        	
		publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoa.getCodigo()));
		
		return pessoa;
	}
	
	@Transactional
	public void deletar(Long codigo) {
		Pessoa pessoa = pessoaRepository.findById(codigo).orElseThrow(()->new PessoaNaoEncontradaException(codigo));
		pessoaRepository.delete(pessoa);
	}
	
	@Transactional
	public void attPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pessoaAtual = pessoaRepository.findById(codigo).orElseThrow(()->new PessoaNaoEncontradaException(codigo));
		pessoaAtual.setAtivo(ativo);
		
		pessoaRepository.save(pessoaAtual);
	}
	
}
