package org.mifosplatform.batch.command;

import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;

public interface CommandStrategy {

	public BatchResponse execute(BatchRequest batchRequest);
}
