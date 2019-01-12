
package com.djt.cvpp.ota.vadr.client.impl.model.applicationdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "version",
    "description",
    "blobStorageInfos",
    "swReleaseStatus",
    "swNecessity",
    "ownerGroup",
    "ownerKeyContact",
    "swClassification",
    "requiresSubscription",
    "regions"
})
@Data
public class ApplicationData {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    @NotEmpty
    private String name;

    @JsonProperty("version")
    @NotEmpty
    private String version;

    @JsonProperty("description")
    private String description;

    @JsonProperty("blobStorageInfos")
    @Valid
    private List<BlobStorageInfo> blobStorageInfos;

    @JsonProperty("swReleaseStatus")
    private String swReleaseStatus;

    @JsonProperty("swNecessity")
    private String swNecessity;

    @JsonProperty("ownerGroup")
    private String ownerGroup;

    @JsonProperty("ownerKeyContact")
    private String ownerKeyContact;

    @JsonProperty("swClassification")
    private String swClassification;

    @JsonProperty("requiresSubscription")
    private Boolean requiresSubscription;

    @JsonProperty("regions")
    @Valid
    private List<Object> regions;

    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
