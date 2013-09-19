package org.mifosplatform.template.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Entity
@Table(name = "m_template",  uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "unq_name")})
public class Template extends AbstractPersistable<Long>{
	
	@Column(name = "name",nullable = false, unique = true)
    private String name;
	
	@Enumerated
	@JsonSerialize(using = TemplateEntitySerializer.class)
	private TemplateEntity entity;
	
	@Enumerated
	@JsonSerialize(using = TemplateTypeSerializer.class)
	private TemplateType type;

    @Column(name = "text", columnDefinition = "longtext", nullable = false)
    private String text;
    
    @OrderBy(value = "mapperorder")
    @OneToMany(targetEntity=TemplateMapper.class, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private List<TemplateMapper> mappers;
    
    public Template(String name, 
    				String text, 
    				TemplateEntity entity, 
    				TemplateType type, 
    				List<TemplateMapper> mappers) {
    	this.name = name;
    	this.entity = entity;
    	this.type = type;
    	this.text = text;
    	this.mappers = mappers;
    }
    
    protected Template() {
    }
    
    public static Template fromJson(final JsonCommand command) {
    	System.out.println("COMMAND: "+command);
    	String name = command.stringValueOfParameterNamed("name");
    	String text = command.stringValueOfParameterNamed("text");
    	TemplateEntity entity = TemplateEntity.values()[command.integerValueSansLocaleOfParameterNamed("entity")];
    	TemplateType type = TemplateType.values()[command.integerValueSansLocaleOfParameterNamed("type")];
    	JsonArray array = command.arrayOfParameterNamed("mappers");
    	List<TemplateMapper> mappersList = new ArrayList<TemplateMapper>();
    	for(JsonElement element : array) {
    		mappersList.add(new TemplateMapper(
    				element.getAsJsonObject().get("mappersorder").getAsInt(), 
    				element.getAsJsonObject().get("mapperskey").getAsString(),
    				element.getAsJsonObject().get("mappersvalue").getAsString()));
    	}

    	return new Template(name, text, entity, type, mappersList); 
    }
    
    public LinkedHashMap<String, String> getMappersAsMap() {
    	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
    	for(TemplateMapper mapper : getMappers()) {
    		map.put(mapper.getMapperkey(), mapper.getMappervalue());
    	}
    	return map;
    }

	public List<TemplateMapper> getMappers() {
		return this.mappers;
	}

	public void setMappers(List<TemplateMapper> mappers) {
		this.mappers = mappers;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public TemplateEntity getEntity() {
		return this.entity;
	}

	public void setEntity(TemplateEntity entity) {
		this.entity = entity;
	}

	public TemplateType getType() {
		return this.type;
	}

	public void setType(TemplateType type) {
		this.type = type;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
