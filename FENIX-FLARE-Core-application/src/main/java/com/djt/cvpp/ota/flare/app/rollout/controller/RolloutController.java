/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.djt.cvpp.ota.flare.app.rollout.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.djt.cvpp.ota.common.exception.EntityAlreadyExistsException;
import com.djt.cvpp.ota.common.exception.EntityDoesNotExistException;
import com.djt.cvpp.ota.common.exception.FenixRuntimeException;
import com.djt.cvpp.ota.common.exception.ValidationException;
import com.djt.cvpp.ota.common.model.AbstractEntity;
import com.djt.cvpp.ota.common.timekeeper.TimeKeeper;
import com.djt.cvpp.ota.common.timekeeper.impl.TestTimeKeeperImpl;
import com.djt.cvpp.ota.flare.api.rollout.RolloutApplicationService;
import com.djt.cvpp.ota.flare.rollout.mapper.dto.AbstractRollout;
import com.djt.cvpp.ota.flare.rollout.mapper.dto.RolloutProgress;
import com.djt.cvpp.ota.flare.rollout.mapper.dto.VehicleCampaignBatch;
import com.djt.cvpp.ota.flare.rollout.model.enums.RolloutState;
import com.djt.cvpp.ota.flare.rollout.repository.RolloutRepository;
import com.djt.cvpp.ota.flare.rollout.repository.impl.MockRolloutRepositoryImpl;
import com.djt.cvpp.ota.flare.rollout.service.RolloutService;
import com.djt.cvpp.ota.flare.rollout.service.impl.RolloutServiceImpl;
import com.djt.cvpp.ota.flare.vehiclestatustemplate.service.VehicleStatusMessageTemplateService;
import com.djt.cvpp.ota.flare.vehiclestatustemplate.service.impl.VehicleStatusMessageTemplateServiceImpl;
import com.djt.cvpp.ota.orfin.client.delivery.DeliveryRuleSetApplicationServiceClient;
import com.djt.cvpp.ota.orfin.delivery.event.OrfinDeliveryRuleSetEventPublisher;
import com.djt.cvpp.ota.orfin.delivery.event.impl.MockOrfinDeliveryRuleSetEventPublisher;
import com.djt.cvpp.ota.tmc.bytestream.client.TmcByteStreamClient;
import com.djt.cvpp.ota.tmc.bytestream.client.impl.MockTmcByteStreamClientImpl;
import com.djt.cvpp.ota.tmc.bytestream.exception.TmcByteStreamException;
import com.djt.cvpp.ota.tmc.deployment.client.TmcDeploymentClient;
import com.djt.cvpp.ota.tmc.deployment.client.impl.MockTmcDeploymentClientImpl;
import com.djt.cvpp.ota.tmc.deployment.exception.TmcDeploymentException;
import com.djt.cvpp.ota.tmc.deployment.simulator.TmcVehicleSimulator;
import com.djt.cvpp.ota.tmc.vehiclediscovery.client.TmcVehicleClient;
import com.djt.cvpp.ota.tmc.vehiclediscovery.client.impl.MockTmcVehicleClientImpl;
import com.djt.cvpp.ota.tmc.vehiclediscovery.exception.TmcResourceException;
import com.djt.cvpp.ota.tmc.vehiclediscovery.model.TmcVehicle;
import com.djt.cvpp.ota.tmc.vehiclestatus.event.TmcVehicleStatusMessageEventPublisher;
import com.djt.cvpp.ota.tmc.vehiclestatus.event.impl.MockTmcVehicleStatusMessageEventPublisher;
import com.djt.cvpp.ota.vadr.client.VadrClient;
import com.djt.cvpp.ota.vadr.client.impl.MockVadrClientImpl;
import com.djt.cvpp.ota.vadr.exception.VadrException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 *
 * @author tmyers1@yahoo.com (Tom Myers)
 *
 */
@RestController
@RequestMapping(path = RolloutApplicationService.ROLLOUT_URI)
public class RolloutController implements RolloutApplicationService, Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RolloutController.class);


	// ORFIN dependencies
	@Autowired
	private DeliveryRuleSetApplicationServiceClient deliveryRuleSetApplicationServiceClient;
	private OrfinDeliveryRuleSetEventPublisher orfinDeliveryRuleSetEventPublisher;
	
	
	

	
	// VADR depdencies
	private VadrClient vadrClient;

	
	// TMC Dependencies
	private TmcVehicleClient tmcVehicleClient;
	private TmcByteStreamClient tmcByteStreamClient;
	private TmcDeploymentClient tmcDeploymentClient;
	
	private TmcVehicleStatusMessageEventPublisher tmcVehicleStatusMessageEventPublisher;

	
	// FLARE dependencies
	private VehicleStatusMessageTemplateService vehicleStatusMessageTemplateService;
	private RolloutRepository rolloutRepository;
	
	private RolloutService rolloutService;
	
	public RolloutController() {
		
		// ORFIN dependencies
		orfinDeliveryRuleSetEventPublisher = MockOrfinDeliveryRuleSetEventPublisher.getInstance();


		
		
		// VADR dependencies
		vadrClient = new MockVadrClientImpl();
		
		
		// TMC Dependencies
		tmcByteStreamClient = new MockTmcByteStreamClientImpl();
		tmcVehicleClient = new MockTmcVehicleClientImpl();
		tmcDeploymentClient = new MockTmcDeploymentClientImpl();
		
		
		// FLARE depdendencies
		vehicleStatusMessageTemplateService = new VehicleStatusMessageTemplateServiceImpl();
		
		rolloutRepository = new MockRolloutRepositoryImpl();
		rolloutService = new RolloutServiceImpl(
			rolloutRepository,
			vehicleStatusMessageTemplateService,
			vadrClient,
			tmcVehicleClient,
			tmcByteStreamClient,
			tmcDeploymentClient);

		
		// To simulate external event publishers. The real implementations will be the actual external systems communicating events over some event bus.
		orfinDeliveryRuleSetEventPublisher = MockOrfinDeliveryRuleSetEventPublisher.getInstance();
		tmcVehicleStatusMessageEventPublisher = MockTmcVehicleStatusMessageEventPublisher.getInstance();
		
		orfinDeliveryRuleSetEventPublisher.subscribe(this.rolloutService);
		tmcVehicleStatusMessageEventPublisher.subscribe(this.rolloutService);
		
		
		// TODO: TDM: Add Quartz and have the following be in a timer that runs, say every minute.
		Thread rolloutTimerThread = new Thread(this, "Rollout Timer");
		rolloutTimerThread.start();
	}
	
	@Override
	public void run() {
		
		while (true) {
			
			// Evaluate the rollout state for any active rollouts.
			Iterator<com.djt.cvpp.ota.flare.rollout.model.AbstractRollout> iterator = this.rolloutService.getAllRollouts().iterator();
			while (iterator.hasNext()) {

				com.djt.cvpp.ota.flare.rollout.model.AbstractRollout rollout = iterator.next();
				
				String rolloutName = rollout.getRolloutName();
				com.djt.cvpp.ota.flare.rollout.model.enums.RolloutState rolloutState = rollout.getRolloutState();
				
				// TODO: TDM: Anything type specific for a rollout needs to be put in an appropriate method in the subclass.
				
				switch(rolloutState) {
					case PRE_ROLLOUT_INVALID:
						com.djt.cvpp.ota.orfin.delivery.mapper.dto.DeliveryRuleSet deliveryRuleSetDto;
						try {
							String deliveryRuleSetName = ((com.djt.cvpp.ota.flare.rollout.model.SoftwareUpdateRollout)rollout).getDeliveryRuleSetName();
							LOGGER.info(rollout.getClassAndIdentity() + " -deliveryRuleSetName: " + deliveryRuleSetName);
							deliveryRuleSetDto = this.deliveryRuleSetApplicationServiceClient.getDeliveryRuleSetByName(deliveryRuleSetName);
							this.rolloutService.performPreProcessing(rolloutName, deliveryRuleSetDto);
							this.rolloutService.evaluateRolloutState(rolloutName);
						} catch (EntityDoesNotExistException | ValidationException | VadrException | TmcResourceException | TmcDeploymentException e) {
							throw new FenixRuntimeException("Unable to perform pre-processing for rollout:" + rollout + ", error: " + e.getMessage(), e);
						}
						break;
						
					case PRE_ROLLOUT_VALID:
						try {
							this.rolloutService.uploadGeneratedContentToTmcByteStream(rolloutName);
							this.rolloutService.evaluateRolloutState(rolloutName);
						} catch (TmcByteStreamException | EntityDoesNotExistException | ValidationException | TmcDeploymentException e) {
							throw new FenixRuntimeException("Unable to upload generated content to TMC bytes tream for rollout:" + rollout + ", error: " + e.getMessage(), e);
						}
						break;

					case RELEASE_ARTIFACT_UPLOAD_PENDING:
						try {
							this.rolloutService.evaluateRolloutState(rolloutName);
						} catch (EntityDoesNotExistException | ValidationException | TmcDeploymentException e) {
							throw new FenixRuntimeException("Unable to upload evaluate rollout (in upload pending state):" + rollout + ", error: " + e.getMessage(), e);
						}
						break;

					case PENDING:
						try {
							this.rolloutService.initiateRollout(rolloutName, "Digital Jukebox Technologies. LLC.");
							this.rolloutService.evaluateRolloutState(rolloutName);
						} catch (EntityDoesNotExistException | ValidationException | TmcDeploymentException e) {
							throw new FenixRuntimeException("Unable to upload initiate rollout:" + rollout + ", error: " + e.getMessage(), e);
						}
						break;
						
					case DEPLOYING:
						try {
							this.rolloutService.evaluateRolloutState(rolloutName);
						} catch (EntityDoesNotExistException | ValidationException | TmcDeploymentException e) {
							throw new FenixRuntimeException("Unable to upload evaluate rollout (in deploying state):" + rollout + ", error: " + e.getMessage(), e);
						}
						break;
						
					case FINISHED:
						break;
						
					case PAUSED:
						try {
							this.rolloutService.evaluateRolloutState(rolloutName);
						} catch (EntityDoesNotExistException | ValidationException | TmcDeploymentException e) {
							throw new FenixRuntimeException("Unable to upload evaluate rollout (in paused state):" + rollout + ", error: " + e.getMessage(), e);
						}
						break;
						
					default:
						break;
				}
			}
			
			// Sleep for 30 seconds
			try {
				Thread.sleep(30000);
			} catch (InterruptedException ie) {
				throw new FenixRuntimeException("Unable to sleep for 30 seconds, error: [" + ie.getMessage() + "].", ie);
			}
		}				
	}

	
	// ************************************************************************************************************************************
	// CONTROLLER METHODS MAP TO SERVICE METHODS ONE-TO-ONE.  ALL SWAGGER DOC INFO COMES FROM JAVADOC IN SERVICE INTERFACE.
	// ************************************************************************************************************************************

	
	@ApiOperation(value = CREATE_SOFTWARE_UPDATE_ROLLOUT_URI, notes = "Step 1: Create rollout")
	@GetMapping(CREATE_SOFTWARE_UPDATE_ROLLOUT_URI)
	public AbstractRollout createSoftwareUpdateRollout(
		@ApiParam(ROLLOUT_NAME)
		@RequestParam
		String rolloutName,

		@ApiParam(DELIVERY_RULE_SET_NAME)
		@RequestParam
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException {

		
		return this.rolloutService.getDtoMapper().mapEntityToDto(this.rolloutService.createSoftwareUpdateRollout(rolloutName, deliveryRuleSetName));
	}

	
	@ApiOperation(value = CREATE_APPLICATION_UPDATE_ROLLOUT_URI, notes = "Create rollout")
	@GetMapping(CREATE_APPLICATION_UPDATE_ROLLOUT_URI)
	public AbstractRollout createApplicationUpdateRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		return this.rolloutService.getDtoMapper().mapEntityToDto(this.rolloutService.createApplicationUpdateRollout(rolloutName, deliveryRuleSetName));
	}

	@ApiOperation(value = CREATE_ADD_APPLICATION_ROLLOUT_URI, notes = "Create rollout")
	@GetMapping(CREATE_ADD_APPLICATION_ROLLOUT_URI)
	public AbstractRollout createAddApplicationRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		return this.rolloutService.getDtoMapper().mapEntityToDto(this.rolloutService.createAddApplicationRollout(rolloutName, deliveryRuleSetName));
	}

	@ApiOperation(value = CREATE_REMOVE_APPLICATION_UPDATE_ROLLOUT_URI, notes = "Create rollout")
	@GetMapping(CREATE_REMOVE_APPLICATION_UPDATE_ROLLOUT_URI)
	public AbstractRollout createRemoveApplicationRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		return this.rolloutService.getDtoMapper().mapEntityToDto(this.rolloutService.createRemoveApplicationRollout(rolloutName, deliveryRuleSetName));
	}

	@ApiOperation(value = CREATE_SUSPEND_SOFTWARE_ROLLOUT_URI, notes = "Suspend software rollout")
	@GetMapping(CREATE_SUSPEND_SOFTWARE_ROLLOUT_URI)
	public AbstractRollout createSuspendSoftwareRollout(
		String rolloutName,
		String deliveryRuleSetName)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		return this.rolloutService.getDtoMapper().mapEntityToDto(this.rolloutService.createSuspendSoftwareRollout(rolloutName, deliveryRuleSetName));
	}

	@ApiOperation(value = CREATE_SOFTWARE_WITH_DIRECT_CONFIGURATION_ROLLOUT_URI, notes = "Create software with direct configuration rollout")
	@GetMapping(CREATE_SOFTWARE_WITH_DIRECT_CONFIGURATION_ROLLOUT_URI)
	public AbstractRollout createSoftwareWithDirectConfigurationRollout(
		String rolloutName,
		String deliveryRuleSetName,
		String nodeAcronym,
		String nodeAddress)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		return this.rolloutService.getDtoMapper().mapEntityToDto(this.rolloutService.createSoftwareWithDirectConfigurationRollout(rolloutName, deliveryRuleSetName, nodeAcronym, nodeAddress));
	}

	@ApiOperation(value = CREATE_DIRECT_CONFIGURATION_ONLY_ROLLOUT_URI, notes = "Create direct configuration only rollout")
	@GetMapping(CREATE_DIRECT_CONFIGURATION_ONLY_ROLLOUT_URI)
	public AbstractRollout createDirectConfigurationOnlyRollout(
		String rolloutName,
		String nodeAcronym,
		String nodeAddress)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		return this.rolloutService.getDtoMapper().mapEntityToDto(this.rolloutService.createDirectConfigurationOnlyRollout(rolloutName, nodeAcronym, nodeAddress));
	}

	@ApiOperation(value = CREATE_ODL_UPDATE_ROLLOUT_URI, notes = "Create ODL rollout")
	@GetMapping(CREATE_ODL_UPDATE_ROLLOUT_URI)
	public AbstractRollout createOdlUpdateRollout(
		String rolloutName,
		String programCode,
		Integer modelYear,
		String regionCode)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		return this.rolloutService.getDtoMapper().mapEntityToDto(this.rolloutService.createOdlUpdateRollout(rolloutName, programCode, modelYear, regionCode));
	}

	@ApiOperation(value = CREATE_POLICY_TABLE_UPDATE_ROLLOUT_URI, notes = "Create policy table rollout")
	@GetMapping(CREATE_POLICY_TABLE_UPDATE_ROLLOUT_URI)
	public AbstractRollout createPolicyTableUpdateRollout(
		String rolloutName,
		String programCode,
		Integer modelYear,
		String regionCode)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		
		return this.rolloutService.getDtoMapper().mapEntityToDto(this.rolloutService.createPolicyTableUpdateRollout(rolloutName, programCode, modelYear, regionCode));
	}
	
	@ApiOperation(value = GET_ALL_ROLLOUTS_URI, notes = "Retrieves all rollouts")
	@GetMapping(GET_ALL_ROLLOUTS_URI)
	public List<AbstractRollout> getAllRollouts() {
		
		List<AbstractRollout> list = new ArrayList<>();
		Iterator<com.djt.cvpp.ota.flare.rollout.model.AbstractRollout> iterator = this.rolloutService.getAllRollouts().iterator();
		while (iterator.hasNext()) {
			list.add(this.rolloutService.getDtoMapper().mapEntityToDto(iterator.next()));
		}
		return list;
	}

	@ApiOperation(value = GET_ROLLOUT_BY_NAME_URI, notes = "Retrieves the specified rollout")
	@GetMapping(GET_ROLLOUT_BY_NAME_URI)
	public AbstractRollout getRolloutByName(
		@ApiParam(ROLLOUT_NAME)
		@RequestParam
        String rolloutName)
	throws 
		EntityDoesNotExistException {

		com.djt.cvpp.ota.flare.rollout.model.AbstractRollout rolloutEntity = this.rolloutService.getRolloutByName(rolloutName);
		return this.rolloutService.getDtoMapper().mapEntityToDto(rolloutEntity);
	}

	@ApiOperation(value = PERFORM_PRE_PROCESSING, notes = "STEP 2: Performs pre-processing, or vehicle discovery and campaign, campaign batch and vehicle campaign batch entity creation, for the specified rollout")
	@GetMapping(PERFORM_PRE_PROCESSING)
	public void performPreProcessing(
		@ApiParam("rolloutName")
		@RequestParam
		String rolloutName) 
	throws
		EntityDoesNotExistException,
		ValidationException,
		VadrException, 
		TmcResourceException {
		
		com.djt.cvpp.ota.flare.rollout.model.AbstractRollout rolloutEntity = this.rolloutService.getRolloutByName(rolloutName);
		
		// TODO: TDM: Fix
		if (rolloutEntity instanceof com.djt.cvpp.ota.flare.rollout.model.SoftwareUpdateRollout) {
			
			String deliveryRuleSetName = ((com.djt.cvpp.ota.flare.rollout.model.SoftwareUpdateRollout)rolloutEntity).getDeliveryRuleSetName();
			com.djt.cvpp.ota.orfin.delivery.mapper.dto.DeliveryRuleSet deliveryRuleSetDto = this.deliveryRuleSetApplicationServiceClient.getDeliveryRuleSetByName(deliveryRuleSetName);
			
			this.rolloutService.performPreProcessing(rolloutName, deliveryRuleSetDto);
		}
	}

	@ApiOperation(value = UPLOAD_GENERATED_CONTENT_TO_TMC_BYTE_STREAM, notes = "STEP 3: Uploads generated generic manifests and vehicle signed commands for the specified rollout")
	@GetMapping(UPLOAD_GENERATED_CONTENT_TO_TMC_BYTE_STREAM)
	public void uploadGeneratedContentToTmcByteStream(
		@ApiParam("rolloutName")
		@RequestParam
		String rolloutName)
	throws
		EntityDoesNotExistException,
		TmcByteStreamException,
		ValidationException {
		
		this.rolloutService.uploadGeneratedContentToTmcByteStream(rolloutName);
	}

	@ApiOperation(value = INITIATE_ROLLOUT, notes = "STEP 4: Initiates, or deploys, the specified rollout")
	@GetMapping(INITIATE_ROLLOUT)
	public void initiateRollout(
		@ApiParam("rolloutName")
		@RequestParam
		String rolloutName,
		
		@ApiParam("initiatedBy")
		@RequestParam
		String initiatedBy) 
	throws 
		EntityDoesNotExistException, 
		ValidationException {
		
		this.rolloutService.initiateRollout(rolloutName, initiatedBy);
	}

	@ApiOperation(value = EVALUATE_ROLLOUT_STATE, notes = "Evaluates the rollout state for the specified rollout")
	@GetMapping(EVALUATE_ROLLOUT_STATE)
	public void evaluateRolloutState(
		@ApiParam("rolloutName")
		@RequestParam
		String rolloutName) 
	throws 
		EntityDoesNotExistException, 
		ValidationException, 
		TmcDeploymentException {
		
		this.rolloutService.evaluateRolloutState(rolloutName);
	}
	
	@ApiOperation(value = PAUSE_ROLLOUT, notes = "Pauses the specified rollout")
	@GetMapping(PAUSE_ROLLOUT)
	public void pauseRollout(
		@ApiParam("rolloutName")
		@RequestParam
		String rolloutName) 
	throws 
		EntityDoesNotExistException, 
		ValidationException {
		
		this.rolloutService.pauseRollout(rolloutName);
	}

	@ApiOperation(value = RESUME_ROLLOUT, notes = "Resumes the specified rollout")
	@GetMapping(RESUME_ROLLOUT)
	public void resumeRollout(
		@ApiParam("rolloutName")
		@RequestParam
		String rolloutName) 
	throws 
		EntityDoesNotExistException, 
		ValidationException {
		
		this.rolloutService.resumeRollout(rolloutName);
	}

	@ApiOperation(value = CANCEL_ROLLOUT, notes = "Cancels the specified rollout")
	@GetMapping(CANCEL_ROLLOUT)
	public void cancelRollout(
		@ApiParam("rolloutName")
		@RequestParam
		String rolloutName) 
	throws 
		EntityDoesNotExistException {
		
		this.rolloutService.cancelRollout(rolloutName);
	}

	@ApiOperation(value = GET_ROLLOUT_PROGRESS, notes = "Retrieves the rollout progress for the specified rollout")
	@GetMapping(GET_ROLLOUT_PROGRESS)
	public RolloutProgress getRolloutProgress(
		@ApiParam("rolloutName")
		@RequestParam
		String rolloutName)
	throws 
		EntityDoesNotExistException {
		
		return this.rolloutService.getRolloutProgress(rolloutName);
	}

	@ApiOperation(value = GET_VEHICLE_CAMPAIGN_BATCH_BY_VIN, notes = "Retrieves the vehicle campaign batch from the specified rollout for the specified VIN")
	@GetMapping(GET_VEHICLE_CAMPAIGN_BATCH_BY_VIN)
	public VehicleCampaignBatch getVehicleCampaignBatchByVin(
		@ApiParam("rolloutName, e.g. rollout1")
		@RequestParam
		String rolloutName,
		
		@ApiParam("vin, e.g. 1FA6P8TH4J5000000")
		@RequestParam
		String vin)
	throws 
		EntityDoesNotExistException {
		
		com.djt.cvpp.ota.flare.rollout.model.AbstractRollout rolloutEntity = this.rolloutService.getRolloutByName(rolloutName);
		com.djt.cvpp.ota.flare.rollout.model.VehicleCampaignBatch vehicleCampaignBatchEntity = rolloutEntity.getVehicleCampaignBatchVin(vin);
		if (vehicleCampaignBatchEntity != null) {
			return this.rolloutService.getDtoMapper().mapVehicleCampaignBatch(vehicleCampaignBatchEntity);	
		}
		return null;
	}

	@ApiOperation(value = GET_VEHICLE_CAMPAIGN_BATCH_BY_TMC_IDS, notes = "Retrieves the vehicle campaign batch from the specified rollout for the specified TMC deployment id and TMC vehicleId")
	@GetMapping(GET_VEHICLE_CAMPAIGN_BATCH_BY_TMC_IDS)
	public VehicleCampaignBatch getVehicleCampaignBatchByTmcIds(
		@ApiParam("rolloutName, e.g. rollout1")
		@RequestParam
		String rolloutName,
		
		@ApiParam("tmcDeploymentId")
		@RequestParam
		String tmcDeploymentId,
		
		@ApiParam("tmcVehicleId")
		@RequestParam
		String tmcVehicleId)
	throws 
		EntityDoesNotExistException {
		
		com.djt.cvpp.ota.flare.rollout.model.AbstractRollout rolloutEntity = this.rolloutService.getRolloutByName(rolloutName);
		com.djt.cvpp.ota.flare.rollout.model.VehicleCampaignBatch vehicleCampaignBatchEntity = rolloutEntity.getVehicleCampaignBatchByTmcVehicleId(tmcDeploymentId, tmcVehicleId);
		if (vehicleCampaignBatchEntity != null) {
			return this.rolloutService.getDtoMapper().mapVehicleCampaignBatch(vehicleCampaignBatchEntity);	
		}
		return null;
	}
	
	@ApiOperation(value = RUN_ROLLOUT_SIMULATION, notes = "Runs a rollout simulation with the specified values")
	@GetMapping(RUN_ROLLOUT_SIMULATION)
	public RolloutProgress runRolloutSimulation(
		@ApiParam("templateVin, e.g. 1FA6P8TH4J5000000")
		@RequestParam
		String templateVin,
		
		@ApiParam("number of vehicles to provision based on templateVin NOTE: Use a number that is a power of 10, then subtract 2, e.g. 9998 or 99998")
		@RequestParam
		String numberToProvision,

		@ApiParam("expectedPercentageSuccess, e.g. 0.5")
		@RequestParam
		double expectedPercentageSuccess,
		
		@ApiParam("expectedPercentageFailure, e.g. 0.4")
		@RequestParam
		double expectedPercentageFailure,
		
		@ApiParam("expectedPercentageOffline, e.g. 0.1")
		@RequestParam
		double expectedPercentageOffline,
		
		@ApiParam("rolloutName, e.g. rollout1")
		@RequestParam
		String rolloutName,
		
		@ApiParam("owner, e.g. tmyers28")
		@RequestParam
		String owner,

		@ApiParam("initiatedBy, e.g. tmyers28")
		@RequestParam
		String initiatedBy,
		
		@ApiParam("deliveryRuleSetName, e.g. deliveryRuleSet_MayHackathon-ECG")
		@RequestParam
		String deliveryRuleSetName) 
	throws 
		EntityDoesNotExistException, 
		EntityAlreadyExistsException, 
		ValidationException,
		TmcByteStreamException,
		VadrException,
		TmcResourceException,
		TmcDeploymentException {

		// Set the test timekeeper so that we can "fast forward" through time
		String env = System.getProperty(TimeKeeper.ENV);
		if (env == null) {
			System.setProperty(TimeKeeper.ENV, TimeKeeper.TEST);
		}
		AbstractEntity.setTimeKeeper(new TestTimeKeeperImpl(TestTimeKeeperImpl.DEFAULT_TEST_EPOCH_MILLIS_01_01_2020));
		
		// Let's "spin up" 10000 virtual vehicles, based on the specified VIN vehicle template
		TmcVehicle djtMustang2018VehicleTemplate = this.tmcVehicleClient.getVehicleByVin(templateVin);
		
		// 10,000 vehicles takes 6 seconds.  100,000 vehicles takes about 10 minutes (with heap size increased to 2GB)
		this.tmcVehicleClient.provisionVirtualVehiclesFromTemplateVehicle(djtMustang2018VehicleTemplate, Integer.parseInt(numberToProvision));
		
		// Let's set the expectations for vehicle behavior (the numbers must all add up to 1.0)
		TmcVehicleSimulator.setVehicleBehaviorExpectations(
			expectedPercentageSuccess, 
			expectedPercentageFailure, 
			expectedPercentageOffline);


		
		this.rolloutService.createSoftwareUpdateRollout(rolloutName, deliveryRuleSetName);
		com.djt.cvpp.ota.flare.rollout.model.AbstractRollout rollout = this.rolloutService.getRolloutByName(rolloutName);

		
		
		// We can't initiate a rollout unless we have done pre-processing (which creates the rollout tree, 
		// as well as generating manifests/signed commands, i.e. performPreProcessing() and 
		// uploading of release artifacts to TMC first, i.e. uploadReleaseArtifactsToTmcByteStream().
		
		
		// TODO: TDM: Fix this
		com.djt.cvpp.ota.orfin.delivery.mapper.dto.DeliveryRuleSet deliveryRuleSetDto = this.deliveryRuleSetApplicationServiceClient.getDeliveryRuleSetByName(deliveryRuleSetName);
		this.rolloutService.performPreProcessing(rolloutName, deliveryRuleSetDto);
		
		this.rolloutService.evaluateRolloutState(rolloutName);
		this.rolloutService.uploadGeneratedContentToTmcByteStream(rolloutName);
		this.rolloutService.evaluateRolloutState(rolloutName);
		this.rolloutService.initiateRollout(rolloutName, initiatedBy);
		

		
		// Step forward in time and let the model "play out"
		Timestamp endDate = rollout.getEndDate();
		Timestamp currentDate = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
		while (currentDate.before(endDate) || currentDate.equals(endDate)) {
			
			this.rolloutService.evaluateRolloutState(rolloutName);
			
			AbstractEntity.getTimeKeeper().setCurrentTimeInMillisForwardByOneDay();
			currentDate = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
		}
		rollout.assertValid();
		
		// For large rollouts, this becomes very time consuming
		if (Integer.parseInt(numberToProvision) <= 100) {
 
			String json = this.rolloutService.getJsonConverter().marshallFromEntityToJson(rollout);
			LOGGER.debug(json);
		}
		
		// TODO: TDM
		// Add any method that is invoked on the entity to be on the service interface.
		// Make service extend entity service so that it has the update entity method (same goes for repository interface)
		int totalNumberOfCampaigns = rollout.getTotalNumberOfCampaigns();
		int totalNumberofBatches = rollout.getTotalNumberOfBatches();
		int totalNumberOfVehicles = rollout.getTotalNumberOfVehicles();
		int totalNumberOfVehicleMessages = rollout.getTotalNumberOfVehicleMessages();
		
		RolloutState rolloutState = rollout.getRolloutState();
		int numberComplete = rollout.getNumberComplete();
		int numberSuccessful = rollout.getNumberSuccessful();
		int numberFailure = rollout.getNumberFailure();
		int numberIncomplete = rollout.getNumberIncomplete();
		
		double percentageComplete = rollout.getPercentageComplete();
		double percentageSuccessful = rollout.getPercentageSuccessful();
		double percentageFailure = rollout.getPercentageFailure();
		
		RolloutProgress rolloutProgress = new RolloutProgress();
		rolloutProgress.setTotalNumberOfCampaigns(totalNumberOfCampaigns);
		rolloutProgress.setTotalNumberofBatches(totalNumberofBatches);
		rolloutProgress.setTotalNumberOfVehicles(totalNumberOfVehicles);
		rolloutProgress.setTotalNumberOfVehicleMessages(totalNumberOfVehicleMessages);
		rolloutProgress.setRolloutState(rolloutState);
		rolloutProgress.setNumberComplete(numberComplete);
		rolloutProgress.setNumberSuccessful(numberSuccessful);
		rolloutProgress.setNumberFailure(numberFailure);
		rolloutProgress.setNumberIncomplete(numberIncomplete);
		rolloutProgress.setPercentageComplete(percentageComplete);
		rolloutProgress.setPercentageFailure(percentageFailure);
		rolloutProgress.setPercentageSuccessful(percentageSuccessful);
		return rolloutProgress;
	}
}
