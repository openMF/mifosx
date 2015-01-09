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
import org.mifosplatform.portfolio.savings.api.SavingsAccountTransactionsApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implements {@link org.mifosplatform.batch.command.CommandStrategy} to handle
 * transaction on a savings account. It passes the contents of the body from the
 * BatchRequest to
 * {@link org.mifosplatform.portfolio.client.api.SavingsAccountTransactionApiResource} and gets
 * back the response. This class will also catch any errors raised by
 * {@link org.mifosplatform.portfolio.client.api.SavingsAccountTransactionApiResource} and map
 * those errors to appropriate status codes in BatchResponse.
 * 
 * @author Rishabh Shukla
 * 
 * @see org.mifosplatform.batch.command.CommandStrategy
 * @see org.mifosplatform.batch.domain.BatchRequest
 * @see org.mifosplatform.batch.domain.BatchResponse
 */
@Component
public class SavingsTransactionsCommandStrategy implements CommandStrategy {

    private final SavingsAccountTransactionsApiResource savingsTxnApiResource;

    @Autowired
    public SavingsTransactionsCommandStrategy(final SavingsAccountTransactionsApiResource savingsTxnApiResource) {
        this.savingsTxnApiResource = savingsTxnApiResource;
    }

    @Override
    public BatchResponse execute(final BatchRequest request, @SuppressWarnings("unused") UriInfo uriInfo) {

        final BatchResponse response = new BatchResponse();
        final String responseBody;

        response.setRequestId(request.getRequestId());
        response.setHeaders(request.getHeaders());
        
        final String[] pathParameters = request.getRelativeUrl().split("/");
        Long savingsAccountId = Long.parseLong(pathParameters[1]);
        String transactionType = pathParameters[2].substring(pathParameters[2].indexOf("?command=")+9,pathParameters[2].length());

        // Try-catch blocks to map exceptions to appropriate status codes
        try {

            // Calls 'transaction' function from 'SavingsAccountTransactionApiResource' to do the transaction on the savings account          
            responseBody = savingsTxnApiResource.transaction(savingsAccountId, transactionType, request.getBody());

            response.setStatusCode(200);
            
            // Sets the body of the response after the successful transaction on the savings account
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
