package org.mifosplatform.xbrl.report.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReadTaxonomyMappingServiceImplTest {

    XBRLResultServiceImpl readService;

    @Before
    public void setUp() throws Exception {
        RoutingDataSource dataSource = Mockito.mock(RoutingDataSource.class);
        readService = new XBRLResultServiceImpl(dataSource, null, null);

    }

    @Test
    public void shouldCorrectlyGetGLCode() {
        ArrayList<String> result = readService.getGLCodes("{12000}+{11000}");
        assertEquals("12000", result.get(0));
        assertEquals("11000", result.get(1));
    }

}
