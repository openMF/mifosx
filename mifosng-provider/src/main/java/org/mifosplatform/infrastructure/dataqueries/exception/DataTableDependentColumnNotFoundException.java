package org.mifosplatform.infrastructure.dataqueries.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class DataTableDependentColumnNotFoundException extends AbstractPlatformResourceNotFoundException{

	public DataTableDependentColumnNotFoundException() {
        super("error.msg.dependent.column.name.not.found", "Dependent Column not found.");
    }

}
