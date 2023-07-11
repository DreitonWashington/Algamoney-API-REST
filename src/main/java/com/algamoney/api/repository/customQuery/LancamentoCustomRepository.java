package com.algamoney.api.repository.customQuery;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.algamoney.api.domain.model.Lancamento;


@Repository
public class LancamentoCustomRepository {
	
	
	private final EntityManager manager;
	
	public LancamentoCustomRepository(EntityManager manager) {
		this.manager = manager;
	}
	
	@SuppressWarnings("unchecked")
	public List<Lancamento> find(String descricao, String dataVen, String dataPag){
		

		
		String query = "select * from lancamento";
		String condicao = " where ";
		
		if(descricao != null) {
			query += condicao + "descricao = :descricao";
			condicao = " and ";
		}
		
		if(dataVen != null && dataPag == null) {
			query += condicao + " data_vencimento = :dataVen";
			condicao = " and ";
		}
		
		if(dataVen != null && dataPag != null) {
			query += condicao + " data_vencimento >= :dataVen";
		}
		
		if(dataVen != null && dataPag != null) {
			query += " and data_pagamento <= :dataPag";
		}
		
		if(dataVen == null && dataPag != null) {
			query += condicao + " data_pagamento <= :dataPag";

		}
		
		var q = manager.createNativeQuery(query, Lancamento.class);


		if(descricao != null) {
			q.setParameter("descricao", descricao);
		}
		
		if(dataVen != null) {
			q.setParameter("dataVen", dataVen);
		}
		
		if(dataPag != null) {
			q.setParameter("dataPag", dataPag);
		}
		
		
		return q.getResultList();
	}
	
	
}
