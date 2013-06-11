package org.mifosplatform.template.service;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.springframework.stereotype.Service;

@Service
public class TemplateMergeService {

	VelocityEngine velocityEngine = new VelocityEngine();
	Template template = new Template();
	
	public static String merge(String template) {
		
		VelocityContext context = new VelocityContext();
		context.put("key", "Hello");
		
		StringWriter stringWriter = new StringWriter();
		try {
			Velocity.evaluate(context, stringWriter, "logTag", template);
        }
        catch( ParseErrorException parseErrorException ) {
            System.out.println("ParseErrorException : " + parseErrorException );
        }

		return stringWriter.toString();
	}
}
