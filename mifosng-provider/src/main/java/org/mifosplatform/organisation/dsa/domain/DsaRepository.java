package org.mifosplatform.organisation.dsa.domain;

import org.mifosplatform.organisation.dsa.domain.Dsa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DsaRepository extends JpaRepository<Dsa,Long>, JpaSpecificationExecutor<Dsa> {

	public final static String FIND_BY_OFFICE_QUERY = "select d from Dsa d where d.id = :id AND d.office.id = :officeId";

    /**
     * Find Dsa by officeid.
     */
    @Query(FIND_BY_OFFICE_QUERY)
    public Dsa findByOffice(@Param("id") Long id, @Param("officeId") Long officeId);
	
	
}
