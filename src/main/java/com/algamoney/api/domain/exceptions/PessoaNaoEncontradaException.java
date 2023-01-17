package com.algamoney.api.domain.exceptions;

import javax.persistence.EntityNotFoundException;

public class PessoaNaoEncontradaException extends EntityNotFoundException{
	private static final long serialVersionUID = 1L;
	
	public PessoaNaoEncontradaException(String mensagem) {
		super(mensagem);
	}
	
	public PessoaNaoEncontradaException(Long codigo) {
		this(String.format("Pessoa de código %s não existe.", codigo));
	}
	
}
