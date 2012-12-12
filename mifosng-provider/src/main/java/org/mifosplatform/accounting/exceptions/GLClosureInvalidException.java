package org.mifosplatform.accounting.exceptions;

import java.util.Date;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * A {@link RuntimeException} thrown when a GL Closure is Invalid
 */
public class GLClosureInvalidException extends AbstractPlatformDomainRuleException {

    /*** enum of reasons for invalid Accounting Closure **/
    public static enum GL_CLOSURE_INVALID_REASON {
        FUTURE_DATE, ACCOUNTING_CLOSED;

        public String errorMessage() {
            if (name().toString().equalsIgnoreCase("FUTURE_DATE")) {
                return "Accounting closures cannot be made for a future date";
            } else if (name().toString().equalsIgnoreCase("ACCOUNTING_CLOSED")) { return "Accounting Closure for this branch has already been defined for a greater date"; }
            return name().toString();
        }

        public String errorCode() {
            if (name().toString().equalsIgnoreCase("FUTURE_DATE")) {
                return "error.msg.glclosure.invalid.future.date";
            } else if (name().toString().equalsIgnoreCase("ACCOUNTING_CLOSED")) { return "error.msg.glclosure.invalid.accounting.closed"; }
            return name().toString();
        }
    }

    public GLClosureInvalidException(final GL_CLOSURE_INVALID_REASON reason, final Date date) {
        super(reason.errorCode(), reason.errorMessage(), date);
    }
}