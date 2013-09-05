package org.mifosplatform.template.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(using = TemplateTypeSerializer.class)
public enum TemplateType {

	DOCUMENT(0,"Document"),
	EMAIL(1, "E-Mail"),
	SMS(2, "SMS");

	private int id;
	private String name;
	
	private TemplateType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
