package com.djt.cvpp.ota.vadr.client.impl.model.applicationdata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomainAppDependency {
    String name;
    String version;
}
