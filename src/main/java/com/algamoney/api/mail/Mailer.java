package com.algamoney.api.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.model.Usuario;

@Component
public class Mailer {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
//	@Autowired
//	private LancamentoRepository repo;
	
	public void avisarSobreLancamentosVencidos(List<Lancamento> vencidos, List<Usuario> destinatarios) {
		Map<String, Object> variaveis = new HashMap<>();
		variaveis.put("lancamentos", vencidos);
		
		List<String> emails = destinatarios.stream().map(u -> u.getEmail()).collect(Collectors.toList());
		
		this.enviarEmail("coralTestes@outlook.com", emails, "Lançamentos Vencidos", "mail/aviso-lancamentos-vencidos", variaveis);
	}
	
	public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String template, 
			Map<String, Object> variaveis) {
		
		Context context = new Context(new Locale("pt","BR"));
		
		variaveis.entrySet().forEach(e -> context.setVariable(e.getKey(), e.getValue()));
		
		String mensagem = thymeleaf.process(template, context);
		this.enviarEmail(remetente, destinatarios, assunto, mensagem);
	}
	
	public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String mensagem) {
		
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setFrom(remetente);
			helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
			helper.setSubject(assunto);
			helper.setText(mensagem, true);
			
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new RuntimeException("Probemas com envio de email", e);
		}
	}
	
	/*@EventListener
	private void teste(ApplicationReadyEvent event) {
		this.enviarEmail("coralTestes@outlook.com", Arrays.asList("dreiton.washington@outlook.com"),
				"Testando", "Olá!<br/>Teste ok!");
		System.out.println("Terminado o envio de email");
	}*/
	
//	@EventListener
//	private void teste(ApplicationReadyEvent event) {
//		String template = "mail/aviso-lancamentos-vencidos";
//		List<Lancamento> lista = repo.findAll();
//		Map<String, Object> variaveis = new HashMap<>();
//		variaveis.put("lancamentos", lista);
//		this.enviarEmail("coralTestes@outlook.com", Arrays.asList("dreiton.washington@outlook.com"),
//				"Testando", template,variaveis);
//		System.out.println("Terminado o envio de email");
//	}
	
//	@Autowired
//	private LancamentoRepository lancamentoRepository;
//	
//	@Autowired
//	private UsuarioRepository usuarioRepository;
//	
//	@EventListener
//	private void teste(ApplicationReadyEvent event) {
//		System.out.println("ENTROU");
//		List<Lancamento> lista = repo.findAll();
//		Map<String, Object> variaveis = new HashMap<>();
//		variaveis.put("lancamentos", lista);
//		
//		List<Lancamento> vencidos = lancamentoRepository
//				.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
////		for(Lancamento x: vencidos) {
////			System.out.println(x.getPessoa().getNome());
////		}
//		List<Usuario> destinatarios = usuarioRepository.findByPermissoesDescricao("ROLE_PESQUISAR_LANCAMENTO");
////		for(Usuario x: destinatarios) {
////			System.out.println(x.getEmail());
////		}
//		this.avisarSobreLancamentosVencidos(vencidos, destinatarios);
//		System.out.println("Terminado o envio de email");
//	}
}
