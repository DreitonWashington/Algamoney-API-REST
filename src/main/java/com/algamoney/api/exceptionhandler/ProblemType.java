package com.algamoney.api.exceptionhandler;


public enum ProblemType {
	
	MENSAGEM_INCOMPREENCIVEL("/mensagem-incompreencivel", "Mensagem incompreencivel."),
	ENTIDADE_NAO_ENCONTRADA("/entidade-nao-encontrada", "Entidade não encontrada."),
	RECURSO_EM_USO("/recurso-em-uso", "O recurso está sendo usado."),
	CAMPOS_COM_VALORES_ERRADOS("/valores-errados","Alguns campos com valores errados."),
	//ENTIDADE_COM_DADOS_ERRADOS("/",""),
	PROPRIEDADES_DESCONHECIDAS("/propriedades-desconhecidas", "Propriedade informada desconhecida.");
	
	private String uri;
	private String title;
	
	ProblemType(String uriPath, String title) {
		this.uri = "https://algamoney" + uriPath;
		this.title = title;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
