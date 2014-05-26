package org.mifosplatform.batch.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

<<<<<<< HEAD
import org.mifosplatform.batch.command.CommandContext;
import org.mifosplatform.batch.command.CommandStrategy;
import org.mifosplatform.batch.command.CommandStrategyProvider;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation for {@link BatchApiService} to iterate through all the incoming
 * requests and obtain the appropriate CommandStrategy from CommandStrategyProvider. 
 * 
 * @author Rishabh Shukla
 *
 * @see org.mifosplatform.batch.domain.BatchRequest
 * @see org.mifosplatform.batch.domain.BatchResponse
 * @see org.mifosplatform.batch.command.CommandStrategyProvider
 */
@Service
public class BatchApiServiceImpl implements BatchApiService{
	
	private final CommandStrategyProvider strategyProvider;
	
	/**
	 * Constructs a 'BatchApiServiceImpl' with an argument of 
	 * {@link org.mifosplatform.batch.command.CommandStrategyProvider} type.
	 * 
	 * @param strategyProvider
	 */
	@Autowired
	public BatchApiServiceImpl(final CommandStrategyProvider strategyProvider) {
		this.strategyProvider = strategyProvider;
	}
	
	@Override
	public List<BatchResponse> handleBatchRequests(List<BatchRequest> requestList) {
		
		final  List<BatchResponse> responseList = new ArrayList<>(requestList.size());
		
		for(BatchRequest br: requestList) {
			
			final CommandStrategy commandStrategy = this.strategyProvider.getCommandStrategy(CommandContext.
					resource(br.getRelativeUrl()).method(br.getMethod()).build());
			
			final BatchResponse response = commandStrategy.execute(br);
			
			responseList.add(response);
		}
=======
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.batch.domain.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation for {@link BatchApiService} to iterate through all the incoming
 * requests and obtain the appropriate CommandStrategy from CommandStrategyFactory. 
 * 
 * @author Rishabh Shukla
 *
 * @see org.mifosplatform.batch.domain.BatchRequest
 * @see org.mifosplatform.batch.domain.BatchResponse
 * @see CommandStrategyFactory
 */
@Service
public class BatchApiServiceImpl implements BatchApiService{
	
	private final CommandStrategyFactory strategyFactory;
	
	/**
	 * Constructs a 'BatchApiServiceImpl' with an argument of {@link CommandStrategyFactory} type.
	 * 
	 * @param strategyFactory
	 */
	@Autowired
	public BatchApiServiceImpl(final CommandStrategyFactory strategyFactory) {
		this.strategyFactory = strategyFactory;
	}
	
	@Override
	public List<BatchResponse> handleBatchRequests(List<BatchRequest> requestList) {
		
		List<BatchResponse> responseList = new ArrayList<BatchResponse>();
<<<<<<< HEAD
		responseList.add(response);
>>>>>>> added javadocs in domain and api classes
=======
		Iterator<BatchRequest> itr = requestList.iterator();
		
		while(itr.hasNext()) {
			BatchRequest request = itr.next();
			final Long requestId = request.getRequestId();
			final Integer statusCode = 200;
			final Set<Header> headers = request.getHeaders(); 
			final String body = request.getBody();
			
			BatchResponse response = new BatchResponse(requestId, statusCode, headers, body);
			responseList.add(response);
		}
>>>>>>> Resource and service classes implemented
		
		return responseList;
	}
}
