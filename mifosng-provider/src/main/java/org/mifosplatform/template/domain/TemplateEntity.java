package org.mifosplatform.template.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(using = TemplateEntitySerializer.class)
public enum TemplateEntity{
	
	CLIENT(0,"client"),
	LOAN(1, "loan");

	private int id;
	private String name;
	
	private TemplateEntity(int id, String name) {
		this.id = id;
		this.name = name;
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
