package com.algamoney.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.algamoney.api.domain.model.Cidade;

public interface CidadeRepository extends JpaRepository<Cidade, Long>{
	
	Page<Cidade> findByEstadoCodigo(Pageable pageable, Long estadoCodigo);
}
