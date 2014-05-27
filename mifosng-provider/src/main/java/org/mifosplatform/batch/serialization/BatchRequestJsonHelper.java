package org.mifosplatform.batch.serialization;

import java.lang.reflect.Type;
import java.util.List;

import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;

@Component
public class BatchRequestJsonHelper extends FromJsonHelper {
	
	public List<BatchRequest> extractList(final String json) {
		  final Type listType = new TypeToken<List<BatchRequest>>(){}.getType();
		  final List<BatchRequest> requests = super.getGsonConverter().fromJson(json, listType);
		  return requests;
		}
}
