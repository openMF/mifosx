package org.mifosng.platform.api.commands;

import java.util.Set;

public class ChartAccountCommand {
	private long chartcode;
	private String description;
	private String type;
	private final Set<String> modifiedParameters;
	
	 public Set<String> getModifiedParameters() {
		return modifiedParameters;
	}

	public ChartAccountCommand(final Set<String> modifiedParameters,long chartcode,String description,String type)
	  {
		  this.modifiedParameters=modifiedParameters;
		  this.chartcode=chartcode;
		  this.description=description;
		  this.type=type;
	  }

	public long getChartcode() {
		return chartcode;
	}
	public void setChartcode(long chartcode) {
		this.chartcode = chartcode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
