package org.mifosplatform.template.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_templatemappers")
public class TemplateMapper extends AbstractPersistable<Long>{

	@Column(name="mapperorder")
	private int mapperorder;
	
	@Column(name="mapperkey")
	private String mapperkey;
	
	@Column(name="mappervalue")
	private String mappervalue;
	
	protected TemplateMapper(){};
	
	public TemplateMapper(int mapperorder, String mapperkey, String mappervalue){
		this.mapperorder = mapperorder;
		this.mapperkey = mapperkey;
		this.mappervalue = mappervalue;
	}

	public String getMapperkey() {
		return this.mapperkey;
	}

	public int getMapperorder() {
		return this.mapperorder;
	}

	public void setMapperorder(int mapperorder) {
		this.mapperorder = mapperorder;
	}

	public void setMapperkey(String mapperkey) {
		this.mapperkey = mapperkey;
	}

	public String getMappervalue() {
		return this.mappervalue;
	}

	public void setMappervalue(String mappervalue) {
		this.mappervalue = mappervalue;
	}
	
	
}
