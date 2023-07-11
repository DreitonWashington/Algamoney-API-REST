package com.algamoney.api.exceptionhandler;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.algamoney.api.domain.exceptions.EntidadeComDadosErradosException;
import com.algamoney.api.domain.exceptions.EntidadeEmUsoException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@ControllerAdvice
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler{
	
	@Autowired
	private MessageSource messageSource;
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, 
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		Throwable rootCause = ex.getRootCause();
		if(rootCause instanceof UnrecognizedPropertyException) {
			return handleUnrecognizedPropertyException((UnrecognizedPropertyException)rootCause, headers, status, request);
		}
	
		
		ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENCIVEL;
		String detail = String.format("O corpo da requisição está inválido");
		Problem problem = buildProblem(status,problemType, detail);

		return handleExceptionInternal(ex, problem, headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		//List<FieldError> fieldErros = ex.getBindingResult().getFieldErrors();
		ProblemType problemType = ProblemType.CAMPOS_COM_VALORES_ERRADOS;
		String detail = String.format("Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.");
		Problem problem = buildProblem(status, problemType, detail);
		
		BindingResult bindingResult = ex.getBindingResult();
		List<Field> fieldErros = bindingResult.getFieldErrors().stream().map(fieldE->{
			String messagem = messageSource.getMessage(fieldE, LocaleContextHolder.getLocale());
			return new Field(fieldE.getField(),messagem);
		}).collect(Collectors.toList());
		
		problem.setFields(fieldErros);
		
		/*for(FieldError field : fieldErros) {
			Field fieldClass = new Field();
			fieldClass.setName(field.getField());
			fieldClass.setUserMessage(String.format("O campo '%s' não pode ter o valor '%s'. %s."
					,fieldClass.getName(),field.getRejectedValue(),
					messageSource.getMessage(field, LocaleContextHolder.getLocale())));
			problem.addField(fieldClass);
		}*/
		
		return handleExceptionInternal(ex, problem, headers, status, request);
	}
	
	@ExceptionHandler(UnrecognizedPropertyException.class)
	protected ResponseEntity<Object> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request){
		
		String path = ex.getPath().stream().map(ref-> ref.getFieldName()).collect(Collectors.joining("."));
		
		ProblemType problemType = ProblemType.PROPRIEDADES_DESCONHECIDAS;
		String detail = String.format("A propriedade '%s' não existe.", path);
		Problem problem = buildProblem(status, problemType, detail);
		
		return handleExceptionInternal(ex, problem, headers, status, request);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request){
		
		ProblemType problemType = ProblemType.ENTIDADE_NAO_ENCONTRADA;
		HttpStatus status = HttpStatus.NOT_FOUND; 
		String detail = ex.getMessage();
		Problem problem = buildProblem(status, problemType, detail);
		
		return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}
	
	@ExceptionHandler(EntidadeEmUsoException.class)
	public ResponseEntity<Object> handleEntidadeEmUsoException(EntidadeEmUsoException ex, WebRequest request){
		
		ProblemType problemType = ProblemType.RECURSO_EM_USO;
		HttpStatus status = HttpStatus.CONFLICT;
		String detail = ex.getMessage();
		Problem problem = buildProblem(status, problemType, detail);
		
		return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
			WebRequest request){
		
		
		ProblemType problemType = ProblemType.RECURSO_EM_USO;
		HttpStatus status = HttpStatus.BAD_REQUEST;
		String detail = ExceptionUtils.getRootCauseMessage(ex);
		Problem problem = buildProblem(status, problemType, detail);
		
		return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}
	
	@ExceptionHandler(EntidadeComDadosErradosException.class)
	public ResponseEntity<Object> handleEntidadeComDadosErradosException(EntidadeComDadosErradosException ex, WebRequest request){
		
		ProblemType problemType = ProblemType.CAMPOS_COM_VALORES_ERRADOS;
		HttpStatus status = HttpStatus.BAD_REQUEST;
		String detail = ex.getMessage();
		Problem problem = buildProblem(status, problemType, detail);
		return handleExceptionInternal(ex, problem, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request){
		
		ProblemType problemType = ProblemType.CAMPOS_COM_VALORES_ERRADOS;
		HttpStatus status = HttpStatus.BAD_REQUEST;
		String mensagem = ex.getMessage();
		System.out.println(ex.getMessage());
		
		String[] msgSplit = mensagem.split(" ");
	
		String detail = String.format("O campo '%s' não deve ser '%s' .", msgSplit[2], msgSplit[6]);
		Problem problem = buildProblem(status, problemType, detail);
		return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}
	
	public Problem buildProblem(HttpStatus status,ProblemType problemType, String detail) {
		Problem problem = new Problem();
		problem.setStatus(status.value());
		problem.setTimestamp(LocalDateTime.now());
		problem.setType(problemType.getUri());
		problem.setTitle(problemType.getTitle());
		problem.setDetail(detail);
		return problem;
	}
}

