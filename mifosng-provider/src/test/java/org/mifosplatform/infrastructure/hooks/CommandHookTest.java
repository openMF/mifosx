package org.mifosplatform.infrastructure.hooks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SampleCommandHandler.class,CommandHookRegistry.class,CommandHookProvider.class,CommandHookRejectAll.class,CommandRecordInPost.class})
public class CommandHookTest  {

    @Autowired
    private SampleCommandHandler handler;

    @Autowired
    private CommandHookRegistry registry;

    @Autowired
    private CommandRecordInPost recordInPost;

    @Test
    public void runWithoutHook() {
        CommandProcessingResult result = handler.processCommand(new JsonCommand(1L,"sample",null,null,"null",1L,1L,1L,1L,1L,1L,"","",1L));
        assertEquals(result.commandId().longValue(), 1L);
    }

    @Test
    public void rejectWithPreHook() {
        try {
            registry.register(CommandHookType.ActivateCenter,CommandHookRejectAll.class);
            handler.processCommand(new JsonCommand(1L,"sample",null,null,"null",1L,1L,1L,1L,1L,1L,"","",1L));
            assertTrue("Should have thrown exception ",false);
        } catch (IllegalAccessException e) {
            assertTrue("Threw exception " + e,false);
        } catch (InstantiationException e) {
            assertTrue("Threw exception " + e,false);
        } catch (GeneralPlatformDomainRuleException e) {
            assertTrue(true);
        }
    }

    @Test
    public void postHookCalled() {
        try {
            registry.register(CommandHookType.ActivateCenter,CommandRecordInPost.class);
            handler.processCommand(new JsonCommand(1L,"sample",null,null,"null",1L,1L,1L,1L,1L,1L,"","",1L));
            assertEquals(recordInPost.getSaved().commandId().longValue(),1L);
        } catch (IllegalAccessException e) {
            assertTrue("Threw exception " + e,false);
        } catch (InstantiationException e) {
            assertTrue("Threw exception " + e,false);
        } catch (GeneralPlatformDomainRuleException e) {
            assertTrue(true);
        }
    }
}
