package org.mifosplatform.template.domain;

import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_template")
public class Template extends AbstractPersistable<Long>{

	@Column(name = "name", nullable = false)
    private String name;

    @Column(name = "text", columnDefinition = "longtext", nullable = false)
    private String text;
    
    @ElementCollection(targetClass=String.class, fetch = FetchType.EAGER)
    @MapKeyColumn(name="resource")
    @Column(name="address")
    @CollectionTable(name="m_template_resource_address_mapping", 
    				 joinColumns=
    				 @JoinColumn(name="metadata_id"))
    private Map<String, String> metadata;
    
    public Template(String name, String text, Map<String, String> metadata) {
    	this.name = name;
    	this.text = text;
    	this.metadata = metadata;
    }
    
    protected Template() {
    }
    
    public static Template fromJson(final JsonCommand command) {
    	String name = command.stringValueOfParameterNamed("name");
    	String text = command.stringValueOfParameterNamed("text");
    	
    	String metadata = command.jsonFragment("metadata");    	
    	Map<String, String> metadataMap = command.mapValueOfParameterNamed(metadata);
    	
    	return new Template(name, text, metadataMap); 
    }

	public Map<String, String> getMetadata() {
		return this.metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
