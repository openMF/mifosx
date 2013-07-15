package org.mifosplatform.xbrl.report.data;

public class ContextData {
	
	private final String dimensionType;
	private final String dimension;
	private final Integer periodType;
	
	public ContextData(String dimensionType, String dimension, Integer periodType) {
		this.dimensionType = dimensionType;
		this.dimension = dimension;
		this.periodType = periodType;
	}

	public String getDimensionType() {
		return this.dimensionType;
	}

	public String getDimension() {
		return this.dimension;
	}

	public Integer getPeriodType() {
		return this.periodType;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ContextData) {
			ContextData aContext = (ContextData) obj;
			if (aContext.getPeriodType() == this.getPeriodType() && 
					aContext.getDimension().equals(this.getDimension()) && 
					aContext.getDimensionType().equals(this.getDimensionType())) {
				return true;
			}
		}
		return false;
	}
}
