
package com.djt.cvpp.ota.vadr.client.impl.model.domaindata;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "canReceptionId",
    "ecuAcronymName"
})
@Data
public class Ecu {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("canReceptionId")
    @NotEmpty(message = "canReceptionId cannot be null.")
    private String canReceptionId; // aka node address

    @JsonProperty("ecuAcronymName")
    private String ecuAcronymName;

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
