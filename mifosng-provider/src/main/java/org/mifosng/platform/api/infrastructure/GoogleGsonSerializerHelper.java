package org.mifosng.platform.api.infrastructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifosng.platform.api.errorhandling.UnsupportedParameterException;
import org.springframework.stereotype.Service;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Helper class for serialization of java objects into JSON using google-gson.
 */
@Service
public class GoogleGsonSerializerHelper {

	public Gson createGsonBuilder(final boolean prettyPrint) {
		final GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(LocalDate.class, new JodaLocalDateAdapter());
		builder.registerTypeAdapter(DateTime.class, new JodaDateTimeAdapter());
		if (prettyPrint) {
			builder.setPrettyPrinting();
		}
		return builder.create();
	}
	
	public Gson createGsonBuilderWithParameterExclusionSerializationStrategy(
			final Set<String> supportedParameters, 
			final boolean prettyPrint,
			final Set<String> responseParameters) {

		final Set<String> parameterNamesToSkip = new HashSet<String>();

		if (!responseParameters.isEmpty()) {

			// strip out all known support parameters from expected response to
			// see if unsupported parameters requested for response.
			final Set<String> differentParametersDetectedSet = new HashSet<String>(responseParameters);
			differentParametersDetectedSet.removeAll(supportedParameters);

			if (!differentParametersDetectedSet.isEmpty()) {
				throw new UnsupportedParameterException(new ArrayList<String>(differentParametersDetectedSet));
			}

			parameterNamesToSkip.addAll(supportedParameters);
			parameterNamesToSkip.removeAll(responseParameters);
		}

		final ExclusionStrategy strategy = new ParameterListExclusionStrategy(parameterNamesToSkip);

		final GsonBuilder builder = new GsonBuilder().addSerializationExclusionStrategy(strategy);
		builder.registerTypeAdapter(LocalDate.class, new JodaLocalDateAdapter());
		builder.registerTypeAdapter(DateTime.class, new JodaDateTimeAdapter());
		if (prettyPrint) {
			builder.setPrettyPrinting();
		}
		return builder.create();
	}

	public String serializedJsonFrom(final Gson serializer, final Object[] dataObjects) {
		return serializer.toJson(dataObjects);
	}

	public String serializedJsonFrom(final Gson serializer, final Object singleDataObject) {
		return serializer.toJson(singleDataObject);
	}
}