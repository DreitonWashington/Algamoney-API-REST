package com.algamoney.api.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import com.algamoney.api.domain.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepositoryImplementation<Categoria, Long>{
	
	/*@Query(nativeQuery = true, value = "select * from categoria")
	Page<Categoria>	getCCAT(Pageable pageable);
	
	TESTE DE QUERY COM SQL NATIVO ==> FUNCIONANDO
	*/
	
}
