/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.djt.cvpp.ota.vadr.client.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.djt.cvpp.ota.common.exception.EntityAlreadyExistsException;
import com.djt.cvpp.ota.common.exception.EntityDoesNotExistException;
import com.djt.cvpp.ota.vadr.client.VadrClient;
import com.djt.cvpp.ota.vadr.client.impl.model.applicationdata.ApplicationData;
import com.djt.cvpp.ota.vadr.client.impl.model.applicationdata.DomainAppDependency;
import com.djt.cvpp.ota.vadr.client.impl.model.domaindata.DomainData;
import com.djt.cvpp.ota.vadr.client.impl.model.softwaremetadata.SoftwareMetadata;
import com.djt.cvpp.ota.vadr.exception.VadrException;
import com.djt.cvpp.ota.vadr.mapper.DomainDtoMapper;
import com.djt.cvpp.ota.vadr.mapper.DomainJsonConverter;
import com.djt.cvpp.ota.vadr.model.Application;
import com.djt.cvpp.ota.vadr.model.BinaryMetadata;
import com.djt.cvpp.ota.vadr.model.BinaryMetadataContainer;
import com.djt.cvpp.ota.vadr.model.Domain;
import com.djt.cvpp.ota.vadr.model.DomainInstance;

/**
 *
 * <pre>
	 Current VADR Swagger Endpoints:
	 -------------------------------
	 https://vadr-domain-data-service-qat.apps.cl-east-dev02.cf.djt.com/swagger-ui.html
	 https://vadr-dependencymanagement-qat.apps.cl-east-dev02.cf.djt.com/swagger-ui.html#/Application_Dependency
	 https://vadr-dependencymanagement-qat.apps.cl-east-dev02.cf.djt.com/swagger-ui.html#/Domain_Dependency
	 https://vadr-applicationmanagement-qat.apps.cl-east-dev02.cf.djt.com/swagger-ui.html#/Application_Management
	 https://vadr-applicationstorage-qat.apps.cl-east-dev02.cf.djt.com/swagger-ui.html#/application-storage-controller
	 https://vadr-releasemanagement-qat.apps.cl-east-dev02.cf.djt.com/swagger-ui.html#/Release_Management


 https://domain-data-service-qat.apps.cl-east-dev02.cf.djt.com/api/softwareManagement/v1/domains?domainName=Infotainment
 https://domain-data-service-qat.apps.cl-east-dev02.cf.djt.com/api/softwareManagement/v1/domains/1/domainInstances?productionState=PRODUCTION&releaseState=RELEASED
 * 
 * </pre>
 *
 * @author tmyers1@yahoo.com (Tom Myers)
 *
 */
@Service
public class VadrRestClientImpl extends AbstractVadrRestClient implements VadrClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(VadrRestClientImpl.class);

	static String VADR_DOMAIN_DATA_WEBSERVICE_CALL_FAILED_MESSAGE = "VADR Domain data webservice call failed.";
	static String VADR_APPLICATION_DATA_WEBSERVICE_CALL_FAILED_MESSAGE = "VADR Application data webservice call failed.";
	static String VADR_SOFTWARE_METADATA_WEBSERVICE_CALL_FAILED_MESSAGE = "VADR Software Metadata webservice call failed.";
	static String VADR_DOMAIN_APPLICATION_DEPENDENCY_CALL_FAILED_MESSAGE = "VADR Domain Application Dependency webservice call failed.";
	static String DOMAIN_DATA_ARRAY_FIELD = "content";
	static final String APPLICATION_MANAGEMENT_PATH = "/api/applicationManagement/v1";
	static final String APPLICATION_STORAGE_PATH = "/api/applicationStorage/v1";
	static final String DOMAIN_APP_DEPENDENCY_PATH = "/api/dependencyManagement/v1/domain/findApplicationsByDomainInstance";
	static final String DOMAIN_DATA_PATH = "/api/softwareManagement/v1/domainInstances/search/name";

	@Autowired
	ObjectMapper mapper;

	@Value("${vadr.domain.base.url}")
	String vadrDomainDataServiceBaseUrl;

	@Value("${vadr.domain.resourceId}")
	private String vadrDomainResourceId;

	@Value("${vadr.application.management.resourceId}")
	private String vadrApplicationResourceId;

	@Value("${vadr.application.management.base.url}")
	String vadrApplicationManagementBaseUrl;

	@Value("${vadr.application.storage.base.url}")
	String vadrApplicationStorageBaseUrl;

	@Value("${vadr.application.storage.resourceId}")
	String vadrApplicationStorageResourceId;

	@Value("${vadr.dependency.management.base.url}")
	String vadrDependencyManagementBaseUrl;

	@Value("${vadr.dependency.management.resourceId}")
	String vadrDependencyManagementResourceId;
	
	private VadrObjectMapper vadrObectMapper = new VadrObjectMapper();
	private DomainJsonConverter domainJsonConverter = new DomainJsonConverter();
	private DomainDtoMapper domainDtoMapper = new DomainDtoMapper();
	

	/*
	 * (non-Javadoc)
	 * @see com.djt.cvpp.ota.vadr.client.VadrClient#retrieveDomainInstanceLineage(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)
	 */
	public Domain retrieveDomainInstanceLineage(
		String domainName,
		String domainInstanceName,
		String domainInstanceVersion,
		Integer numberOfPriorDomainInstancesToRetrieve,
		String productionState,
		String releaseState)
	throws 
		VadrException {
		
		// TODO: Clean up the exception handling/logic for the VadrRestClientCalls
		try {

			// The parent Domain is the aggregate root
			Domain fenixParentDomain = null;


			// We get all binary metadatas for all ECU Softwares and Applications for all Domain Instances at once.
			Set<BinaryMetadataContainer> allBinaryMetadataContainers = new HashSet<>();


			// Get the domain instance data.  We need to get all versions of a particular domain instance, as
			// we will need their hardware/software part numbers for manifest and signed command generation purposes,
			// as both of these artifacts need to specify the "source" revision.
			Iterator<com.djt.cvpp.ota.vadr.client.impl.model.domaindata.DomainData> vadrDomainInstanceIterator = this.getDomainData(domainInstanceName).iterator();
			while (vadrDomainInstanceIterator.hasNext()) {

				com.djt.cvpp.ota.vadr.client.impl.model.domaindata.DomainData vadrDomainInstance = vadrDomainInstanceIterator.next();

				// TODO: Change the production state to "PRODUCTION" (when VADR team creates the appropriate data)
				// TODO: Change the release state to "RELEASED" (when VADR team creates the appropriate data)
				//if (vadrDomainInstance.getReleaseState().equals("RELEASED_PENDING_UPLOAD") && vadrDomainInstance.getProductionState().equals("DEVELOPMENT")) {

				// If we haven't mapped the parent domain yet, do it now.
				if (fenixParentDomain == null) {
					fenixParentDomain = this.vadrObectMapper.mapParentDomainFromDomainInstance(vadrDomainInstance);
				}
				
				DomainInstance fenixDomainInstance;
				try {
					fenixDomainInstance = this.vadrObectMapper.mapDomainInstance(fenixParentDomain, vadrDomainInstance);
					
					// For each domain instance, we get its list of dependent application names/versions.
					Iterator<com.djt.cvpp.ota.vadr.client.impl.model.applicationdata.DomainAppDependency> domainAppDependencyIterator = this.getDomainApplicationDependencies(
							vadrDomainInstance.getDomainInstanceName(),
							vadrDomainInstance.getDomainInstanceVersion()).iterator();

					Set<Application> fenixApplications = new TreeSet<>();
					while (domainAppDependencyIterator.hasNext()) {

						com.djt.cvpp.ota.vadr.client.impl.model.applicationdata.DomainAppDependency vadrDomainAppDependency = domainAppDependencyIterator.next();
						com.djt.cvpp.ota.vadr.client.impl.model.applicationdata.ApplicationData vadrAplicationData = this.getApplicationData(
								vadrDomainAppDependency.getName(),
								vadrDomainAppDependency.getVersion());

						// Given the application name/version, get the actual application data for the given domain instance.
						Application fenixApplication = this.vadrObectMapper.mapApplication(vadrAplicationData);
						fenixApplications.add(fenixApplication);
					}
					fenixDomainInstance.addApplications(fenixApplications);
				} catch (EntityAlreadyExistsException | EntityDoesNotExistException e) {
					throw new VadrException("Unable to map VADR object model domain to FENIX object model domain, error: " + e.getMessage(), e);
				}

				// Add all the binary metadata containers for this domain instance to the master set of binary metadata containers that we retrieve all at once below
				allBinaryMetadataContainers.addAll(fenixDomainInstance.getAllBinaryMetadataContainers());
			}
			//}


			// Retrieve *ALL* the actual binary metadata info (for all domain instances) and associate them to the parent binary metadata containers in the given set, using the placeholder "blobStorageId"
			Iterator<BinaryMetadataContainer> fenixBinaryMetadataContainersIterator = allBinaryMetadataContainers.iterator();
			while (fenixBinaryMetadataContainersIterator.hasNext()) {

				BinaryMetadataContainer fenixBinaryMetadataContainer = fenixBinaryMetadataContainersIterator.next();
				Iterator<BinaryMetadata> blobStorageIdIterator = fenixBinaryMetadataContainer.getBinaryMetadatas().iterator();
				while (blobStorageIdIterator.hasNext()) {

					// Here, all we have is the "fileId" (or "blobStorageId") that is defined in the BinaryMetadata, so let's retrieve the whole thing here.
					// NOTE: We stuffed this value in the "swPartNumber" field.
					BinaryMetadata binaryMetadata = blobStorageIdIterator.next();
					com.djt.cvpp.ota.vadr.client.impl.model.softwaremetadata.SoftwareMetadata vadrSoftwareMetadata = this.getSoftwareMetadata(Long.valueOf(binaryMetadata.getSwPartNumber()));

					// TODO: Right now, as of 07-13-2018, there are EcuSoftwares (and possibly Applications) that refer to binary metadatas (by fileId/BlobStorageId) that DO NOT EXIST
					// in the application storage service.  Rather then have the VADR Rest Client blow up at the first missing binary metadata, have it return a 404 and here we will
					// simply not make the association to the parent binary metadata container and the validation of the overall graph will catch this.
					if (vadrSoftwareMetadata != null) {

						BinaryMetadata fenixBinaryMetadata = this.vadrObectMapper.mapBinaryMetadata(vadrSoftwareMetadata);
						fenixBinaryMetadataContainer.addBinaryMetadata(fenixBinaryMetadata);
					}
				}
			}



			if (fenixParentDomain == null) {
				// TODO: Make production state to be "PRODUCTION"
				throw new VadrException("No Domain Instances exist that have been RELEASED and that are in DEVELOPMENT for domainName: [" + domainName + "], domainInstanceName: [" + domainInstanceName + "].");
			}

			// TODO: Until proper exception handling is in place at the controller level, just set a list of these validation warnings to a JSON serializable field
			fenixParentDomain.setValidationWarnings();



			return fenixParentDomain;

		} catch (WebServiceException wse) {
			throw new VadrException(wse.getMessage(), wse);
		} catch (IOException ioe) {
			throw new VadrException(ioe.getMessage(), ioe);
		}
	}
	
	
	public List<DomainData> getDomainData(String instanceName) throws WebServiceException, IOException {

		//RestTemplate restTemplate = getRestTemplate(vadrDomainResourceId);
		RestTemplate restTemplate = getRestTemplate();

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(vadrDomainDataServiceBaseUrl + DOMAIN_DATA_PATH)
				.queryParam("name", instanceName).queryParam("projection", "inlineAll")
				.queryParam("releaseState", "RELEASED");
		ResponseEntity<String> responseEntity;
		String finalUrl = builder.toUriString();

		try {
			LOGGER.info("Calling url: {}", finalUrl);

			responseEntity = restTemplate.getForEntity(finalUrl, String.class);
			List<DomainData> domainDataList = extractDomainDataList(responseEntity);
			LOGGER.info("Received domain data from VADR, size={}", domainDataList.size());
			return domainDataList;
		} catch (HttpClientErrorException hcee) { // HttpStatus 4xx
			LOGGER.error("ResponseCode: {}, Error: {}", hcee.getRawStatusCode(), hcee.getResponseBodyAsString());
			throw new WebServiceException(VADR_DOMAIN_DATA_WEBSERVICE_CALL_FAILED_MESSAGE);
		} catch (HttpServerErrorException hsee) { // HttpStatus 5xx
			LOGGER.error("ResponseCode: {}, Error: {}", hsee.getRawStatusCode(), hsee.getResponseBodyAsString());
			throw new WebServiceException(VADR_DOMAIN_DATA_WEBSERVICE_CALL_FAILED_MESSAGE);
		} catch (RestClientException rce) { // HttpStatus 5xx
			LOGGER.error("RestClientException : {}", rce.getMessage());
			throw new WebServiceException(VADR_DOMAIN_DATA_WEBSERVICE_CALL_FAILED_MESSAGE);
		}
	}

	/**
	 *
	 * @param instanceName
	 * @param instanceVersion
	 * @return list of DomainAppDependency for the given domain instanceName and
	 *         instanceVersion
	 */
	public List<DomainAppDependency> getDomainApplicationDependencies(String instanceName, String instanceVersion) throws WebServiceException {
		
		//RestTemplate restTemplate = getRestTemplate(vadrDependencyManagementResourceId);
		RestTemplate restTemplate = getRestTemplate();
		
		StringBuilder builder = new StringBuilder(vadrDependencyManagementBaseUrl).append("/")
				.append(VadrRestClientImpl.DOMAIN_APP_DEPENDENCY_PATH).append("/").append(instanceName).append("/")
				.append(instanceVersion);
		String finalUrl = builder.toString();
		LOGGER.info("Calling url: {}", finalUrl);
		try {
			ResponseEntity<DomainAppDependency[]> responseEntity = restTemplate.getForEntity(finalUrl,
					DomainAppDependency[].class);
			LOGGER.info("Received Dependent Applications for the domain from VADR");
			return Arrays.asList(responseEntity.getBody());
		} catch (HttpClientErrorException hcee) { // HttpStatus 4xx
			LOGGER.error("ResponseCode: {}, Error: {}", hcee.getRawStatusCode(), hcee.getResponseBodyAsString());
			throw new WebServiceException(VADR_DOMAIN_APPLICATION_DEPENDENCY_CALL_FAILED_MESSAGE);
		} catch (HttpServerErrorException hsee) { // HttpStatus 5xx
			LOGGER.error("ResponseCode: {}, Error: {}", hsee.getRawStatusCode(), hsee.getResponseBodyAsString());
			throw new WebServiceException(VADR_DOMAIN_APPLICATION_DEPENDENCY_CALL_FAILED_MESSAGE);
		} catch (RestClientException rce) { // HttpStatus 5xx
			LOGGER.error("RestClientException : {}", rce.getMessage());
			throw new WebServiceException(VADR_DOMAIN_APPLICATION_DEPENDENCY_CALL_FAILED_MESSAGE);
		}
	}

	/**
	 *
	 * @param binaryStorageId
	 * @return Software metadata
	 * @throws WebServiceException
	 */
	public SoftwareMetadata getSoftwareMetadata(Long binaryStorageId) throws WebServiceException {
		
		//RestTemplate restTemplate = getRestTemplate(vadrApplicationStorageResourceId);
		RestTemplate restTemplate = getRestTemplate();
		
		StringBuilder builder = new StringBuilder(vadrApplicationStorageBaseUrl).append("/")
				.append(VadrRestClientImpl.APPLICATION_STORAGE_PATH).append("/").append(binaryStorageId).append("/")
				.append("parsed");
		String finalUrl = builder.toString();
		LOGGER.info("Calling url: {}", finalUrl);
		try {
			ResponseEntity<SoftwareMetadata> responseEntity = restTemplate.getForEntity(finalUrl,
					SoftwareMetadata.class);
			LOGGER.info("Received SoftwareMetadata from VADR");
			LOGGER.debug("SoftwareMetadata : {} ", responseEntity.getBody());
			return responseEntity.getBody();
		} catch (HttpClientErrorException hcee) { // HttpStatus 4xx

			// If we get a 404, then return null, as the VadrAdapter will deal with this
			// missing binary metadata in the validation of the overall graph.
			int statusCode = hcee.getRawStatusCode();
			if (statusCode == 404) {
				return null;
			}
			LOGGER.error("ResponseCode: {}, Error: {}", hcee.getRawStatusCode(), hcee.getResponseBodyAsString());
			throw new WebServiceException(VADR_SOFTWARE_METADATA_WEBSERVICE_CALL_FAILED_MESSAGE);
		} catch (HttpServerErrorException hsee) { // HttpStatus 5xx
			LOGGER.error("ResponseCode: {}, Error: {}", hsee.getRawStatusCode(), hsee.getResponseBodyAsString());
			throw new WebServiceException(VADR_SOFTWARE_METADATA_WEBSERVICE_CALL_FAILED_MESSAGE);
		} catch (RestClientException rce) { // HttpStatus 5xx
			LOGGER.error("RestClientException : {}", rce.getMessage());
			throw new WebServiceException(VADR_SOFTWARE_METADATA_WEBSERVICE_CALL_FAILED_MESSAGE);
		}
	}

	public ApplicationData getApplicationData(String applicationName, String applicationVersion) throws WebServiceException {
		
		//RestTemplate restTemplate = getRestTemplate(vadrApplicationResourceId);
		RestTemplate restTemplate = getRestTemplate();
		
		StringBuilder builder = new StringBuilder(vadrApplicationManagementBaseUrl).append("/")
				.append(VadrRestClientImpl.APPLICATION_MANAGEMENT_PATH).append("/").append(applicationName).append("/")
				.append(applicationVersion);
		String finalUrl = builder.toString();

		LOGGER.info("Calling url: {}", finalUrl);

		try {
			ResponseEntity<ApplicationData> responseEntity = restTemplate.getForEntity(finalUrl, ApplicationData.class);
			LOGGER.info("Received Application Data from VADR");
			LOGGER.debug("ApplicationData : {} ", responseEntity.getBody());
			return responseEntity.getBody();
		} catch (HttpClientErrorException hcee) { // HttpStatus 4xx
			LOGGER.error("ResponseCode: {}, Error: {}", hcee.getRawStatusCode(), hcee.getResponseBodyAsString());
			throw new WebServiceException(VADR_APPLICATION_DATA_WEBSERVICE_CALL_FAILED_MESSAGE);
		} catch (HttpServerErrorException hsee) { // HttpStatus 5xx
			LOGGER.error("ResponseCode: {}, Error: {}", hsee.getRawStatusCode(), hsee.getResponseBodyAsString());
			throw new WebServiceException(VADR_APPLICATION_DATA_WEBSERVICE_CALL_FAILED_MESSAGE);
		} catch (RestClientException rce) { // HttpStatus 5xx
			LOGGER.error("RestClientException : {}", rce.getMessage());
			throw new WebServiceException(VADR_APPLICATION_DATA_WEBSERVICE_CALL_FAILED_MESSAGE);
		}
	}

	private List<DomainData> extractDomainDataList(ResponseEntity<String> responseEntity) throws IOException {
		JsonNode responseJsonNode = mapper.readTree(responseEntity.getBody());
		JsonNode domainDataArrayNode = responseJsonNode.get(DOMAIN_DATA_ARRAY_FIELD);
		ObjectReader reader = mapper.readerFor(new TypeReference<List<DomainData>>() {
		});
		List<DomainData> domainDataList = reader.readValue(domainDataArrayNode);

		return domainDataList;
	}
	
	public DomainJsonConverter getDomainJsonConverter() {
		return domainJsonConverter;
	}

	public DomainDtoMapper getDomainDtoMapper() {
		return domainDtoMapper;
	}
}
