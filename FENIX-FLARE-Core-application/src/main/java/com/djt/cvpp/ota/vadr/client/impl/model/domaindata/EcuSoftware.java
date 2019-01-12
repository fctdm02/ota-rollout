
package com.djt.cvpp.ota.vadr.client.impl.model.domaindata;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "description",
    "fileId",
    "category",
    "legacyPartNumber",
    "softwareVersionNumber",
    "name",
    "id",
    "ecu"
})
@Data
public class EcuSoftware {

    @JsonProperty("description")
    @NotEmpty
    String description;

    @JsonProperty("fileId")
    @NotEmpty
    String fileId;

    @JsonProperty("category")
    private String category;

    @JsonProperty("legacyPartNumber")
    @NotEmpty
    private String legacyPartNumber;

    @JsonProperty("softwareVersionNumber")
    private String softwareVersionNumber;

    @JsonProperty("name")
    String name;

    @JsonProperty("id")
    Long id;

    @JsonProperty("ecu")
    @Valid
    private Ecu ecu;

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
