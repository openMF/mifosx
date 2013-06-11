package org.mifosplatform.template;

import static org.junit.Assert.assertEquals;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.template.service.TemplateMergeService;

public class TemplateMergeServiceTest {
	
	private VelocityContext velocityContext;
	private String template;

	@Before
	public void setUp() throws Exception {
		
		velocityContext = new VelocityContext();
		template = "$key world!";
	}
	
	@Test
	public void evaluate() {
		
		String output = TemplateMergeService.merge(template);
		
		assertEquals(output, "Hello world!");
	}

}
