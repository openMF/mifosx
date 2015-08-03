package org.mifosplatform.organisation.dsa.domain;

import org.mifosplatform.organisation.dsa.domain.Dsa;
import org.mifosplatform.organisation.dsa.exception.DsaNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

public class DsaRepositoryWrapper {
	
	private final DsaRepository repository;
	
	@Autowired
	public DsaRepositoryWrapper(final DsaRepository repository){
		this.repository = repository;
	}
	
	public Dsa findOneWithNotFoundDetection(final Long id) {
        final Dsa dsa = this.repository.findOne(id);
        if (dsa == null) { throw new DsaNotFoundException(id); }
        return dsa;
    }

	public Dsa findByOfficeWithNotFoundDetection(final Long dsaId, final Long officeId) {
        final Dsa dsa = this.repository.findByOffice(dsaId, officeId);
        if (dsa == null) { throw new DsaNotFoundException(dsaId); }
        return dsa;
    }

    public Dsa findByOfficeHierarchyWithNotFoundDetection(final Long dsaId, final String hierarchy) {
        final Dsa dsa = this.repository.findOne(dsaId);
        if (dsa == null) { throw new DsaNotFoundException(dsaId); }
        final String dsahierarchy = dsa.office().getHierarchy();
        if (!hierarchy.startsWith(dsahierarchy)) { throw new DsaNotFoundException(dsaId); }
        return dsa;
    }
    public void save(final Dsa dsa){
        this.repository.save(dsa);
    }
}
