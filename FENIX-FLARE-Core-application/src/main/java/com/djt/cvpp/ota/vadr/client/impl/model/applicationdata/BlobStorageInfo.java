
package com.djt.cvpp.ota.vadr.client.impl.model.applicationdata;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "blobStorageId",
    "blobStorageLink",
    "autonomicId"
})
@Data
public class BlobStorageInfo {

    @JsonProperty("blobStorageId")
    @Min(value = 0)
    private Long blobStorageId;

    @JsonProperty("blobStorageLink")
    @NotEmpty
    private String blobStorageLink;

    @JsonProperty("autonomicId")
    private String autonomicId;

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
