package com.algamoney.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algamoney.api.domain.model.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long>{

}