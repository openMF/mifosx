package org.mifosplatform.portfolio.loanproduct.productmix.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.loanproduct.productmix.service.ProductMixWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductMixCommandHandler implements NewCommandSourceHandler {

    private final ProductMixWritePlatformService productMixWritePlatformService;

    @Autowired
    public CreateProductMixCommandHandler(final ProductMixWritePlatformService productMixWritePlatformService) {

        this.productMixWritePlatformService = productMixWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.productMixWritePlatformService.createProductMix(command.getProductId(), command);
    }

}
