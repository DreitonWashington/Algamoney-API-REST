package com.algamoney.api.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.algamoney.api.domain.model.TipoLancamento;

public interface LancamentoMinProjection {
	Long getCodigo();
	String getDescricao();
	LocalDate getDataVencimento();
	LocalDate getDataPagamento();
	BigDecimal getValor();
	TipoLancamento getTipo();
	String getCategoriaNome();
	String getPessoaNome();
}
