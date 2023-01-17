package com.algamoney.api.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import com.algamoney.api.domain.model.Pessoa;

@Repository
public interface PessoaRepository extends JpaRepositoryImplementation<Pessoa, Long>{
	
	public Page<Pessoa> findByNomeContaining(String nome, Pageable pageable);
}
