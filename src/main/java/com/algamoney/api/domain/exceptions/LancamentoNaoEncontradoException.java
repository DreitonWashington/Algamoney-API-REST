package com.algamoney.api.domain.exceptions;

import javax.persistence.EntityNotFoundException;

public class LancamentoNaoEncontradoException extends EntityNotFoundException{
	private static final long serialVersionUID = 1L;
	
	public LancamentoNaoEncontradoException(String mensagem) {
		super(mensagem);
	}
	
	public LancamentoNaoEncontradoException(Long codigo) {
		this(String.format("Lancamento de código %d não existe.", codigo));
	}

}
