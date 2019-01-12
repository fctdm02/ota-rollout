
package com.djt.cvpp.ota.vadr.client.impl.model.softwaremetadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "address",
    "rootHash"
})
@Data
public class VerificationStructureAddress {

    @JsonProperty("address")
    public String address;
    @JsonProperty("rootHash")
    public String rootHash;

}
