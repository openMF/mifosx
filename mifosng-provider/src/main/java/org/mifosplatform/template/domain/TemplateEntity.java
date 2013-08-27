package org.mifosplatform.template.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(using = TemplateEntitySerializer.class)
public enum TemplateEntity{
	
	CLIENT(0,"Client", "clients/{{clientId}}?tenantIdentifier=default"),
	LOAN(1, "Loan", "loans/{{loanId}}?tenantIdentifier=default");

	private int id;
	private String name;
	private String url;
	
	private TemplateEntity(int id, String name, String url) {
		this.id = id;
		this.name = name;
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
