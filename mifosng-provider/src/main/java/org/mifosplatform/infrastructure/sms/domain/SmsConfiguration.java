/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "sms_configuration")
public class SmsConfiguration extends AbstractPersistable<Long> {
	@Column(name = "name", nullable = false)
    private String name;
	
	@Column(name = "value", nullable = false)
    private String value;
	
	/** 
	 * SmsConfiguration constructor 
	 **/
	protected SmsConfiguration() {}
	
	/** 
	 * SmsConfiguration constructor 
	 **/
	public SmsConfiguration(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
