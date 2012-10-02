package org.mifosng.platform.api.data;

import org.apache.commons.lang.StringUtils;

public class ClientLookup {

    private final Long id;
    private final String displayName;
    private final Long officeId;
    private final String officeName;

    public ClientLookup(final Long id, final String firstname, final String lastname, final Long officeId, String officeName) {
        this.id = id;
        
        StringBuilder displayNameBuilder = new StringBuilder(firstname);
        if (StringUtils.isNotBlank(displayNameBuilder.toString())) {
            displayNameBuilder.append(' ');
        }
        displayNameBuilder.append(lastname);

        this.displayName = displayNameBuilder.toString();

        this.officeId = officeId;
        this.officeName = officeName;
    }

    public ClientLookup(final Long id, final String firstname, final String lastname) {
        this.id = id;

        StringBuilder displayNameBuilder = new StringBuilder(firstname);
        if (StringUtils.isNotBlank(displayNameBuilder.toString())) {
            displayNameBuilder.append(' ');
        }
        displayNameBuilder.append(lastname);

        this.displayName = displayNameBuilder.toString();

        this.officeId = null;
        this.officeName = null;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object obj) {
        ClientLookup clientLookup = (ClientLookup) obj;
        return this.id.equals(clientLookup.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}