/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.teller.api;

import java.util.Collection;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.teller.data.CashierData;
import org.mifosplatform.organisation.teller.data.CashierTransactionData;
import org.mifosplatform.organisation.teller.data.CashierTransactionsWithSummaryData;
import org.mifosplatform.organisation.teller.data.TellerData;
import org.mifosplatform.organisation.teller.data.TellerJournalData;
import org.mifosplatform.organisation.teller.data.TellerTransactionData;
import org.mifosplatform.organisation.teller.service.TellerManagementReadPlatformService;
import org.mifosplatform.organisation.teller.util.DateRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("tellers")
@Component
@Scope("singleton")
public class TellerApiResource {

    private final PlatformSecurityContext securityContext;
    private final DefaultToApiJsonSerializer<TellerData> jsonSerializer;
    private final TellerManagementReadPlatformService readPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandWritePlatformService;

    @Autowired
    public TellerApiResource(PlatformSecurityContext securityContext, DefaultToApiJsonSerializer<TellerData> jsonSerializer,
            TellerManagementReadPlatformService readPlatformService,
            PortfolioCommandSourceWritePlatformService commandWritePlatformService) {
        super();
        this.securityContext = securityContext;
        this.jsonSerializer = jsonSerializer;
        this.readPlatformService = readPlatformService;
        this.commandWritePlatformService = commandWritePlatformService;
    }

    @GET
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String getTellerData(@QueryParam("officeId") final Long officeId) {
        final Collection<TellerData> foundTellers = this.readPlatformService.getTellers(officeId);

        return this.jsonSerializer.serialize(foundTellers);
    }

    @Path("{tellerId}")
    @GET
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String findTeller(@PathParam("tellerId") final Long tellerId) {
        final TellerData teller = this.readPlatformService.findTeller(tellerId);

        return this.jsonSerializer.serialize(teller);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String createTeller(final String tellerData) {
        final CommandWrapper request = new CommandWrapperBuilder().createTeller().withJson(tellerData).build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(request);

        return this.jsonSerializer.serialize(result);
    }

    @Path("{tellerId}")
    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String updateTeller(@PathParam("tellerId") final Long tellerId, final String tellerData) {
        final CommandWrapper request = new CommandWrapperBuilder().updateTeller(tellerId).withJson(tellerData).build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(request);

        return this.jsonSerializer.serialize(result);
    }

    @Path("{tellerId}")
    @DELETE
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteTeller(@PathParam("tellerId") final Long tellerId) {
        final CommandWrapper request = new CommandWrapperBuilder().deleteTeller(tellerId).build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(request);

        return this.jsonSerializer.serialize(result);
    }

    @GET
    @Path("{tellerId}/cashiers")
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String getCashierData(@PathParam("tellerId") final Long tellerId, @QueryParam("fromdate") final String fromDateStr,
            @QueryParam("todate") final String toDateStr) {
        final DateTimeFormatter dateFormatter = ISODateTimeFormat.basicDate();

        final Date fromDate = (fromDateStr != null ? dateFormatter.parseDateTime(fromDateStr).toDate() : new Date());
        final Date toDate = (toDateStr != null ? dateFormatter.parseDateTime(toDateStr).toDate() : new Date());

        final TellerData teller = this.readPlatformService.findTeller(tellerId);
        final Collection<CashierData> cashiers = this.readPlatformService.getCashiersForTeller(tellerId, fromDate, toDate);

        CashiersForTeller cashiersForTeller = new CashiersForTeller();
        cashiersForTeller.cashiers = cashiers;
        cashiersForTeller.tellerId = tellerId;
        cashiersForTeller.tellerName = teller.getName();
        cashiersForTeller.officeId = teller.getOfficeId();
        cashiersForTeller.officeName = teller.getOfficeName();

        return this.jsonSerializer.serialize(cashiersForTeller);
    }

    @GET
    @Path("{tellerId}/cashiers/{cashierId}")
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String findCashierData(@PathParam("tellerId") final Long tellerId, @PathParam("cashierId") final Long cashierId) {
        final CashierData cashier = this.readPlatformService.findCashier(cashierId);

        return this.jsonSerializer.serialize(cashier);
    }

    @GET
    @Path("{tellerId}/cashiers/template")
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String getCashierTemplate(@PathParam("tellerId") final Long tellerId) {

        final TellerData teller = this.readPlatformService.findTeller(tellerId);
        Long officeId = teller.getOfficeId();

        final CashierData cashier = this.readPlatformService.retrieveCashierTemplate(officeId, tellerId, true);

        return this.jsonSerializer.serialize(cashier);
    }

    @POST
    @Path("{tellerId}/cashiers")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String createCashier(@PathParam("tellerId") final Long tellerId, final String cashierData) {
        final CommandWrapper request = new CommandWrapperBuilder().allocateTeller(tellerId).withJson(cashierData).build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(request);

        return this.jsonSerializer.serialize(result);
    }

    @PUT
    @Path("{tellerId}/cashiers/{cashierId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String updateCashier(@PathParam("tellerId") final Long tellerId, @PathParam("cashierId") final Long cashierId,
            final String cashierDate) {
        final CommandWrapper request = new CommandWrapperBuilder().updateAllocationTeller(tellerId, cashierId).withJson(cashierDate)
                .build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(request);

        return this.jsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{tellerId}/cashiers/{cashierId}")
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteCashier(@PathParam("tellerId") final Long tellerId, @PathParam("cashierId") final Long cashierId) {
        final CommandWrapper request = new CommandWrapperBuilder().deleteAllocationTeller(tellerId, cashierId).build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(request);

        return this.jsonSerializer.serialize(result);
    }

    @POST
    @Path("{tellerId}/cashiers/{cashierId}/allocate")
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String allocateCashToCashier(@PathParam("tellerId") final Long tellerId, @PathParam("cashierId") final Long cashierId,
            final String cashierTxnData) {
        final CommandWrapper request = new CommandWrapperBuilder().allocateCashToCashier(tellerId, cashierId).withJson(cashierTxnData)
                .build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(request);

        return this.jsonSerializer.serialize(result);

    }

    @POST
    @Path("{tellerId}/cashiers/{cashierId}/settle")
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String settleCashFromCashier(@PathParam("tellerId") final Long tellerId, @PathParam("cashierId") final Long cashierId,
            final String cashierTxnData) {
        final CommandWrapper request = new CommandWrapperBuilder().settleCashFromCashier(tellerId, cashierId).withJson(cashierTxnData)
                .build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(request);

        return this.jsonSerializer.serialize(result);

    }

    @GET
    @Path("{tellerId}/cashiers/{cashierId}/transactions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String getTransactionsForCashier(@PathParam("tellerId") final Long tellerId, @PathParam("cashierId") final Long cashierId,
            @QueryParam("currencyCode") final String currencyCode) {
        final TellerData teller = this.readPlatformService.findTeller(tellerId);
        final CashierData cashier = this.readPlatformService.findCashier(cashierId);

        final Date fromDate = null;
        final Date toDate = null;

        final Collection<CashierTransactionData> cashierTxns = this.readPlatformService.retrieveCashierTransactions(cashierId, false,
                fromDate, toDate, currencyCode);

        return this.jsonSerializer.serialize(cashierTxns);
    }

    @GET
    @Path("{tellerId}/cashiers/{cashierId}/summaryandtransactions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String getTransactionsWtihSummaryForCashier(@PathParam("tellerId") final Long tellerId,
            @PathParam("cashierId") final Long cashierId, @QueryParam("currencyCode") final String currencyCode) {
        final TellerData teller = this.readPlatformService.findTeller(tellerId);
        final CashierData cashier = this.readPlatformService.findCashier(cashierId);

        final Date fromDate = null;
        final Date toDate = null;

        final CashierTransactionsWithSummaryData cashierTxnWithSummary = this.readPlatformService
                .retrieveCashierTransactionsWithSummary(cashierId, false, fromDate, toDate, currencyCode);

        return this.jsonSerializer.serialize(cashierTxnWithSummary);
    }

    @GET
    @Path("{tellerId}/cashiers/{cashierId}/transactions/template")
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String getCashierTxnTemplate(@PathParam("tellerId") final Long tellerId, @PathParam("cashierId") final Long cashierId) {

        final CashierTransactionData cashierTxnTemplate = this.readPlatformService.retrieveCashierTxnTemplate(cashierId);

        return this.jsonSerializer.serialize(cashierTxnTemplate);
    }

    @GET
    @Path("{tellerId}/transactions")
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String getTransactionData(@PathParam("tellerId") final Long tellerId, @QueryParam("dateRange") final String dateRange) {
        final DateRange dateRangeHolder = DateRange.fromString(dateRange);

        final Collection<TellerTransactionData> transactions = this.readPlatformService.fetchTellerTransactionsByTellerId(tellerId,
                dateRangeHolder.getStartDate(), dateRangeHolder.getEndDate());

        return this.jsonSerializer.serialize(transactions);
    }

    @GET
    @Path("{tellerId}/transactions/{transactionId}")
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String findTransactionData(@PathParam("tellerId") final Long tellerid, @PathParam("transactionId") final Long transactionId) {
        final TellerTransactionData transaction = this.readPlatformService.findTellerTransaction(transactionId);

        return this.jsonSerializer.serialize(transaction);
    }

    @GET
    @Path("{tellerId}/journals")
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String getJournalData(@PathParam("tellerId") final Long tellerId, @QueryParam("cashierId") final Long cashierDate,
            @QueryParam("dateRange") final String dateRange) {
        final DateRange dateRangeHolder = DateRange.fromString(dateRange);

        final Collection<TellerJournalData> journals = this.readPlatformService.fetchTellerJournals(tellerId, cashierDate,
                dateRangeHolder.getStartDate(), dateRangeHolder.getEndDate());

        return this.jsonSerializer.serialize(journals);
    }

    private class CashiersForTeller {

        public Long tellerId;
        public String tellerName;
        public Long officeId;
        public String officeName;
        public Collection<CashierData> cashiers;

    }
}
