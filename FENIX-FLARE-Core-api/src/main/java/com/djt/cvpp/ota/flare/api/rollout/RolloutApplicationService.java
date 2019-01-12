/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.djt.cvpp.ota.flare.api.rollout;

import java.util.List;

import com.djt.cvpp.ota.common.exception.EntityAlreadyExistsException;
import com.djt.cvpp.ota.common.exception.EntityDoesNotExistException;
import com.djt.cvpp.ota.common.exception.ValidationException;
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
public interface RolloutApplicationService {
	
	String ROLLOUT_URI = "/api/v1/rollout";
	
	String ROLLOUT_NAME = "rolloutName";
	String DELIVERY_RULE_SET_NAME = "deliveryRuleSetName";
	
	
	/**
	 * 
	 * @param rolloutName The unique name of the rollout to create, specified by <code>rolloutName</code>
	 * @param deliveryRuleSetName The name of the delivery rule set which contains the software to be delivered, with the delivery rules/constraints 
	 * 
	 * @return The newly created rollout
	 * 
	 * @throws EntityAlreadyExistsException FLARE-ROLLOUT-1001: Could not create rollout: [rolloutName] because it already exists
	 * @throws EntityDoesNotExistException FLARE-ROLLOUT-1002: Could not create rollout: [rolloutName] because rollout action with UUID: [rolloutActionUUID] does not exist
	 * @throws ValidationException FLARE-ROLLOUT-1003: Could not create rollout because attribute: [attributeName] was invalid for reason: [reason] 
	 */
	String CREATE_SOFTWARE_UPDATE_ROLLOUT_URI = "/createSoftwareUpdateRollout";
	AbstractRollout createSoftwareUpdateRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException;

	/**
	 * 
	 * @param rolloutName
	 * @param deliveryRuleSetName
	 * @return
	 * @throws EntityAlreadyExistsException
	 * @throws ValidationException
	 */
	String CREATE_APPLICATION_UPDATE_ROLLOUT_URI = "/createApplicationUpdateRollout";
	AbstractRollout createApplicationUpdateRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException;

	/**
	 * 
	 * @param rolloutName
	 * @param deliveryRuleSetName
	 * @return
	 * @throws EntityAlreadyExistsException
	 * @throws ValidationException
	 */
	String CREATE_ADD_APPLICATION_ROLLOUT_URI = "/createAddApplicationRollout";
	AbstractRollout createAddApplicationRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException;

	/**
	 * 
	 * @param rolloutName
	 * @param deliveryRuleSetName
	 * @return
	 * @throws EntityAlreadyExistsException
	 * @throws ValidationException
	 */
	String CREATE_REMOVE_APPLICATION_UPDATE_ROLLOUT_URI = "/createRemoveApplicationRollout";
	AbstractRollout createRemoveApplicationRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException;

	/**
	 * 
	 * @param rolloutName
	 * @param deliveryRuleSetName
	 * @return
	 * @throws EntityAlreadyExistsException
	 * @throws ValidationException
	 */
	String CREATE_SUSPEND_SOFTWARE_ROLLOUT_URI = "/createSuspendSoftwareRollout";
	AbstractRollout createSuspendSoftwareRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException;
	
	/**
	 * 
	 * @param rolloutName
	 * @param deliveryRuleSetName
	 * @param nodeAcronym
	 * @param nodeAddress
	 * @return
	 * @throws EntityAlreadyExistsException
	 * @throws ValidationException
	 */
	String CREATE_SOFTWARE_WITH_DIRECT_CONFIGURATION_ROLLOUT_URI = "/createSoftwareWithDirectConfigurationRollout";
	AbstractRollout createSoftwareWithDirectConfigurationRollout(
		String rolloutName,
		String deliveryRuleSetName,
		String nodeAcronym,
		String nodeAddress)
	throws 
		EntityAlreadyExistsException,
		ValidationException;

	/**
	 * 
	 * @param rolloutName
	 * @param nodeAcronym
	 * @param nodeAddress
	 * @return
	 * @throws EntityAlreadyExistsException
	 * @throws ValidationException
	 */
	String CREATE_DIRECT_CONFIGURATION_ONLY_ROLLOUT_URI = "/createDirectConfigurationOnlyRollout";
	AbstractRollout createDirectConfigurationOnlyRollout(
		String rolloutName,
		String nodeAcronym,
		String nodeAddress)
	throws 
		EntityAlreadyExistsException,
		ValidationException;
	
	/**
	 * 
	 * @param rolloutName
	 * @param programCode
	 * @param modelYear
	 * @param regionCode
	 * @return
	 * @throws EntityAlreadyExistsException
	 * @throws ValidationException
	 */
	String CREATE_ODL_UPDATE_ROLLOUT_URI = "/createOdlUpdateRollout";
	AbstractRollout createOdlUpdateRollout(
		String rolloutName,
		String programCode,
		Integer modelYear,
		String regionCode)
	throws 
		EntityAlreadyExistsException,
		ValidationException;

	/**
	 * 
	 * @param rolloutName
	 * @param programCode
	 * @param modelYear
	 * @param regionCode
	 * @return
	 * @throws EntityAlreadyExistsException
	 * @throws ValidationException
	 */
	String CREATE_POLICY_TABLE_UPDATE_ROLLOUT_URI = "/createPolicyTableUpdateRollout";
	AbstractRollout createPolicyTableUpdateRollout(
		String rolloutName,
		String programCode,
		Integer modelYear,
		String regionCode)
	throws 
		EntityAlreadyExistsException,
		ValidationException;

	
	/**
	 * 
	 * @return
	 */
	String GET_ALL_ROLLOUTS_URI = "/getAllRollouts";
	List<AbstractRollout> getAllRollouts();
	
	
	/**
	 * 
	 * @param rolloutName The unique name of the rollout
	 * 
	 * @return The requested rollout
	 * 
	 * @throws EntityDoesNotExistException FLARE-ROLLOUT-1004: Could not retrieve rollout: [rolloutName] because it does not exist
	 */
	String GET_ROLLOUT_BY_NAME_URI = "/getRolloutByName";
	AbstractRollout getRolloutByName(
        String rolloutName)
	throws 
		EntityDoesNotExistException;
	
	
	/**
	 * 
	 * @param rolloutName
	 * 
	 * @throws EntityDoesNotExistException FLARE-ROLLOUT-1005: Could not perform pre-processing on rollout: [rolloutName] because it does not exist
	 * @throws ValidationException FLARE-ROLLOUT-1006: Could not perform pre-processing on rollout: [rolloutName] because attribute: [attributeName] was invalid for reason: [reason]
	 */
	String PERFORM_PRE_PROCESSING = "/performPreProcessing";
	void performPreProcessing(
		String rolloutName) 
	throws
		EntityDoesNotExistException,
		ValidationException,
		VadrException,
		TmcResourceException,
		TmcDeploymentException;


	/**
	 * Generated Content can be one of the following:
	 * <ul>
	 *    <li>Generic Manifests (campaign level)</li>
	 *    <li>Signed Commands (vehicle level)</li>
	 *    <li>OTA Policy Tables (program/region level)</li>
	 *    <li>ODL Updates (of program level)</li>
	 *  </ul>  
	 * 
	 * @param rolloutName The unique name of the rollout
	 * 
	 * @throws EntityDoesNotExistException FLARE-ROLLOUT-1007: Could not upload generated content for rollout: [rolloutName] because it does not exist
	 * @throws TmcByteStreamException FLARE-ROLLOUT-1008: Could not upload generated content for rollout: [rolloutName] because of TMC Byte Stream error: [tmcByteStreamException]
	 */
	String UPLOAD_GENERATED_CONTENT_TO_TMC_BYTE_STREAM = "/uploadGeneratedContentToTmcByteStream";
	void uploadGeneratedContentToTmcByteStream(
		String rolloutName)
	throws
		EntityDoesNotExistException,
		TmcByteStreamException,
		ValidationException;

	/**
	 * 
	 * @param rolloutName
	 * @param initiatedBy
	 * 
	 * @throws EntityDoesNotExistException FLARE-ROLLOUT-1009: Could not initiate rollout: [rolloutName] because it does not exist
	 * @throws ValidationException FLARE-ROLLOUT-1010: Could not initiate rollout: [rolloutName] because attribute: [attributeName] was invalid for reason: [reason]
	 */
	String INITIATE_ROLLOUT = "/initiateRollout";
	void initiateRollout(
		String rolloutName,
		String initiatedBy) 
	throws 
		EntityDoesNotExistException, 
		ValidationException;

	/**
	 * 
	 * @param rolloutName The unique name of the rollout
	 * 
	 * @throws EntityDoesNotExistException FLARE-ROLLOUT-1011: Could not evaluate state for rollout: [rolloutName] because it does not exist
	 * @throws ValidationException FLARE-ROLLOUT-1012: Could not evaluate state for rollout: [rolloutName] because attribute: [attributeName] was invalid for reason: [reason]
	 */
	String EVALUATE_ROLLOUT_STATE = "/evaluateRolloutState";
	void evaluateRolloutState(
		String rolloutName) 
	throws 
		EntityDoesNotExistException, 
		ValidationException,
		TmcDeploymentException;
	
	/**
	 * 
	 * @param rolloutName The unique name of the rollout
	 * 
	 * @throws EntityDoesNotExistException FLARE-ROLLOUT-1013: Could not pause rollout: [rolloutName] because it does not exist
	 * @throws ValidationException FLARE-ROLLOUT-1014: Could not pause rollout: [rolloutName] because attribute: [attributeName] was invalid for reason: [reason]
	 */
	String PAUSE_ROLLOUT = "/pauseRollout";
	void pauseRollout(
		String rolloutName) 
	throws 
		EntityDoesNotExistException, 
		ValidationException;

	/**
	 * 
	 * @param rolloutName The unique name of the rollout
	 * 
	 * @throws EntityDoesNotExistException FLARE-ROLLOUT-1015: Could not resume rollout: [rolloutName] because it does not exist
	 * @throws ValidationException FLARE-ROLLOUT-1016: Could not resume rollout: [rolloutName] because attribute: [attributeName] was invalid for reason: [reason]
	 */
	String RESUME_ROLLOUT = "/resumeRollout";
	void resumeRollout(
		String rolloutName) 
	throws 
		EntityDoesNotExistException, 
		ValidationException;

	/**
	 * 
	 * @param rolloutName The unique name of the rollout
	 * 
	 * @throws EntityDoesNotExistException FLARE-ROLLOUT-1017: Could not cancel rollout: [rolloutName] because it does not exist
	 */
	String CANCEL_ROLLOUT = "/cancelRollout";
	void cancelRollout(
		String rolloutName) 
	throws 
		EntityDoesNotExistException;

	/**
	 * 
	 * @param rolloutName
	 * @return
	 * @throws EntityDoesNotExistException
	 */
	String GET_ROLLOUT_PROGRESS = "/getRolloutProgress";
	RolloutProgress getRolloutProgress(
		String rolloutName)
	throws 
		EntityDoesNotExistException;

	/**
	 * 
	 * @param rolloutName
	 * @param vin
	 * @return
	 * @throws EntityDoesNotExistException
	 */
	String GET_VEHICLE_CAMPAIGN_BATCH_BY_VIN = "/getVehicleCampaignBatchByVin";
	VehicleCampaignBatch getVehicleCampaignBatchByVin(
		String rolloutName,
		String vin)
	throws 
		EntityDoesNotExistException;

	/**
	 * 
	 * @param rolloutName
	 * @param tmcDeploymentId
	 * @param tmcVehicleId
	 * @return
	 * @throws EntityDoesNotExistException
	 */
	String GET_VEHICLE_CAMPAIGN_BATCH_BY_TMC_IDS = "/getVehicleCampaignBatchByTmcIds";
	VehicleCampaignBatch getVehicleCampaignBatchByTmcIds(
		String rolloutName,
		String tmcDeploymentId,
		String tmcVehicleId)
	throws 
		EntityDoesNotExistException;
	
	/**
	 * 
	 * <ol>
	 *    <li> Sets the test time keeper implementation
	 *    <li> Goes into a loop that advances the clock by one day
	 *    <li> Calls the 'evaluateRolloutState()' method
	 *    <li> Ends when the endDate of the Rollout is reached (or all vehicles complete, meaning that the rollout is complete)
	 *  </ol>  
	 * 
	 * @param templateVin
	 * @param numberToProvision
	 * @param expectedPercentageSuccess
	 * @param expectedPercentageFailure
	 * @param expectedPercentageOffline
	 * @param rolloutName
	 * @param owner
	 * @param initiatedBy
	 * @param deliveryRuleSetName
	 * 
	 * @return
	 * 
	 * @throws EntityDoesNotExistException
	 * @throws EntityAlreadyExistsException
	 * @throws ValidationException
	 * @throws TmcByteStreamException
	 */
	String RUN_ROLLOUT_SIMULATION = "/runRolloutSimulation";
	RolloutProgress runRolloutSimulation(
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
		TmcDeploymentException;
}
