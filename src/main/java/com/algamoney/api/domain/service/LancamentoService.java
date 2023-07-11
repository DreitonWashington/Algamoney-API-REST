package com.algamoney.api.domain.service;


import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algamoney.api.domain.exceptions.CategoriaNaoEncontradaException;
import com.algamoney.api.domain.exceptions.EntidadeComDadosErradosException;
import com.algamoney.api.domain.exceptions.LancamentoNaoEncontradoException;
import com.algamoney.api.domain.exceptions.PessoaNaoEncontradaException;
import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.model.Pessoa;
import com.algamoney.api.domain.model.Usuario;
import com.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.algamoney.api.event.RecursoCriadoEvent;
import com.algamoney.api.mail.Mailer;
import com.algamoney.api.repository.CategoriaRepository;
import com.algamoney.api.repository.LancamentoRepository;
import com.algamoney.api.repository.PessoaRepository;
import com.algamoney.api.repository.UsuarioRepository;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService {
	
	private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired 
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";
	
	@Autowired
	private Mailer mailer;
	
	@Scheduled(cron = "0 0 6 * * *")
	//@Scheduled(fixedDelay = 1000*60*30)
	public void avisarLancamentosVencidos() {
		if(logger.isDebugEnabled()) {
			logger.debug("Preparando envio de e-mail de aviso de laçamentos vencidos.");			
		}
		List<Lancamento> vencidos = lancamentoRepository
				.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		
		if(vencidos.isEmpty()) {
			logger.info("Sem lançamentos vencidos para aviso.");
			return;
		}
		
		logger.info("Existem {} lançamentos vencidos.", vencidos.size());
		
		List<Usuario> destinatarios = usuarioRepository.findByPermissoesDescricao(DESTINATARIOS);
		
		if(destinatarios.isEmpty()) {
			logger.warn("Existem lançamentos vencidos, mas o sistema não encontrou destinatários");
			return;
		}
		
		mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);
		
		logger.info("Envio de E-mails de aviso concluído");
	}
	
	public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws JRException {
		List<LancamentoEstatisticaPessoa> dados = lancamentoRepository.porPessoa(inicio, fim);
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("DT_INICIO", Date.valueOf(inicio));
		parametros.put("DT_FIM", Date.valueOf(fim));
		parametros.put("REPORT_LOCALE", new Locale("pt","BR"));
		
		InputStream inputStream = this.getClass().getResourceAsStream("/relatorios/Lancamentos-por-Pessoa.jasper");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros,
				new JRBeanCollectionDataSource(dados));
		
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}
	
	public Lancamento buscar(Long codigo) {
		return lancamentoRepository.findById(codigo).orElseThrow(()->new LancamentoNaoEncontradoException(codigo));
	}
	
	@Transactional
	public Lancamento adicionar(Lancamento lancamento, HttpServletResponse response) {
		Long catCodigo = lancamento.getCategoria().getCodigo();
		Long pesCodigo = lancamento.getPessoa().getCodigo();
		
		
	
		categoriaRepository.findById(catCodigo).orElseThrow(()->new EntidadeComDadosErradosException("A 'categoria.codigo' "
				+ "com valor '"+ catCodigo+"' não foi encontrada."));
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(pesCodigo);
		if(pessoa.isEmpty()) {
			throw new EntidadeComDadosErradosException(String.format(" 'pessoa.codigo' com"
					+ " valor '%d' não foi encontrada.", pesCodigo));
		}else if(pessoa.get().getAtivo() == false) {
			throw new EntidadeComDadosErradosException(String.format(" 'pessoa.codigo' com"
					+ " valor '%d' está inativa.", pesCodigo));
		}
		
		lancamentoRepository.save(lancamento);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamento.getCodigo()));
		
		return lancamento;
		
	}
	
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Long codigo = lancamento.getCodigo();
		Long catCodigo = lancamento.getCategoria().getCodigo();
		Long peCodigo = lancamento.getPessoa().getCodigo();
		
		Lancamento lancamentoAtual = lancamentoRepository.findById(codigo).orElseThrow(()-> new LancamentoNaoEncontradoException(codigo));
		categoriaRepository.findById(catCodigo).orElseThrow(()-> new CategoriaNaoEncontradaException(catCodigo));
		pessoaRepository.findById(peCodigo).orElseThrow(()-> new PessoaNaoEncontradaException(peCodigo));
		
		BeanUtils.copyProperties(lancamento, lancamentoAtual, "codigo");
		lancamentoRepository.save(lancamentoAtual);
		
		 return lancamentoAtual;
	}
	
	@Transactional
	public void remover(Long codigo) {
		lancamentoRepository.findById(codigo).orElseThrow(()->new LancamentoNaoEncontradoException(codigo));
		lancamentoRepository.deleteById(codigo);
	}
}
