/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.batch.command.internal;

import javax.ws.rs.core.UriInfo;

import org.mifosplatform.batch.command.CommandStrategy;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.batch.exception.ErrorHandler;
import org.mifosplatform.batch.exception.ErrorInfo;
import org.mifosplatform.portfolio.loanaccount.api.LoanTransactionsApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implements {@link org.mifosplatform.batch.command.CommandStrategy} to handle
 * transactions on a loan. It passes the contents of the body from the
 * BatchRequest to
 * {@link org.mifosplatform.portfolio.client.api.LoanTransactionsApiResource} and gets
 * back the response. This class will also catch any errors raised by
 * {@link org.mifosplatform.portfolio.client.api.LoanTransactionsApiResource} and map
 * those errors to appropriate status codes in BatchResponse.
 * 
 * @author Rishabh Shukla
 * 
 * @see org.mifosplatform.batch.command.CommandStrategy
 * @see org.mifosplatform.batch.domain.BatchRequest
 * @see org.mifosplatform.batch.domain.BatchResponse
 */
@Component
public class LoanTransactionsCommandStrategy implements CommandStrategy {

    private final LoanTransactionsApiResource loansTxnApiResource;

    @Autowired
    public LoanTransactionsCommandStrategy(final LoanTransactionsApiResource loansTxnApiResource) {
        this.loansTxnApiResource = loansTxnApiResource;
    }

    @Override
    public BatchResponse execute(final BatchRequest request, @SuppressWarnings("unused") UriInfo uriInfo) {

        final BatchResponse response = new BatchResponse();
        final String responseBody;

        response.setRequestId(request.getRequestId());
        response.setHeaders(request.getHeaders());
        
        final String[] pathParameters = request.getRelativeUrl().split("/");
        Long loanId = Long.parseLong(pathParameters[1]);
        String transactionType = pathParameters[2].substring(pathParameters[2].indexOf("?command=")+9,pathParameters[2].length());

        // Try-catch blocks to map exceptions to appropriate status codes
        try {

            // Calls 'transaction' function from 'LoanTransactionsApiResource' to do transaction on a loan account          
            responseBody = loansTxnApiResource.executeLoanTransaction(loanId, transactionType, request.getBody());

            response.setStatusCode(200);
            
            // Sets the body of the response after the successful transaction on the loan
            response.setBody(responseBody);

        } catch (RuntimeException e) {

            // Gets an object of type ErrorInfo, containing information about
            // raised exception
            ErrorInfo ex = ErrorHandler.handler(e);

            response.setStatusCode(ex.getStatusCode());
            response.setBody(ex.getMessage());
        }

        return response;
    }

}
