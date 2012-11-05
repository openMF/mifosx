package org.mifosng.platform.api.data;

import java.util.Set;

public class ChartAccountData {
	private long chartcode;
	private String description;
	private String type;
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
	public ChartAccountData(final long chartcode,String description,String type)
	  {
		  this.chartcode=chartcode;
		  this.description=description;
		  this.type=type;
	  }

}
