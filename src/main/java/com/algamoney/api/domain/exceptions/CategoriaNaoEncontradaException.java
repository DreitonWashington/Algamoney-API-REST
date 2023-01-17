package com.algamoney.api.domain.exceptions;

import javax.persistence.EntityNotFoundException;

public class CategoriaNaoEncontradaException extends EntityNotFoundException{
	private static final long serialVersionUID = 1L;
	
	public CategoriaNaoEncontradaException(String mensagem) {
		super(mensagem);
	}
	
	public CategoriaNaoEncontradaException(Long id) {
		this(String.format("Categoria de código %d não existe.", id));
	}

}

