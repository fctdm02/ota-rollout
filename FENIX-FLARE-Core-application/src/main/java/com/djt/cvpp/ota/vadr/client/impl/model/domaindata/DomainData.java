
package com.djt.cvpp.ota.vadr.client.impl.model.domaindata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.djt.cvpp.ota.vadr.client.impl.model.applicationdata.ApplicationData;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "domainInstanceName",
    "description",
    "domainInstanceVersion",
    "releaseState",
    "productionState",
    "domain",
    "ecuHardwares",
    "ecuSoftwares"
})
@Data
public class DomainData {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("domainInstanceName")
    @NotEmpty(message = "DomainInstanceName cannot be null or empty.")
    private String domainInstanceName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("domainInstanceVersion")
    @NotEmpty(message = "domainInstanceVersion cannot be null or empty.")
    private String domainInstanceVersion;


    @JsonProperty("releaseState")
    private String releaseState;

    @JsonProperty("productionState")
    private String productionState;

    @JsonProperty("domain")
    @Valid
    @NotNull(message = "Domain object cannot be null")
    private Domain domain;

    @JsonProperty("ecuHardwares")
    @Valid
    private List<EcuHardware> ecuHardwares;

    @JsonProperty("ecuSoftwares")
    @Valid
    @NotNull(message = "EcuSoftware list cannot be null.")
    private List<EcuSoftware> ecuSoftwares;

    @JsonProperty("applications")
    @Valid
    private List<ApplicationData> applications;

    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
