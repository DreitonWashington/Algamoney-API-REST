package com.algamoney.api.domain.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algamoney.api.domain.exceptions.CategoriaNaoEncontradaException;
import com.algamoney.api.domain.exceptions.EntidadeEmUsoException;
import com.algamoney.api.domain.model.Categoria;
import com.algamoney.api.repository.CategoriaRepository;

@Service
public class CategoriaService {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	
	public Categoria buscar(Long codigo) {
		return categoriaRepository.findById(codigo).orElseThrow(()->new CategoriaNaoEncontradaException(codigo));
	}
	
	@Transactional
	public Categoria atualizar(Categoria categoria) {
		Long codigo = categoria.getCodigo();
		Categoria catAtual = categoriaRepository.findById(codigo).orElseThrow(()->new CategoriaNaoEncontradaException(codigo));
		BeanUtils.copyProperties(categoria, catAtual, "codigo");
		categoriaRepository.save(catAtual);
		return catAtual;
	}
	
	@Transactional
	public void deletar(Long codigo) {
		try {
			categoriaRepository.deleteById(codigo);
		}catch(EmptyResultDataAccessException e) {
			throw new CategoriaNaoEncontradaException(codigo);
		}catch(DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(String.format("Categoria de código %d em uso.", codigo));
		}
	}

	
}
