package org.mifosng.platform.chartaccount.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mifosng.platform.infrastructure.AbstractAuditableCustom;
import org.mifosng.platform.user.domain.AppUser;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "m_chartaccount")
public class ChartAccount extends AbstractPersistable<Long>{
	@Column(name = "chartcode", nullable = false, length = 20)
	private long chartcode;
	@Column(name = "description", nullable = false, length = 100)
	private String description;
	@Column(name = "type", nullable = false, length = 100)
	private String type;
	
	  public ChartAccount(long chartcode,String description,String type)
	  {
		  this.chartcode=chartcode;
		  this.description=description;
		  this.type=type;
		  
	  }
	  
	  public static ChartAccount  createNew(long chartcode,String description,String type)
	  {
		  return new ChartAccount(chartcode,description,type);
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
