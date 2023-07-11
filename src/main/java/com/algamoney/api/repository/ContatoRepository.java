package com.algamoney.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algamoney.api.domain.model.Contato;

public interface ContatoRepository extends JpaRepository<Contato, Long>{

}
