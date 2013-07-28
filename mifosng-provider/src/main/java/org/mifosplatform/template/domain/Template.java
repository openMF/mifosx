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
    @CollectionTable(name="m_template_mappers", 
    				 joinColumns=
    				 @JoinColumn(name="mapper_id"))
    private Map<String, String> mappers;
    
    public Template(String name, String text, Map<String, String> mappers) {
    	
    	this.name = name;
    	this.text = text;
    	this.mappers = mappers;
    }
    
    protected Template() {
    }
    
    public static Template fromJson(final JsonCommand command) {
    	
    	String name = command.stringValueOfParameterNamed("name");
    	String text = command.stringValueOfParameterNamed("text");
    	
    	String mappers = command.jsonFragment("mappers");    	
    	Map<String, String> mappersMap = command.mapValueOfParameterNamed(mappers);
    	
    	return new Template(name, text, mappersMap); 
    }

	public Map<String, String> getMappers() {
		return this.mappers;
	}

	public void setMappers(Map<String, String> mappers) {
		this.mappers = mappers;
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
