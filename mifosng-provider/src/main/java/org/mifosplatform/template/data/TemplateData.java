package org.mifosplatform.template.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifosplatform.template.domain.Template;
import org.mifosplatform.template.domain.TemplateEntity;
import org.mifosplatform.template.domain.TemplateType;

public class TemplateData {
	
	@SuppressWarnings("unused")
	private final List<Map<String,Object>> entities;
	@SuppressWarnings("unused")
	private final List<Map<String,Object>> types;
	@SuppressWarnings("unused")
	private final Template template;
	
	private TemplateData (Template template) {
		this.template = template;
		this.entities = getEntites();
		this.types = getTypes() ;
	}
	
	public static TemplateData template(Template template) {
		return new TemplateData(template);
	}
	
	public static TemplateData template() {
		return new TemplateData(null);
	}
	
	private List<Map<String,Object>> getEntites() {
		List<Map<String,Object>> l = new ArrayList<Map<String,Object>>();
		for(TemplateEntity e : TemplateEntity.values()){
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("id", e.getId());
			m.put("name", e.getName());
			l.add(m);
		}
		return l;
	}
	
	private List<Map<String,Object>> getTypes() {
		List<Map<String,Object>> l = new ArrayList<Map<String,Object>>();
		for(TemplateType e : TemplateType.values()){
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("id", e.getId());
			m.put("name", e.getName());
			l.add(m);
		}
		return l;
	}
}
