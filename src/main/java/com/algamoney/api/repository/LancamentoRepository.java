package com.algamoney.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.repository.lancamento.LancamentoRepositoryQuery;
import com.algamoney.api.repository.projection.LancamentoMinProjection;

@Repository
public interface LancamentoRepository extends JpaRepositoryImplementation<Lancamento, Long>, LancamentoRepositoryQuery{
	
	@Query(nativeQuery = true, value = "select lancamento.codigo,lancamento.descricao,"
			+ "lancamento.data_vencimento as dataVencimento,lancamento.data_pagamento as dataPagamento,\r\n"
			+ "lancamento.valor,lancamento.tipo,categoria.nome as categoriaNome,pessoa.nome as pessoaNome from lancamento inner join categoria\r\n"
			+ "on lancamento.codigo_categoria = categoria.codigo inner join pessoa on lancamento.codigo_pessoa = pessoa.codigo"
			, countQuery = "select count(*) from lancamento")
	public Page<LancamentoMinProjection> resumir(Pageable pageable);
}