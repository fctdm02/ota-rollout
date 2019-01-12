
package com.djt.cvpp.ota.vadr.client.impl.model.softwaremetadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "startAddress",
    "length"
})
@Data
public class Program {

    @JsonProperty("startAddress")
    public String startAddress;
    @JsonProperty("length")
    public String length;

}
