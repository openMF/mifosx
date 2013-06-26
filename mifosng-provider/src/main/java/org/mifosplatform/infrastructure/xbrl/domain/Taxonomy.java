package org.mifosplatform.infrastructure.xbrl.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="m_taxonomy", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "namespace", "dimension" }, name = "name_ns_dim") })
public class Taxonomy extends AbstractPersistable<Long> {

	
}
