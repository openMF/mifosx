package org.mifosplatform.template.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = {"file:src/test/resources/META-INF/testContext.xml" })
public class TemplateRepositoryTest {
	
	@Autowired
	private TemplateRepository templateRepository;
	
	@Test
	public void saveTemplate() {
		Template template = new Template();
		template.setName("template_name");
		template.setText("$hello world!");
		
		template = templateRepository.save(template);
		
		assertNotNull(template);
		assertEquals(template, templateRepository.findOne(template.getId()));
	}
}
