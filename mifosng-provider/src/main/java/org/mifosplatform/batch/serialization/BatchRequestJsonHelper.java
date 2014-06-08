package org.mifosplatform.batch.serialization;

import java.lang.reflect.Type;
import java.util.List;

import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;

/**
 * Extends {@link org.mifosplatform.infrastructure.core.serialization.FromJsonHelper} to 
 * de-serialize the incoming String into a JSON List of type
 * {@link org.mifosplatform.batch.domain.BatchRequest}
 * 
 * @author Rishabh Shukla
 * 
 * @see org.mifosplatform.batch.domain.BatchRequest
 * @see org.mifosplatform.infrastructure.core.serialization.FromJsonHelper
 */
@Component
public class BatchRequestJsonHelper extends FromJsonHelper {
	
	/**
	 * returns a list of batchRequests after de-serializing it from the input
	 * JSON string.
	 * 
	 * @param json
	 * @return List<BatchRequest>
	 */
	public List<BatchRequest> extractList(final String json) {
		  final Type listType = new TypeToken<List<BatchRequest>>(){}.getType();
		  final List<BatchRequest> requests = super.getGsonConverter().fromJson(json, listType);
		  return requests;
		}
}
