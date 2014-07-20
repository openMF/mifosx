package org.mifosplatform.infrastructure.configuration.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@Import(TestDataSourceConfiguration.class)
// we do NOT want the testContext.xml here, but the real one!
@ImportResource("classpath*:META-INF/spring/appContext.xml")
public class TestSpringConfiguration {
}