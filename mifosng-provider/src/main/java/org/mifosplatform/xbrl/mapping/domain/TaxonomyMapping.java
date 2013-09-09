package org.mifosplatform.xbrl.mapping.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "mix_taxonomy_mapping")
public class TaxonomyMapping extends AbstractPersistable<Long> {

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "config")
    private String config;

    @Column(name = "currency")
    private String currency;

    protected TaxonomyMapping() {
        // default
    }

    private TaxonomyMapping(final String identifier, final String config, final String currency) {
        this.identifier = StringUtils.defaultIfEmpty(identifier, null);
        this.config = StringUtils.defaultIfEmpty(config, null);
        this.currency = StringUtils.defaultIfEmpty(currency, null);
    }

    public static TaxonomyMapping fromJson(final JsonCommand command) {
        final String identifier = command.stringValueOfParameterNamed("identifier");
        final String config = command.stringValueOfParameterNamed("config");
        final String currency = command.stringValueOfParameterNamed("currency");
        return new TaxonomyMapping(identifier, config, currency);
    }

    public void update(final JsonCommand command) {

        this.identifier = command.stringValueOfParameterNamed("identifier");
        this.config = command.stringValueOfParameterNamed("config");
        this.currency = command.stringValueOfParameterNamed("currency");

    }

}
