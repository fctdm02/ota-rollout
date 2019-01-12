/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.djt.cvpp.ota.flare.app.rollout.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.djt.cvpp.ota.common.exception.EntityAlreadyExistsException;
import com.djt.cvpp.ota.common.exception.EntityDoesNotExistException;
import com.djt.cvpp.ota.common.exception.ValidationException;
import com.djt.cvpp.ota.common.model.AbstractEntity;
import com.djt.cvpp.ota.flare.rollout.model.AbstractRollout;
import com.djt.cvpp.ota.flare.rollout.repository.RolloutRepository;

/**
 *
 * @author tmyers1@yahoo.com (Tom Myers)
 *
 */
@Repository
public class RolloutRepositoryHibernateImpl implements RolloutRepository {
	
	@PersistenceContext
	private EntityManager entityManager;	
	
	public void saveRollout(AbstractRollout rollout)
	throws 
		EntityAlreadyExistsException,
		ValidationException {
		throw new RuntimeException("Not implemented yet.");
	}
	
	public List<AbstractRollout> getAllRollouts() {
		
		return this.entityManager.createQuery("select r from rollout r", AbstractRollout.class).getResultList();
	}
	
	public AbstractRollout getRolloutByName(
        String rolloutName)
	throws 
		EntityDoesNotExistException {

		throw new RuntimeException("Not implemented yet.");
	}
	
	public AbstractEntity getEntityByNaturalIdentityNullIfNotFound(String naturalIdentity) {
		
		throw new RuntimeException("Not implemented yet.");
	}
	
	public AbstractEntity updateEntity(
		AbstractEntity entity)
	throws 
		ValidationException {

		throw new RuntimeException("Not implemented yet.");
	}
	
	public AbstractEntity deleteEntity(AbstractEntity entity) {

		throw new RuntimeException("Not implemented yet.");
	}
	
	public void reset() {
		throw new UnsupportedOperationException("Not supported");
	}
}
