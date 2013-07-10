package org.mifosplatform.template.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.mifosplatform.template.domain.Template;
import org.springframework.stereotype.Service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;


@Service
public class TemplateMergeService {
	
	public static String compile(Template template, Map<String, Object> scopes) {
		
	    MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile(new StringReader(template.getText()),
				template.getName());
		
		//TODO: convert map into relevant objcets and add them to scopes map
		Map<String, String> data = template.getMetadata();
		
		if(data != null) {
			for (Map.Entry<String, String> entry: data.entrySet()) {
				scopes.put(entry.getKey(), entry.getValue());
			}
		}
		
		StringWriter stringWriter = new StringWriter();
		mustache.execute(stringWriter, scopes);

		return stringWriter.toString();
	}
}
