
package com.djt.cvpp.ota.vadr.client.impl.model.domaindata;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "domainName",
    "domainDescription",
    "ecus"
})
@Data
public class Domain {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("domainName")
    private String domainName;

    @JsonProperty("domainDescription")
    private String domainDescription;

    @JsonProperty("ecus")
    @Valid
    @NotNull(message = "ECU list cannot be null.")
    private List<Ecu> ecus;

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
