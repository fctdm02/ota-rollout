
package com.djt.cvpp.ota.vadr.client.impl.model.domaindata;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "ecuAssemblyPartNumber",
    "ecu"
})
@Data
public class EcuHardware {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("ecuAssemblyPartNumber")
    private String ecuAssemblyPartNumber;
    @JsonProperty("ecu")
    @Valid
    private Ecu ecu;

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
