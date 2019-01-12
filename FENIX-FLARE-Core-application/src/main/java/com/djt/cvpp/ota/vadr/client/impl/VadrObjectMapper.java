/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.djt.cvpp.ota.vadr.client.impl;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.djt.cvpp.ota.common.exception.EntityAlreadyExistsException;
import com.djt.cvpp.ota.common.exception.EntityDoesNotExistException;
import com.djt.cvpp.ota.vadr.model.BinaryMetadata;
import com.djt.cvpp.ota.vadr.model.enums.FileFormat;
import com.djt.cvpp.ota.vadr.model.enums.ReleaseState;
import com.djt.cvpp.ota.vadr.model.enums.SoftwareCategory;
import com.djt.cvpp.ota.vadr.model.enums.SoftwareClassification;

/**
 *
 * @author tmyers1@yahoo.com (Tom Myers)
 *
 */
public class VadrObjectMapper {
	
	public com.djt.cvpp.ota.vadr.model.Domain mapParentDomainFromDomainInstance(com.djt.cvpp.ota.vadr.client.impl.model.domaindata.DomainData vadrDomainInstance) {
		
		Set<com.djt.cvpp.ota.vadr.model.EcuNode> fenixEcuNodes = new TreeSet<>();

		com.djt.cvpp.ota.vadr.client.impl.model.domaindata.Domain vadrDomain = vadrDomainInstance.getDomain();

		Iterator<com.djt.cvpp.ota.vadr.client.impl.model.domaindata.Ecu> vadrEcuIterator = vadrDomain.getEcus().iterator();
		while (vadrEcuIterator.hasNext()) {
			
			com.djt.cvpp.ota.vadr.client.impl.model.domaindata.Ecu vadrEcu = vadrEcuIterator.next();
			
			com.djt.cvpp.ota.vadr.model.EcuNode fenixEcuNode = new com.djt.cvpp.ota.vadr.model.EcuNode
                    .EcuNodeBuilder()
                    .withEcuAcronymName(vadrEcu.getEcuAcronymName())
                    .withEcuLogicalAddress(vadrEcu.getCanReceptionId())
					.withPersistentIdentity(Long.valueOf(vadrEcu.getId().longValue()))
                    .build();
            fenixEcuNodes.add(fenixEcuNode);
		}
		
		com.djt.cvpp.ota.vadr.model.Domain fenixDomain = new com.djt.cvpp.ota.vadr.model.Domain
                .DomainBuilder()
                .withDomainName(vadrDomain.getDomainName())
                .withDomainDescription(vadrDomain.getDomainDescription())
                .withEcuNodes(fenixEcuNodes)
				.withPersistentIdentity(Long.valueOf(vadrDomain.getId().longValue()))
                .build();
		
		return fenixDomain;
	}



	public com.djt.cvpp.ota.vadr.model.DomainInstance mapDomainInstance(
		com.djt.cvpp.ota.vadr.model.Domain fenixParentDomain,
		com.djt.cvpp.ota.vadr.client.impl.model.domaindata.DomainData vadrDomainInstance) throws EntityAlreadyExistsException, EntityDoesNotExistException {
		
		Set<com.djt.cvpp.ota.vadr.model.EcuHardware> fenixEcuHardwares = new TreeSet<>();
		Iterator<com.djt.cvpp.ota.vadr.client.impl.model.domaindata.EcuHardware> vadrEcuHardwareIterator = vadrDomainInstance.getEcuHardwares().iterator();
		while (vadrEcuHardwareIterator.hasNext()) {

			com.djt.cvpp.ota.vadr.client.impl.model.domaindata.EcuHardware vadrEcuHardware = vadrEcuHardwareIterator.next();

			com.djt.cvpp.ota.vadr.model.EcuNode fenixEcuNode = fenixParentDomain.getChildEcuNode(vadrEcuHardware.getEcu().getEcuAcronymName(), vadrEcuHardware.getEcu().getCanReceptionId());

			com.djt.cvpp.ota.vadr.model.EcuHardware fenixEcuHardware = new com.djt.cvpp.ota.vadr.model.EcuHardware
					.EcuHardwareBuilder()
					.withEcuNode(fenixEcuNode)
					.withEcuHardwarePartNumber(vadrEcuHardware.getEcuAssemblyPartNumber())
					.withPersistentIdentity(Long.valueOf(vadrEcuHardware.getId().longValue()))
					.build();
			fenixEcuHardwares.add(fenixEcuHardware);
		}

		Set<com.djt.cvpp.ota.vadr.model.EcuSoftware> fenixEcuSoftwares = new TreeSet<>();
		Iterator<com.djt.cvpp.ota.vadr.client.impl.model.domaindata.EcuSoftware> vadrEcuSoftwareIterator = vadrDomainInstance.getEcuSoftwares().iterator();
		while (vadrEcuSoftwareIterator.hasNext()) {
			
			com.djt.cvpp.ota.vadr.client.impl.model.domaindata.EcuSoftware vadrEcuSoftware = vadrEcuSoftwareIterator.next();
			
			com.djt.cvpp.ota.vadr.model.EcuNode fenixEcuNode = fenixParentDomain.getChildEcuNode(vadrEcuSoftware.getEcu().getEcuAcronymName(), vadrEcuSoftware.getEcu().getCanReceptionId());

			com.djt.cvpp.ota.vadr.model.BinaryMetadata fenixEcuSoftwareBinaryMetadata = new com.djt.cvpp.ota.vadr.model.BinaryMetadata
					.BinaryMetadataBuilder()
					.withPersistentIdentity(Long.valueOf(vadrEcuSoftware.getFileId()))
					.withSwPartNumber(vadrEcuSoftware.getFileId()) // TODO: TDM: VADR Team needs to specify the swPartNumber as the soft/foreign key from one database to the other, as it is environment agnostic.
					.build();

			com.djt.cvpp.ota.vadr.model.EcuSoftware fenixEcuSoftware = new com.djt.cvpp.ota.vadr.model.EcuSoftware
                    .EcuSoftwareBuilder()
                    .withEcuNode(fenixEcuNode)
                    .withEcuSoftwarePartNumber(vadrEcuSoftware.getLegacyPartNumber())
                    .withSoftwareName(vadrEcuSoftware.getName())
                    .withSoftwareVersionNumber(vadrEcuSoftware.getSoftwareVersionNumber())
					.withBinaryMetadata(fenixEcuSoftwareBinaryMetadata)
					.withPersistentIdentity(Long.valueOf(vadrEcuSoftware.getId().longValue()))
                    .build();

			fenixEcuSoftwares.add(fenixEcuSoftware);
		}

		com.djt.cvpp.ota.vadr.model.DomainInstance fenixDomainInstance = new com.djt.cvpp.ota.vadr.model.DomainInstance
                .DomainInstanceBuilder()
                .withParentDomain(fenixParentDomain)
                .withDomainInstanceName(vadrDomainInstance.getDomainInstanceName())
                .withDomainInstanceVersion(vadrDomainInstance.getDomainInstanceVersion())
                .withDomainInstanceDescription(vadrDomainInstance.getDescription())
                //TODO: Add to VADR anemic model 
                //.withReleasedDate(new Timestamp(AbstractEntity.getTimeKeeper().getCurrentTimeInMillis()))
                .withReleaseState(ReleaseState.RELEASED)
                //.withProductionState(ProductionState.PRODUCTION) // TODO: Figure out what the deal is with ProductionState
                .withEcuHardwares(fenixEcuHardwares)
                .withEcuSoftwares(fenixEcuSoftwares)
                //.withApplications(applications)
				.withPersistentIdentity(Long.valueOf(vadrDomainInstance.getId().longValue()))
                .build();

		return fenixDomainInstance;
	}



	public com.djt.cvpp.ota.vadr.model.Application mapApplication(com.djt.cvpp.ota.vadr.client.impl.model.applicationdata.ApplicationData vadrApplicationData) {

		Set<BinaryMetadata> applicationBinaryMetadatas = new TreeSet<>();
		Iterator<com.djt.cvpp.ota.vadr.client.impl.model.applicationdata.BlobStorageInfo> blobStorageInfoIterator = vadrApplicationData.getBlobStorageInfos().iterator();
		while (blobStorageInfoIterator.hasNext()) {

			com.djt.cvpp.ota.vadr.client.impl.model.applicationdata.BlobStorageInfo vadrBlobStorageInfo = blobStorageInfoIterator.next();

			com.djt.cvpp.ota.vadr.model.BinaryMetadata fenixApplicationBinaryMetadata = new com.djt.cvpp.ota.vadr.model.BinaryMetadata
					.BinaryMetadataBuilder()
					.withPersistentIdentity(Long.valueOf(vadrBlobStorageInfo.getBlobStorageId()))
					.withSwPartNumber(Long.toString(vadrBlobStorageInfo.getBlobStorageId())) // TODO: TDM: VADR Team needs to specify the swPartNumber as the soft/foreign key from one database to the other, as it is environment agnostic.
					.build();

			applicationBinaryMetadatas.add(fenixApplicationBinaryMetadata);
		}

		com.djt.cvpp.ota.vadr.model.Application fenixApplication = new com.djt.cvpp.ota.vadr.model.Application
				.ApplicationBuilder()
				//.withAppId(vadrApplicationData.???) TODO:
				.withAppName(vadrApplicationData.getName())
				.withAppVersion(vadrApplicationData.getVersion())
				//.withRegion(easternTimeZoneNorthAmericaRegion) TODO: Figure out what the deal is with regions (i.e. cardinality, business rules, etc.)
				.withDescription(vadrApplicationData.getDescription())
				.withOwnerGroup(vadrApplicationData.getOwnerGroup())
				.withOwnerKeyContact(vadrApplicationData.getOwnerKeyContact())
				.withRequiresSubscription(vadrApplicationData.getRequiresSubscription())
				//.withApplicationNecessity(vadrApplicationData.getSwNecessity()) // TODO: Map to the enum
				.withReleaseState(ReleaseState.RELEASED)
				.withBinaryMetadatas(applicationBinaryMetadatas)
				.withPersistentIdentity(vadrApplicationData.getId())
				.build();

		return fenixApplication;
	}



	public com.djt.cvpp.ota.vadr.model.BinaryMetadata mapBinaryMetadata(com.djt.cvpp.ota.vadr.client.impl.model.softwaremetadata.SoftwareMetadata vadrSoftwareMetadata) {

		Set<com.djt.cvpp.ota.vadr.model.ProgramLogicalBlock> fenixProgramLogicalBlocks = new TreeSet<>();
		Iterator<com.djt.cvpp.ota.vadr.client.impl.model.softwaremetadata.Program> vadrProgramBlockIterator = vadrSoftwareMetadata.getProgram().iterator();
		while (vadrProgramBlockIterator.hasNext()) {

			com.djt.cvpp.ota.vadr.client.impl.model.softwaremetadata.Program vadrProgramBlock = vadrProgramBlockIterator.next();
			com.djt.cvpp.ota.vadr.model.ProgramLogicalBlock fenixProgramLogicalBlock = new com.djt.cvpp.ota.vadr.model.ProgramLogicalBlock
					.ProgramLogicalBlockBuilder()
					.withStartAddress(vadrProgramBlock.getStartAddress())
					.withLength(vadrProgramBlock.getLength())
					.build();
			fenixProgramLogicalBlocks.add(fenixProgramLogicalBlock);
		}

		Set<com.djt.cvpp.ota.vadr.model.EraseLogicalBlock> fenixEraseLogicalBlocks = new TreeSet<>();
		Iterator<com.djt.cvpp.ota.vadr.client.impl.model.softwaremetadata.Erase> vadrEraseBlockIterator = vadrSoftwareMetadata.getErase().iterator();
		while (vadrEraseBlockIterator.hasNext()) {

			com.djt.cvpp.ota.vadr.client.impl.model.softwaremetadata.Erase vadrEraseBlock = vadrEraseBlockIterator.next();
			com.djt.cvpp.ota.vadr.model.EraseLogicalBlock fenixEraseLogicalBlock = new com.djt.cvpp.ota.vadr.model.EraseLogicalBlock
					.EraseLogicalBlockBuilder()
					.withStartAddress(vadrEraseBlock.getStartAddress())
					.withLength(vadrEraseBlock.getLength())
					.build();
			fenixEraseLogicalBlocks.add(fenixEraseLogicalBlock);
		}

		Set<com.djt.cvpp.ota.vadr.model.VerificationLogicalBlock> fenixVerificationLogicalBlocks = new TreeSet<>();
		Iterator<com.djt.cvpp.ota.vadr.client.impl.model.softwaremetadata.VerificationStructureAddress> verificationStructureAddressIterator = vadrSoftwareMetadata.verificationStructureAddress.iterator();
		while (verificationStructureAddressIterator.hasNext()) {

			com.djt.cvpp.ota.vadr.client.impl.model.softwaremetadata.VerificationStructureAddress vadrVerificationStructureAddressBlock = verificationStructureAddressIterator.next();
			com.djt.cvpp.ota.vadr.model.VerificationLogicalBlock fenixVerificationLogicalBlock = new com.djt.cvpp.ota.vadr.model.VerificationLogicalBlock
					.VerificationLogicalBlockBuilder()
					.withStartAddress(vadrVerificationStructureAddressBlock.getAddress())
					.withRootHash(vadrVerificationStructureAddressBlock.getRootHash())
					.build();
			fenixVerificationLogicalBlocks.add(fenixVerificationLogicalBlock);
		}

		com.djt.cvpp.ota.vadr.model.BinaryMetadata fenixBinaryMetadata = new com.djt.cvpp.ota.vadr.model.BinaryMetadata
				.BinaryMetadataBuilder()
				//.withPersistentIdentity(Long.valueOf(vadrSoftwareMetadata.getId().longValue()))
				.withEcuAcronymName(vadrSoftwareMetadata.getEcuAcronym())
				.withEcuLogicalAddress(vadrSoftwareMetadata.getCanLogicalAddress())
				.withPublicKeyHash(vadrSoftwareMetadata.getPublicKeyHash())
				.withSwPartNumber(vadrSoftwareMetadata.swPartNumber)
				.withLegacySwPartType(vadrSoftwareMetadata.getLegacySwPartType())
				.withSwCategory(SoftwareCategory.BINARY_APPLICATION)
				.withSwClassification(SoftwareClassification.PRODUCTION)
				.withDidAddress(vadrSoftwareMetadata.getDidAddress())
				.withIsOvtp(Boolean.TRUE)
				.withProgramLogicalBlocks(fenixProgramLogicalBlocks)
				.withEraseLogicalBlocks(fenixEraseLogicalBlocks)
				.withVerificationLogicalBlocks(fenixVerificationLogicalBlocks)
				.withAutonomicUrl(vadrSoftwareMetadata.getAutonomicUrl())
				.withFileName(vadrSoftwareMetadata.getFileName())
				.withFileFormat(FileFormat.APPLICATION_BINARY) // There's only one enum right now
				.withDescription(vadrSoftwareMetadata.description)
				.withUnzippedFileSize(vadrSoftwareMetadata.getUnzippedFileSize())
				.withZippedFileSize(vadrSoftwareMetadata.getZippedFileSize())
				.withVbfVersion(vadrSoftwareMetadata.vbfVersion)
				.withDataFormatIdentifier(vadrSoftwareMetadata.getDataFormatIdentifier())
				.withFrameFormat(vadrSoftwareMetadata.getFrameFormat())
				.withCallStartAddress(vadrSoftwareMetadata.getCallStartAddress()) // ?
				.withFileChecksum(vadrSoftwareMetadata.getFileChecksum())
				.withFileFingerprint(vadrSoftwareMetadata.getFileFingerprint())
				.withProgramProtected(Boolean.TRUE.booleanValue())
				.withFirmwareInstallType(vadrSoftwareMetadata.getFirmwareInstallType())
				//.withCreatedBy(???) // TODO
				//.withCreatedTimestamp(new Timestamp(AbstractEntity.getTimeKeeper().getCurrentTimeInMillis()))
				//.withLastModifiedBy("lastModifiedBy")
				//.withLastModifiedTimestamp(new Timestamp(AbstractEntity.getTimeKeeper().getCurrentTimeInMillis()))
				.withPersistentIdentity(Long.valueOf(vadrSoftwareMetadata.getId().longValue()))
				.build();

		fenixBinaryMetadata.setProgramLogicalBlocks(fenixProgramLogicalBlocks);
		fenixBinaryMetadata.setEraseLogicalBlocks(fenixEraseLogicalBlocks);
		fenixBinaryMetadata.setVerificationLogicalBlocks(fenixVerificationLogicalBlocks);

		return fenixBinaryMetadata;
	}
}
