/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.djt.cvpp.ota.flare.client.rollout;

import java.util.List;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.djt.cvpp.ota.common.exception.EntityAlreadyExistsException;
import com.djt.cvpp.ota.common.exception.EntityDoesNotExistException;
import com.djt.cvpp.ota.common.exception.ValidationException;
import com.djt.cvpp.ota.flare.api.rollout.RolloutApplicationService;
import com.djt.cvpp.ota.flare.client.AbstractFenixFlareServiceClient;
import com.djt.cvpp.ota.flare.rollout.mapper.dto.AbstractRollout;
import com.djt.cvpp.ota.flare.rollout.mapper.dto.RolloutProgress;
import com.djt.cvpp.ota.flare.rollout.mapper.dto.VehicleCampaignBatch;
import com.djt.cvpp.ota.tmc.bytestream.exception.TmcByteStreamException;
import com.djt.cvpp.ota.tmc.deployment.exception.TmcDeploymentException;
import com.djt.cvpp.ota.tmc.vehiclediscovery.exception.TmcResourceException;
import com.djt.cvpp.ota.vadr.exception.VadrException;

/**
 *
 * @author tmyers1@yahoo.com (Tom Myers)
 *
 */
public class RolloutApplicationServiceClient extends AbstractFenixFlareServiceClient implements RolloutApplicationService {
	
	private RestTemplate restTemplate;
	
	public RolloutApplicationServiceClient() {
		
		this.restTemplate = super.restTemplate();
	}

	public AbstractRollout createSoftwareUpdateRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException {

		String uri = buildEndpointUri(ROLLOUT_URI, CREATE_SOFTWARE_UPDATE_ROLLOUT_URI);
		uri =  UriComponentsBuilder
			.fromHttpUrl(uri)
			.queryParam(ROLLOUT_NAME, rolloutName)
			.queryParam(DELIVERY_RULE_SET_NAME, deliveryRuleSetName)
			.toUriString();
		
		return this.restTemplate.getForObject(uri, com.djt.cvpp.ota.flare.rollout.mapper.dto.AbstractRollout.class);
	}

	public AbstractRollout createApplicationUpdateRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		throw new RuntimeException("Not implemented yet.");
	}

	public AbstractRollout createAddApplicationRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		throw new RuntimeException("Not implemented yet.");
	}

	public AbstractRollout createRemoveApplicationRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		throw new RuntimeException("Not implemented yet.");
	}

	public AbstractRollout createSuspendSoftwareRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		throw new RuntimeException("Not implemented yet.");
	}
	
	public AbstractRollout createSoftwareWithDirectConfigurationRollout(
		String rolloutName,
		String deliveryRuleSetName,
		String nodeAcronym,
		String nodeAddress)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		throw new RuntimeException("Not implemented yet.");
	}

	public AbstractRollout createDirectConfigurationOnlyRollout(
		String rolloutName,
		String nodeAcronym,
		String nodeAddress)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		throw new RuntimeException("Not implemented yet.");
	}
	
	public AbstractRollout createOdlUpdateRollout(
		String rolloutName,
		String programCode,
		Integer modelYear,
		String regionCode)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		throw new RuntimeException("Not implemented yet.");
	}

	public AbstractRollout createPolicyTableUpdateRollout(
		String rolloutName,
		String programCode,
		Integer modelYear,
		String regionCode)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		throw new RuntimeException("Not implemented yet.");
	}

	public List<AbstractRollout> getAllRollouts() {
		
		throw new RuntimeException("Not implemented yet.");
	}
	
	public AbstractRollout getRolloutByName(
        String rolloutName)
	throws 
		EntityDoesNotExistException {
		
		String uri = buildEndpointUri(ROLLOUT_URI, GET_ROLLOUT_BY_NAME_URI);
		uri =  UriComponentsBuilder
			.fromHttpUrl(uri)
			.queryParam(ROLLOUT_NAME, rolloutName)
			.toUriString();
		
		return this.restTemplate.getForObject(uri, com.djt.cvpp.ota.flare.rollout.mapper.dto.AbstractRollout.class);
	}
		
	public void performPreProcessing(
		String rolloutName) 
	throws
		EntityDoesNotExistException,
		ValidationException,
		VadrException,
		TmcResourceException,
		TmcDeploymentException {
		
		throw new RuntimeException("Not implemented yet.");
	}
	
	public void uploadGeneratedContentToTmcByteStream(
		String rolloutName)
	throws
		EntityDoesNotExistException,
		TmcByteStreamException,
		ValidationException {
		
		throw new RuntimeException("Not implemented yet.");
	}

	public void initiateRollout(
		String rolloutName,
		String initiatedBy) 
	throws 
		EntityDoesNotExistException, 
		ValidationException {
		
		throw new RuntimeException("Not implemented yet.");
	}

	public void evaluateRolloutState(
		String rolloutName) 
	throws 
		EntityDoesNotExistException, 
		ValidationException,
		TmcDeploymentException {
		
		throw new RuntimeException("Not implemented yet.");
	}
	
	public void pauseRollout(
		String rolloutName) 
	throws 
		EntityDoesNotExistException, 
		ValidationException {
		
		throw new RuntimeException("Not implemented yet.");
	}

	public void resumeRollout(
		String rolloutName) 
	throws 
		EntityDoesNotExistException, 
		ValidationException {
		
		throw new RuntimeException("Not implemented yet.");
	}

	public void cancelRollout(
		String rolloutName) 
	throws 
		EntityDoesNotExistException {
		
		throw new RuntimeException("Not implemented yet.");
	}
	
	public RolloutProgress getRolloutProgress(
		String rolloutName)
	throws 
		EntityDoesNotExistException {
		
		throw new RuntimeException("Not implemented yet.");
	}
	
	public VehicleCampaignBatch getVehicleCampaignBatchByVin(
		String rolloutName,
		String vin)
	throws 
		EntityDoesNotExistException {
		
		throw new RuntimeException("Not implemented yet.");
	}
	
	public VehicleCampaignBatch getVehicleCampaignBatchByTmcIds(
		String rolloutName,
		String tmcDeploymentId,
		String tmcVehicleId)
	throws 
		EntityDoesNotExistException {
		
		throw new RuntimeException("Not implemented yet.");
	}
	
	public RolloutProgress runRolloutSimulation(
		String templateVin,
		String numberToProvision,
		double expectedPercentageSuccess,
		double expectedPercentageFailure,
		double expectedPercentageOffline,
		String rolloutName,
		String owner,
		String initiatedBy,
		String deliveryRuleSetName) 
	throws 
		EntityDoesNotExistException,
		EntityAlreadyExistsException,
		ValidationException,
		TmcByteStreamException,
		VadrException,
		TmcResourceException,
		TmcDeploymentException {
		
		throw new RuntimeException("Not implemented yet.");
	}
}
