
package com.djt.cvpp.ota.vadr.client.impl.model.softwaremetadata;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "fileFormat",
    "createdTimestamp",
    "lastUpdatedTimestamp",
    "unzippedFileSize",
    "zippedFileSize",
    "autonomicUrl",
    "description",
    "nexusBinaryLocation",
    "swCategory",
    "vbfVersion",
    "fileName",
    "swPartNumber",
    "legacySwPartType",
    "dataFormatIdentifier",
    "frameFormat",
    "ecuMainNodeAddress",
    "ecuSubNetworkAddress",
    "ecuSubNodeAddress",
    "erase",
    "omit",
    "callStartAddress",
    "verificationStructureAddress",
    "swSignature",
    "publicKeyHash",
    "fileChecksum",
    "program",
    "canRx",
    "canTx",
    "canLogicalAddress",
    "ecuAcronym",
    "fileFingerprint",
    "programProtected",
    "firmwareInstallType",
    "ovtp",
    "didAddress"
})

@Data
public class SoftwareMetadata {

    @JsonProperty("id")
    public Integer id;
    @JsonProperty("fileFormat")
    public String fileFormat;
    @JsonProperty("createdTimestamp")
    public String createdTimestamp;
    @JsonProperty("lastUpdatedTimestamp")
    public String lastUpdatedTimestamp;
    @JsonProperty("unzippedFileSize")
    @NotNull(message = "SoftwareMetadata should have unzippedFileSize populated.")
    public Integer unzippedFileSize;
    @JsonProperty("zippedFileSize")
    public Integer zippedFileSize;
    @JsonProperty("autonomicUrl")
    @NotNull(message = "SoftwareMetadata should have autonomicUrl populated.")
    public String autonomicUrl;
    @JsonProperty("description")
    public String description;
    @JsonProperty("nexusBinaryLocation")
    public String nexusBinaryLocation;
    @JsonProperty("swCategory")
    public String swCategory;
    @JsonProperty("vbfVersion")
    public String vbfVersion;
    @JsonProperty("fileName")
    public String fileName;
    @JsonProperty("swPartNumber")
    @NotEmpty(message = "SoftwareMetadata should have swPartNumber populated.")
    public String swPartNumber;
    @JsonProperty("legacySwPartType")
    public String legacySwPartType;
    @JsonProperty("dataFormatIdentifier")
    public String dataFormatIdentifier;
    @JsonProperty("frameFormat")
    public String frameFormat;
    @JsonProperty("ecuMainNodeAddress")
    public String ecuMainNodeAddress;
    @JsonProperty("ecuSubNetworkAddress")
    public Object ecuSubNetworkAddress;
    @JsonProperty("ecuSubNodeAddress")
    public Object ecuSubNodeAddress;
    @JsonProperty("erase")
    @Valid
    public List<Erase> erase = null;
    @JsonProperty("omit")
    public Object omit;
    @JsonProperty("callStartAddress")
    public String callStartAddress;
    @JsonProperty("verificationStructureAddress")
    @Valid
    public List<VerificationStructureAddress> verificationStructureAddress = null;
    @JsonProperty("swSignature")
    public Object swSignature;
    @JsonProperty("publicKeyHash")
    @NotNull(message = "SoftwareMetadata should have publicKeyHash populated.")
    public String publicKeyHash;
    @JsonProperty("fileChecksum")
    public String fileChecksum;
    @JsonProperty("program")
    @Valid
    public List<Program> program = null;
    @JsonProperty("canRx")
    @NotEmpty(message = "SoftwareMetadata should have canRx populated.")
    public String canRx;
    @JsonProperty("canTx")
    public Object canTx;
    @JsonProperty("canLogicalAddress")
    public String canLogicalAddress;
    @JsonProperty("ecuAcronym")
    @NotEmpty(message = "SoftwareMetadata should have ecuAcronum populated.")
    public String ecuAcronym;
    @JsonProperty("fileFingerprint")
    public String fileFingerprint;
    @JsonProperty("programProtected")
    public Boolean programProtected;
    @JsonProperty("firmwareInstallType")
    @NotNull(message = "SoftwareMetadata should have firmwareInstallType populated.")
    public String firmwareInstallType;
    @JsonProperty("ovtp")
    public Boolean ovtp;
    @JsonProperty("didAddress")
    public String didAddress;
}
