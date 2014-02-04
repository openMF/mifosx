package org.mifosplatform.template.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.google.gson.annotations.SerializedName;

@JsonSerialize(using = TemplateEntitySerializer.class)
public enum TemplateEntity {

    @SerializedName("client")
    CLIENT(0, "client"), @SerializedName("loan")
    LOAN(1, "loan");

    private int id;
    private String name;

    private TemplateEntity(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }
}
